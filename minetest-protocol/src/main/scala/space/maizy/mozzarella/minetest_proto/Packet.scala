package space.maizy.mozzarella.minetest_proto

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import space.maizy.mozzarella.minetest_proto.data.PacketType

trait Packet {
  def packetType: PacketType.Type
}
