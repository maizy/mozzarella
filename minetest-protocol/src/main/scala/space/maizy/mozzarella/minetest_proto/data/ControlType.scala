package space.maizy.mozzarella.minetest_proto.data

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scala.language.implicitConversions
import scodec.bits.ByteVector

/**
 * https://github.com/minetest/minetest/blob/master/src/network/connection.h
 */
object ControlType extends Enumeration {

  type Type = Value

  protected case class Val(asInt: Int) extends super.Val {
    val asByte: ByteVector = ByteVector(asInt)
  }

  implicit def valueToVal(x: Value): Val = x.asInstanceOf[Val]

  val Ack = Val(0)
  val SetPeerId = Val(1)
  val Ping = Val(2)
  val Disconnect = Val(3)

  val index: Map[Int, ControlType.Type] = values.map(v => v.asInt -> v).toMap
  val byteIndex: Map[ByteVector, ControlType.Type] = values.map(v => v.asByte -> v).toMap
}
