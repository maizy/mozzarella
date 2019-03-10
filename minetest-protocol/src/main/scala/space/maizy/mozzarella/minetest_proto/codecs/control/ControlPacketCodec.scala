package space.maizy.mozzarella.minetest_proto.codecs.control

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.bits.BitVector
import scodec.{ Attempt, Codec, DecodeResult }
import space.maizy.mozzarella.minetest_proto.control.{ ControlPacket, ControlPayload }
import space.maizy.mozzarella.minetest_proto.data.ControlType

object ControlPacketCodec {

  import ControlPayloadCodec._
  import ControlTypeCodec._

  implicit val controlPacketCodec: Codec[ControlPacket] = Codec[ControlPacket](
    (c: ControlPacket) => ???,
    (bits: BitVector) => {
      controlTypeCodec.decode(bits).flatMap{ case DecodeResult(controlType, remainder) =>
        val payloadAttempt: Attempt[DecodeResult[ControlPayload]] = controlType match {
          case ControlType.Ack =>
            controlAckPayloadCodec.decode(remainder)
          case ControlType.SetPeerId =>
            controlSetPeerIdCodec.decode(remainder)
          case ControlType.Ping =>
            emptyPayloadCodec.decode(remainder)
          case ControlType.Disconnect =>
            emptyPayloadCodec.decode(remainder)
        }
        payloadAttempt.map(p => p.map(ControlPacket(controlType, _)))
      }
    }
  )

}
