package space.maizy.mozzarella.minetest_monitor

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import org.pcap4j.core.PacketListener

trait MinetestProtoListener extends PacketListener {
  var serverPort: Option[Int] = None
}
