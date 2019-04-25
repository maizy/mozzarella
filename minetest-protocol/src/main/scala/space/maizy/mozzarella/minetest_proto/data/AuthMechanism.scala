package space.maizy.mozzarella.minetest_proto.data

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2019
 * See LICENSE.txt for details.
 */

import scala.language.implicitConversions
import scodec.bits.BitVector
import scodec.codecs._

object AuthMechanism extends Enumeration {

  type Type = Value

  protected case class Val(mask: Int) extends super.Val {
    def asBits: BitVector = uint32.encode(mask).require
  }

  implicit def valueToVal(x: Value): Val = x.asInstanceOf[Val]

  val None = Val(0)
  val LegacyPassword = Val(1)
  val SRP = Val(1 << 1)
  val FirstSRP = Val(1 << 2)
}
