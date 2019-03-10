package space.maizy.mozzarella.minetest_proto

import scodec.bits.ByteVector
import space.maizy.mozzarella.minetest_proto.data.{ ControlType, PacketType }
import space.maizy.mozzarella.minetest_proto.data.PacketType.Type
/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */


/**
 * https://github.com/minetest/minetest/blob/master/src/network/connection.h
 * https://github.com/minetest/minetest/blob/master/src/network/connectionthreads.cpp
 */
final case class ControlPacket(controlType: ControlType.Type, data: ByteVector) extends Packet {
  override val packetType: Type = PacketType.Control
}
