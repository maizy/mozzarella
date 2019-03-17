package space.maizy.mozzarella.minetest_proto.original

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scala.language.implicitConversions
import scodec.bits.ByteVector
import space.maizy.mozzarella.minetest_proto.Packet
import space.maizy.mozzarella.minetest_proto.data.PacketType.Type
import space.maizy.mozzarella.minetest_proto.data.{ ToClientCommand, PacketType, ToServerCommand }
import space.maizy.mozzarella.minetest_proto.utils.Printer

object Direction extends Enumeration {

  protected case class Val(mnemonic: String) extends super.Val {
    override def toString(): String = mnemonic
  }

  implicit def valueToVal(x: Value): Val = x.asInstanceOf[Val]

  val ToServer = Val("->")
  val ToClient = Val("<-")
}

sealed trait OriginalPacketWithKnownDirection extends Packet {
  override val packetType: Type = PacketType.Original
  def direction: Direction.Value
}

sealed trait ToServerOriginalPacket extends OriginalPacketWithKnownDirection {
  def command: ToServerCommand.Type
  override val direction: Direction.Value = Direction.ToServer
}

final case class ToServerUnsupportedPacket(override val command: ToServerCommand.Type, payload: ByteVector)
  extends ToServerOriginalPacket {
  override def toString: String = s"OriginalPacket($direction " +
    s"Unsupported($command): ${Printer.printByteVector(payload)})"
}

sealed trait ToClientOriginalPacket extends OriginalPacketWithKnownDirection {
  def command: ToClientCommand.Type
  override val direction: Direction.Value = Direction.ToClient
}

final case class ToClientUnsupportedPacket(override val command: ToClientCommand.Type, payload: ByteVector)
  extends ToClientOriginalPacket {

  override def toString: String = s"OriginalPacket($direction " +
    s"Unsupported($command): ${Printer.printByteVector(payload)})"
}
