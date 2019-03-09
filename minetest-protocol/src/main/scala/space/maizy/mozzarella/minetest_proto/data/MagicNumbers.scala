package space.maizy.mozzarella.minetest_proto.data

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.bits._

object MagicNumbers {
  val protocolVersion: ByteVector = hex"4f 45 74 03"
  val protocolVersionInt: Int = protocolVersion.toInt()

  val serverPeer: Int = 1
}
