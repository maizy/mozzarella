package space.maizy.mozzarella.minetest_monitor

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import space.maizy.mozzarella.minetest_monitor.listener.DumpPackets


object DumpApp extends App {

  def main(args: Array[String]): Unit = {
    val dumpFile = args.headOption.getOrElse("./dump.bin")
    val interface = args.lift(1).getOrElse("lo0")
    val port = 30000
    launch(interface, port, new DumpPackets(dumpFile))
  }
}
