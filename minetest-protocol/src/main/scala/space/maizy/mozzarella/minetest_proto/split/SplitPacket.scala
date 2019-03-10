package space.maizy.mozzarella.minetest_proto.split

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.bits.ByteVector
import space.maizy.mozzarella.minetest_proto.Packet
import space.maizy.mozzarella.minetest_proto.data.PacketType
import space.maizy.mozzarella.minetest_proto.data.PacketType.Type
import space.maizy.mozzarella.minetest_proto.utils.Printer

/**
 * https://github.com/minetest/minetest/blob/master/src/network/connection.cpp, makeSplitPacket
 *
 * TODO: parse data
 *
 * @param seqNum u16
 * @param chunkCount u16
 * @param chunkNum u16
 */
final case class SplitPacket(seqNum: Int, chunkCount: Int, chunkNum: Int, data: ByteVector) extends Packet {
  override val packetType: Type = PacketType.Split

  override def toString: String =
    s"SplitPacket(#$seqNum, $chunkNum of $chunkCount, ${Printer.printByteVector(data)})"
}
