package space.maizy.mozzarella.minetest_proto

import scodec.bits.ByteVector
import space.maizy.mozzarella.minetest_proto.data.PacketType
import space.maizy.mozzarella.minetest_proto.data.PacketType.Type

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */


/**
 * https://github.com/minetest/minetest/blob/master/src/network/connection.cpp, makeSplitPacket
 */
final case class SplitPacket(seqNum: Int, chunkCound: Int, chunkNum: Int, data: ByteVector) extends Packet {
  override val packetType: Type = PacketType.Split
}
