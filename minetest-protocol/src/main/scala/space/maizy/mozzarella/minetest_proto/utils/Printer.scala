package space.maizy.mozzarella.minetest_proto.utils

import scodec.bits.ByteVector

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */
object Printer {
  def byteVectorToString(vector: ByteVector): String = {
    val parts = vector.foldLeft(List.empty[String]) { (acc, byte) => f"$byte%02x" +: acc }
    parts.reverse.mkString(" ")
  }

  def asHex(value: Int): String = byteVectorToString(ByteVector(value))
  def asHex(value: Long): String = byteVectorToString(ByteVector(value))
}
