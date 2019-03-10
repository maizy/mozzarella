package space.maizy.mozzarella.minetest_proto

import scodec.bits.ByteVector
import space.maizy.mozzarella.minetest_proto.data.PacketType
import space.maizy.mozzarella.minetest_proto.data.PacketType.Type

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */


/**
 * https://github.com/minetest/minetest/blob/master/src/network/connection.cpp, makeOriginalPacket
 */
final case class OriginalPacket(data: ByteVector) extends Packet {
  override val packetType: Type = PacketType.Original
}
