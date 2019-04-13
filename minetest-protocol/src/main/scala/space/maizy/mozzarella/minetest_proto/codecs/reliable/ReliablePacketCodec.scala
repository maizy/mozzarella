package space.maizy.mozzarella.minetest_proto.codecs.reliable

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.bits.BitVector
import scodec.codecs._
import scodec.{ Attempt, Codec, Err }
import space.maizy.mozzarella.minetest_proto.Packet
import space.maizy.mozzarella.minetest_proto.codecs.PacketTypeCodec
import space.maizy.mozzarella.minetest_proto.control.ControlPacket
import space.maizy.mozzarella.minetest_proto.data.PacketType
import space.maizy.mozzarella.minetest_proto.original.{ OriginalPacket, ToClientOriginalPacket, ToServerOriginalPacket }
import space.maizy.mozzarella.minetest_proto.reliable.ReliablePacket
import space.maizy.mozzarella.minetest_proto.split.SplitPacket
import space.maizy.mozzarella.minetest_proto.utils.DecoderSyntax._
import space.maizy.mozzarella.minetest_proto.utils.EncoderSyntax._

/**
 * TODO: parse encapsulated packet
 */
object ReliablePacketCodec {
  import space.maizy.mozzarella.minetest_proto.codecs.control.ControlPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.original.OriginalPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.original.ToClientOriginalPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.original.ToServerOriginalPacketCodec._
  import space.maizy.mozzarella.minetest_proto.codecs.split.SplitPacketCodec._

  private val headerCodec = {
    ("seqnum" | uint16) ::
    ("packet_type" | PacketTypeCodec.packetTypeCodec)
  }.as[(Int, PacketType.Type)]

  implicit val reliablePacketCodec: Codec[ReliablePacket] = Codec[ReliablePacket](

    (packet: ReliablePacket) =>
      headerCodec.encode((packet.seqNum, packet.encapsulatedType))
        .append {
          packet.encapsulatedPacket match {
            case enc: ControlPacket => controlPacketCodec.encode(enc)
            case enc: OriginalPacket => originalPacketCodec.encode(enc)
            case enc: ToServerOriginalPacket => toServerOriginalPacketEncoder.encode(enc)
            case enc: ToClientOriginalPacket => toClientOriginalPacketEncoder.encode(enc)
            case enc: SplitPacket => splitPacketCodec.encode(enc)
            case other: Packet => Attempt.Failure(Err(
              s"unexpected encapsulated packet ${other.packetType} (${other.getClass.getName}})"
            ))
          }
        },

    (bits: BitVector) => {
      headerCodec.decode(bits)
        .decodeRemaining { case ((_, encapsulatedPacketType), remainder) =>
          encapsulatedPacketType match {
            case PacketType.Control => controlPacketCodec.decode(remainder)
            // decoded as original packet with unknown direction, see Protocol#decodeAndReplaceOriginalPacket
            case PacketType.Original => originalPacketCodec.decode(remainder)
            case PacketType.Split => splitPacketCodec.decode(remainder)
            case _ => Attempt.Failure(Err(s"unexpected encapsulated packet $encapsulatedPacketType"))
          }
        }
        .mapErr { _.pushContext("encapsulated packet") }
        .combine { case ((seqNum, packetType), encPacket) =>
          ReliablePacket(seqNum, packetType, encPacket)
        }


    }
  )
}
