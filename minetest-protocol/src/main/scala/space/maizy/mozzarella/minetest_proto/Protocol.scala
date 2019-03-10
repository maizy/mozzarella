package space.maizy.mozzarella.minetest_proto

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.{ Attempt, Codec, DecodeResult, Err }
import scodec.bits.BitVector
import space.maizy.mozzarella.minetest_proto.control.ControlPacket
import space.maizy.mozzarella.minetest_proto.data.{ MagicNumbers, PacketType }
import space.maizy.mozzarella.minetest_proto.original.OriginalPacket
import space.maizy.mozzarella.minetest_proto.reliable.ReliablePacket
import space.maizy.mozzarella.minetest_proto.split.SplitPacket

object Protocol {

  import space.maizy.mozzarella.minetest_proto.codecs.RawPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.control.ControlPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.split.SplitPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.reliable.ReliablePacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.original.OriginalPacketCodec._

  def parse(bits: BitVector): Attempt[DecodeResult[ParsedPacket]] = {
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

        packetAttempt.map(res => res.map(p => ParsedPacket(rawPacket.channel, rawPacket.peerId, p)))
      } else {
        Attempt.Failure(Err(s"unsupported procol version ${rawPacket.protocolVersion}"))
      }
    }
  }

  def parseRawPacket(bits: BitVector): Attempt[DecodeResult[RawPacket]] =
    Codec.decode[RawPacket](bits)
}
