package space.maizy.mozzarella.minetest_proto.codecs.reliable

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.Codec
import scodec.codecs._
import space.maizy.mozzarella.minetest_proto.reliable.ReliablePacket

/**
 * TODO: parse encapsulated packet
 */
object ReliablePacketCodec {
    implicit val reliablePacketCodec: Codec[ReliablePacket] = {
      ("seqnum" | uint16 ) ::
      ("packet" | bytes )
    }.as[ReliablePacket]
}
