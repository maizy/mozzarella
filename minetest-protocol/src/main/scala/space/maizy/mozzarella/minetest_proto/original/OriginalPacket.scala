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
 * https://github.com/minetest/minetest/blob/5.0.1/src/network/connection.cpp, makeOriginalPacket
 */
final case class OriginalPacket(commandCode: Int, payload: ByteVector) extends Packet {
  override val packetType: Type = PacketType.Original

  override def toString: String = s"OriginalPacket(${Printer.asHex(commandCode)}: " +
    s"${Printer.byteVectorToString(payload)})"
}
