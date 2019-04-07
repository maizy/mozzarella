package space.maizy.mozzarella.minetest_proto.codecs.control

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.Codec
import scodec.bits.BitVector
import space.maizy.mozzarella.minetest_proto.control.{ ControlAck, ControlPacket, ControlSetPeerId, EmptyPayload }
import space.maizy.mozzarella.minetest_proto.data.ControlType
import space.maizy.mozzarella.minetest_proto.utils.DecoderSyntax._
import space.maizy.mozzarella.minetest_proto.utils.EncoderSyntax._

object ControlPacketCodec {

  import ControlPayloadCodec._
  import ControlTypeCodec._

  implicit val controlPacketCodec: Codec[ControlPacket] = Codec[ControlPacket](
    (c: ControlPacket) => {
      controlTypeCodec.encode(c.controlType).append {
        c.payload match {
          case p: ControlAck => controlAckPayloadCodec.encode(p)
          case p: ControlSetPeerId => controlSetPeerIdCodec.encode(p)
          case EmptyPayload => emptyPayloadCodec.encode(EmptyPayload)
        }
      }
    },
    (bits: BitVector) => {
      controlTypeCodec.decode(bits)
        .decodeRemaining { (controlType, remainder) =>
          controlType match {
            case ControlType.Ack =>
              controlAckPayloadCodec.decode(remainder)
            case ControlType.SetPeerId =>
              controlSetPeerIdCodec.decode(remainder)
            case ControlType.Ping =>
              emptyPayloadCodec.decode(remainder)
            case ControlType.Disconnect =>
              emptyPayloadCodec.decode(remainder)
          }
        }
        .combine { (controlType, payload) =>
          ControlPacket(controlType, payload)
        }
    }
  )

}
