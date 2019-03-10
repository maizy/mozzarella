package space.maizy.mozzarella.minetest_proto.codecs

import scodec.Codec
import scodec.codecs._
import space.maizy.mozzarella.minetest_proto.RawPacket

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

/**
 * https://dev.minetest.net/Network_Protocol
 * https://github.com/minetest/minetest/blob/master/doc/protocol.txt
 * https://github.com/minetest/minetest/blob/master/src/network/connection.cpp
 */
object RawPacketCodec {
  implicit val rawPacketCodec: Codec[RawPacket] = {
    ("proto_version" | uint32) ::
    ("peer_id" | uint16 ) ::
    ("channel" | uint8 ) ::
    ("packet_type" | PacketTypeCodec.packetTypeCodec ) ::
    ("data" | bytes )
  }.as[RawPacket]
}
