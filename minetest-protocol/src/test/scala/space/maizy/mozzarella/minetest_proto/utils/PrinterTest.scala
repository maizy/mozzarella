package space.maizy.mozzarella.minetest_proto.utils

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import org.scalatest.{ FlatSpec, Matchers }
import scodec.bits._

class PrinterTest extends FlatSpec with Matchers {

  "Printer.byteVector" should "work" in {
    Printer.byteVectorToString(hex"ff b0 0b 00") shouldBe "ff b0 0b 00"
  }

}
