package space.maizy.mozzarella.minetest_proto

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import cats.syntax.either._
import scodec.bits.BitVector
import scodec.{ Attempt, DecodeResult, Err }
import space.maizy.mozzarella.minetest_proto.codecs.original._
import space.maizy.mozzarella.minetest_proto.control.ControlPacket
import space.maizy.mozzarella.minetest_proto.data.{ MagicNumbers, PacketType }
import space.maizy.mozzarella.minetest_proto.original._
import space.maizy.mozzarella.minetest_proto.reliable.ReliablePacket
import space.maizy.mozzarella.minetest_proto.split.SplitPacket

object Protocol {

  import space.maizy.mozzarella.minetest_proto.codecs.RawPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.control.ControlPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.original.OriginalPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.original.ToClientOriginalPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.original.ToServerOriginalPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.reliable.ReliablePacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.split.SplitPacketCodec._

  def decode(bits: BitVector): Either[List[String], PacketOnWire] = {
    parseRawPacket(bits).flatMap { rawResult =>
      val rawPacket = rawResult.value
      if (rawPacket.protocolVersion == MagicNumbers.protocolVersionInt) {
        val packetAttempt = rawPacket.packetType match {
          case PacketType.Control => controlPacketCodec.decode(rawPacket.data.bits)
          case PacketType.Reliable => reliablePacketCodec.decode(rawPacket.data.bits)
          case PacketType.Original => originalPacketCodec.decode(rawPacket.data.bits)
          case PacketType.Split => splitPacketCodec.decode(rawPacket.data.bits)
          case other: PacketType.Type => Attempt.Failure(Err(s"unknown packet type $other"))
        }
        packetAttempt.flatMap {
          case DecodeResult(_, remainder) if remainder.nonEmpty =>
            Attempt.Failure(Err(s"have ${remainder.length} unconsumed bits after payload parsing"))
          case DecodeResult(packet, _) =>
            Attempt.Successful(PacketOnWire(rawPacket.channel, rawPacket.peerId, packet))
        }
      } else {
        Attempt.Failure(Err(s"unsupported procol version ${rawPacket.protocolVersion}"))
      }
    }.toEither.left.map(err => List(err.messageWithContext))
  }

  private def decodeAndReplaceOriginalPacket(
      bits: BitVector,
      replaceF: OriginalPacket => Either[List[String], OriginalPacketWithKnownDirection])
      : Either[List[String], PacketOnWire] = {
    decode(bits).flatMap {
      case parsed @ PacketOnWire(_, _, orig: OriginalPacket) =>
        replaceF(orig).map { withDirection =>
          parsed.copy(packet = withDirection)
        }
      case parsed @ PacketOnWire(_, _, reliable @ ReliablePacket(_, _, orig: OriginalPacket)) =>
        replaceF(orig).map { withDirection =>
          parsed.copy(packet = reliable.copy(encapsulatedPacket = withDirection))
        }
      case other: PacketOnWire => other.asRight
    }
  }

  def decodeToClient(bits: BitVector): Either[List[String], PacketOnWire] =
    decodeAndReplaceOriginalPacket(bits, ToClientOriginalPacketCodec.decode)

  def decodeToServer(bits: BitVector): Either[List[String], PacketOnWire] =
    decodeAndReplaceOriginalPacket(bits, ToServerOriginalPacketCodec.decode)

  def encode(packetOnWire: PacketOnWire): Either[List[String], BitVector] = {
    val payloadRes = packetOnWire.packet match {
      case c: ControlPacket => controlPacketCodec.encode(c)
      case c: ReliablePacket => reliablePacketCodec.encode(c)
      case c: OriginalPacket => originalPacketCodec.encode(c)
      case c: ToServerOriginalPacket => toServerOriginalPacketEncoder.encode(c)
      case c: ToClientOriginalPacket => toClientOriginalPacketEncoder.encode(c)
      case c: SplitPacket => splitPacketCodec.encode(c)
      case other: Packet => Attempt.Failure(Err(s"unsupported packet type ${other.packetType}"))
    }

    payloadRes.flatMap { payloadBits =>
      if (payloadBits.length % 8 == 0) {
        val rawPacket = RawPacket(
          MagicNumbers.protocolVersionInt.toLong,
          packetOnWire.peerId,
          packetOnWire.channel,
          packetOnWire.packet.packetType,
          payloadBits.toByteVector
        )
        rawPacketCodec.encode(rawPacket)
      } else {
        Attempt.Failure(Err("packet payload not aligned to bytes"))
      }
    }.toEither.left.map(err => List(err.messageWithContext))
  }

  private def parseRawPacket(bits: BitVector): Attempt[DecodeResult[RawPacket]] =
    rawPacketCodec.decode(bits)
}
