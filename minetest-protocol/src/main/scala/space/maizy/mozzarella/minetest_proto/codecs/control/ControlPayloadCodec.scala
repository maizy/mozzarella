package space.maizy.mozzarella.minetest_proto.codecs.control

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec._
import scodec.codecs._
import space.maizy.mozzarella.minetest_proto.control.{ ControlAck, ControlSetPeerId, EmptyPayload }

object ControlPayloadCodec {

  val controlAckPayloadCodec: Codec[ControlAck] =
    ("seq_num" | uint16).as[ControlAck]

  val controlSetPeerIdCodec: Codec[ControlSetPeerId] =
    ("new_peer_id" | uint16).as[ControlSetPeerId]

  val emptyPayloadCodec: Codec[EmptyPayload.type] = provide(EmptyPayload)

}
