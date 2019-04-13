package space.maizy.mozzarella.minetest_proto

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */
final case class PacketOnWire(channel: Int, peerId: Int, packet: Packet) {
  override def toString: String = s"PacketOnWire($shortDescription)"
  def shortDescription: String = {
    val peerStr = if (peerId == 1) {
      "server"
    } else if (peerId == 0) {
      "unassigned"
    } else {
      peerId.toString.padTo(6, ' ')
    }

    s"ch:$channel, peer:$peerStr, $packet"
  }
}
