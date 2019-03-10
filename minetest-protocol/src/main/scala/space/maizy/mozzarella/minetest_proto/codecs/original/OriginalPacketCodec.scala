package space.maizy.mozzarella.minetest_proto.codecs.original

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.Codec
import scodec.codecs._
import space.maizy.mozzarella.minetest_proto.original.OriginalPacket

/**
 * TODO: parse payload
 */
object OriginalPacketCodec {
    implicit val originalPacketCodec: Codec[OriginalPacket] =
      ("payload" | bytes ).as[OriginalPacket]
}
