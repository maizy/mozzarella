package space.maizy.mozzarella.minetest_proto

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.{ Attempt, Codec, DecodeResult, Err }
import scodec.bits.BitVector
import space.maizy.mozzarella.minetest_proto.control.ControlPacket
import space.maizy.mozzarella.minetest_proto.data.{ ToClientCommand, MagicNumbers, PacketType, ToServerCommand }
import space.maizy.mozzarella.minetest_proto.original.{ OriginalPacket, OriginalPacketWithKnownDirection, ToClientOriginalPacket, ToClientUnsupportedPacket, ToServerOriginalPacket, ToServerUnsupportedPacket }
import space.maizy.mozzarella.minetest_proto.reliable.ReliablePacket
import space.maizy.mozzarella.minetest_proto.split.SplitPacket
import cats.syntax.either._

object Protocol {

  import space.maizy.mozzarella.minetest_proto.codecs.RawPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.control.ControlPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.split.SplitPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.reliable.ReliablePacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.original.OriginalPacketCodec._

  def parse(bits: BitVector): Either[List[String], ParsedPacket] = {

    // TODO: check that all bits are consumed (reminder is empty)

    parseRawPacket(bits).flatMap { rawResult =>
      val rawPacket = rawResult.value
      if (rawPacket.protocolVersion == MagicNumbers.protocolVersionInt) {
        val packetAttempt = rawPacket.packetType match {
          case PacketType.Control => Codec.decode[ControlPacket](rawPacket.data.bits)
          case PacketType.Reliable => Codec.decode[ReliablePacket](rawPacket.data.bits)
          case PacketType.Original => Codec.decode[OriginalPacket](rawPacket.data.bits)
          case PacketType.Split => Codec.decode[SplitPacket](rawPacket.data.bits)
          case other => Attempt.Failure(Err(s"unknown packet type $other"))
        }

        packetAttempt.map(res => ParsedPacket(rawPacket.channel, rawPacket.peerId, res.value))
      } else {
        Attempt.Failure(Err(s"unsupported procol version ${rawPacket.protocolVersion}"))
      }
    }.toEither.left.map(err => List(err.messageWithContext))
  }

  private def parseAndReplaceOriginalPacket(
      bits: BitVector,
      replaceF: OriginalPacket => Either[List[String], OriginalPacketWithKnownDirection])
      : Either[List[String], ParsedPacket] = {
    parse(bits).flatMap {
      case parsed @ ParsedPacket(_, _, orig: OriginalPacket) =>
        replaceF(orig).map { withDirection =>
          parsed.copy(packet = withDirection)
        }
      case parsed @ ParsedPacket(_, _, reliable @ ReliablePacket(_, _, orig: OriginalPacket)) =>
        replaceF(orig).map { withDirection =>
          parsed.copy(packet = reliable.copy(encapsulatedPacket = withDirection))
        }
      case other => other.asRight
    }
  }

  def parseToClient(bits: BitVector): Either[List[String], ParsedPacket] =
    parseAndReplaceOriginalPacket(bits, parseToClientOriginalPacket)

  def parseToServer(bits: BitVector): Either[List[String], ParsedPacket] =
    parseAndReplaceOriginalPacket(bits, parseToServerOriginalPacket)

  private def parseToClientOriginalPacket(packet: OriginalPacket): Either[List[String], ToClientOriginalPacket] = {
    ToClientCommand.index.get(packet.commandCode) match {
      case Some(command) =>
        // TODO: parse to client original packet
        ToClientUnsupportedPacket(command, packet.payload).asRight
      case None =>
        List(s"unknown client command code in $packet").asLeft
    }
  }

  private def parseToServerOriginalPacket(packet: OriginalPacket): Either[List[String], ToServerOriginalPacket] = {
    ToServerCommand.index.get(packet.commandCode) match {
      case Some(command) =>
        // TODO: parse to server original packet
        ToServerUnsupportedPacket(command, packet.payload).asRight
      case None =>
        List(s"unknown server command code in $packet").asLeft
    }
  }

  private def parseRawPacket(bits: BitVector): Attempt[DecodeResult[RawPacket]] =
    Codec.decode[RawPacket](bits)
}
