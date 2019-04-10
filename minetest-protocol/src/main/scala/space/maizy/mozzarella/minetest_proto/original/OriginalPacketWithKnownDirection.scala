package space.maizy.mozzarella.minetest_proto.original

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scala.language.implicitConversions
import space.maizy.mozzarella.minetest_proto.Packet
import space.maizy.mozzarella.minetest_proto.data.PacketType
import space.maizy.mozzarella.minetest_proto.data.PacketType.Type

object Direction extends Enumeration {

  protected case class Val(mnemonic: String) extends super.Val {
    override def toString(): String = mnemonic
  }

  implicit def valueToVal(x: Value): Val = x.asInstanceOf[Val]

  val ToServer = Val("->")
  val ToClient = Val("<-")
}

trait OriginalPacketWithKnownDirection extends Packet {
  override val packetType: Type = PacketType.Original
  def direction: Direction.Value
}
