package space.maizy.mozzarella.minetest_proto.codecs.reliable

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.{ Attempt, Codec, DecodeResult, Err }
import scodec.bits.BitVector
import scodec.codecs._
import space.maizy.mozzarella.minetest_proto.codecs.PacketTypeCodec
import space.maizy.mozzarella.minetest_proto.control.ControlPacket
import space.maizy.mozzarella.minetest_proto.reliable.ReliablePacket
import space.maizy.mozzarella.minetest_proto.data.PacketType
import space.maizy.mozzarella.minetest_proto.original.OriginalPacket
import space.maizy.mozzarella.minetest_proto.split.SplitPacket

/**
 * TODO: parse encapsulated packet
 */
object ReliablePacketCodec {

  import space.maizy.mozzarella.minetest_proto.codecs.control.ControlPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.split.SplitPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.original.OriginalPacketCodec._

  implicit val reliablePacketCodec: Codec[ReliablePacket] = Codec[ReliablePacket](
    (c: ReliablePacket) => ???,
    (bits: BitVector) => {
      {
        ("seqnum" | uint16) ::
        ("packet_type" | PacketTypeCodec.packetTypeCodec )
      }.as[(Int, PacketType.Type)].decode(bits).flatMap{ case DecodeResult((seqNum, packetType), remainder) =>
        val encapsulatedPacketAttempt = packetType match {
          case PacketType.Control => Codec.decode[ControlPacket](remainder)
          case PacketType.Original => Codec.decode[OriginalPacket](remainder)
          case PacketType.Split => Codec.decode[SplitPacket](remainder)
          case _ => Attempt.Failure(Err(s"unexpected encapsulated packet $packetType"))
        }

        encapsulatedPacketAttempt
          .map{ _.map { encPacket =>
            ReliablePacket(seqNum, packetType, encPacket)
          }}
          .mapErr { _.pushContext("encapsulated packet") }
      }


    }
  )
}
