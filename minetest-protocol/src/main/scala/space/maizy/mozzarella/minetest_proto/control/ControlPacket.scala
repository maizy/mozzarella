package space.maizy.mozzarella.minetest_proto.control

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import space.maizy.mozzarella.minetest_proto.Packet
import space.maizy.mozzarella.minetest_proto.data.ControlType
import space.maizy.mozzarella.minetest_proto.data.PacketType

/**
 * https://github.com/minetest/minetest/blob/5.0.1/src/network/connection.h
 * https://github.com/minetest/minetest/blob/5.0.1/src/network/connectionthreads.cpp
 */
final case class ControlPacket(controlType: ControlType.Type, payload: ControlPayload) extends Packet {

  override val packetType: PacketType.Type = PacketType.Control

  override def toString: String = {
    val payloadStr = payload match {
      case EmptyPayload => ""
      case _ => ": " + payload.toString
    }
    s"Control($controlType$payloadStr)"
  }
}
