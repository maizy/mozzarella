package space.maizy.mozzarella.minetest_proto.original

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2019
 * See LICENSE.txt for details.
 */

import scodec.bits.ByteVector
import space.maizy.mozzarella.minetest_proto.data.ToClientCommand
import space.maizy.mozzarella.minetest_proto.utils.Printer


sealed trait ToClientOriginalPacket extends OriginalPacketWithKnownDirection {
  def command: ToClientCommand.Type
  override val direction: Direction.Value = Direction.ToClient
}

final case class ToClientUnsupported(override val command: ToClientCommand.Type, payload: ByteVector)
  extends ToClientOriginalPacket {

  override def toString: String = s"ToClientOriginalPacket(" +
    s"Unsupported($command): ${Printer.byteVectorToString(payload)})"
}
