package space.maizy.mozzarella.minetest_monitor

import space.maizy.mozzarella.minetest_monitor.listener.PrintPackets

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */


object PrintApp extends App {

  def main(args: Array[String]): Unit = {
    val interface = args.headOption.getOrElse("lo0")
    val port = 30000
    launch(interface, port, new PrintPackets)
  }
}
