package space.maizy.mozzarella.minetest_proto.control

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

sealed trait ControlPayload

/**
 * https://github.com/minetest/minetest/blob/master/src/network/connection.h
 *
 * @param seqNum u16
 */
final case class ControlAck(seqNum: Int) extends ControlPayload {
  override def toString: String = s"#$seqNum"
}

/**
 * https://github.com/minetest/minetest/blob/master/src/network/connection.h
 *
 * @param newPeerId u16
 */
final case class ControlSetPeerId(newPeerId: Int) extends ControlPayload {
  override def toString: String = s"newPeerId: $newPeerId"
}

object EmptyPayload extends ControlPayload
