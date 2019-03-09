package space.maizy.mozzarella.minetest_proto

import scodec.bits.ByteVector
import space.maizy.mozzarella.minetest_proto.data.MagicNumbers

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

/**
 * https://dev.minetest.net/Network_Protocol
 */
final case class RawPacket(protocolVersion: Long, peerId: Int, channel: Int, data: ByteVector) {
  override def toString: String = {
    val proto = if (protocolVersion != MagicNumbers.protocolVersionInt) {
      s"!proto:$protocolVersion, "
    } else {
      ""
    }
    s"RawPacket(${proto}peer=$peerId, ch=$channel: ${data.toHex.replaceAll("(.{2})", "$1 ")})"
  }
}
