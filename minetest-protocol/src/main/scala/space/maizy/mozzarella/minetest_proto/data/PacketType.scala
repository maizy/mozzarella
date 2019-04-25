package space.maizy.mozzarella.minetest_proto.data

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scala.language.implicitConversions
import scodec.bits.ByteVector

/**
 * https://github.com/minetest/minetest/blob/5.0.1/src/network/connection.h
 * size: u8
 */
object PacketType extends Enumeration {

  type Type = Value

  protected case class Val(asInt: Int) extends super.Val {
    val asByte: ByteVector = ByteVector(asInt)

    override def toString(): String = super.toString().toLowerCase()
  }

  implicit def valueToVal(x: Value): Val = x.asInstanceOf[Val]

  val Control = Val(0)
  val Original = Val(1)
  val Split = Val(2)
  val Reliable = Val(3)

  val index: Map[Int, PacketType.Type] = values.map(v => v.asInt -> v).toMap
  val byteIndex: Map[ByteVector, PacketType.Type] = values.map(v => v.asByte -> v).toMap
}
