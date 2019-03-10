package space.maizy.mozzarella.minetest_proto.codecs.split

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.codecs._
import scodec.Codec
import space.maizy.mozzarella.minetest_proto.split.SplitPacket

/**
 * TODO: parse payload
 */
object SplitPacketCodec {
    implicit val splitPacketCodec: Codec[SplitPacket] = {
      ("seqnum" | uint16 ) ::
      ("chunk_count" | uint16 ) ::
      ("chunk_num" | uint16 ) ::
      ("payload" | bytes )
    }.as[SplitPacket]
}
