package space.maizy.mozzarella.minetest_proto

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.bits.ByteVector
import space.maizy.mozzarella.minetest_proto.data.MagicNumbers
import space.maizy.mozzarella.minetest_proto.data.PacketType

/**
 * https://dev.minetest.net/Network_Protocol
 */
final case class RawPacket(
    protocolVersion: Long,
    peerId: Int,
    channel: Int,
    packetType: PacketType.Type,
    data: ByteVector) {

  override def toString: String = {
    val proto = if (protocolVersion != MagicNumbers.protocolVersionInt) {
      s"!proto:$protocolVersion, "
    } else {
      ""
    }
    s"RawPacket($packetType, ${proto}peer=$peerId, ch=$channel: ${data.toHex.replaceAll("(.{2})", "$1 ")})"
  }
}
