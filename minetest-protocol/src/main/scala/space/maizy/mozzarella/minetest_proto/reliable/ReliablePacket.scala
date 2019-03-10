package space.maizy.mozzarella.minetest_proto.reliable

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
 * https://github.com/minetest/minetest/blob/master/src/network/connection.cpp, makeReliablePacket
 *
 * @param seqNum u16
 */
final case class ReliablePacket(seqNum: Int, packet: ByteVector) extends Packet {
  override val packetType: Type = PacketType.Reliable

  override def toString: String = s"ReliablePacket(#$seqNum, ${Printer.printByteVector(packet)})"
}
