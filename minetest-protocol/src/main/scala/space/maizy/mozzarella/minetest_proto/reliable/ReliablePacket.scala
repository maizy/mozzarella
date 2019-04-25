package space.maizy.mozzarella.minetest_proto.reliable

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import space.maizy.mozzarella.minetest_proto.Packet
import space.maizy.mozzarella.minetest_proto.data.PacketType
import space.maizy.mozzarella.minetest_proto.data.PacketType.Type

/**
 * https://github.com/minetest/minetest/blob/5.0.1/src/network/connection.cpp, makeReliablePacket
 *
 * @param seqNum u16
 */
final case class ReliablePacket(
    seqNum: Int,
    encapsulatedType: PacketType.Type,
    encapsulatedPacket: Packet) extends Packet {
  override def toString: String = s"ReliablePacket(#$seqNum: $encapsulatedPacket)"
  override val packetType: Type = PacketType.Reliable
}
