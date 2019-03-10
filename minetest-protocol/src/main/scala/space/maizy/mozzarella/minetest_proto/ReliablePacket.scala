package space.maizy.mozzarella.minetest_proto

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.bits.ByteVector
import space.maizy.mozzarella.minetest_proto.data.PacketType
import space.maizy.mozzarella.minetest_proto.data.PacketType.Type

/**
 * https://github.com/minetest/minetest/blob/master/src/network/connection.cpp, makeReliablePacket
 */
final case class ReliablePacket(seqNum: Int, data: ByteVector) extends Packet {
  override val packetType: Type = PacketType.Reliable
}
