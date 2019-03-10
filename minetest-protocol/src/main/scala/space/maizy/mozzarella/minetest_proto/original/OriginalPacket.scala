package space.maizy.mozzarella.minetest_proto.original

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
 * https://github.com/minetest/minetest/blob/master/src/network/connection.cpp, makeOriginalPacket
 *
 * TODO: parse data
 */
final case class OriginalPacket(data: ByteVector) extends Packet {
  override val packetType: Type = PacketType.Original

  override def toString: String =
    s"OriginalPacket(${Printer.printByteVector(data)})"
}
