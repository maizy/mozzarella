package space.maizy.mozzarella.minetest_monitor

/**
  * Copyright (c) Nikita Kovaliov, maizy.space, 2019
  * See LICENSE.txt for details.
  */

import java.util.concurrent.Executors
import com.typesafe.scalalogging.LazyLogging
import org.pcap4j.core.BpfProgram.BpfCompileMode
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode
import org.pcap4j.core.{ PcapStat, Pcaps }


object Launcher extends LazyLogging {

  val INF: Int = -1

  def main(args: Array[String]): Unit = {

    val interface = args.headOption.getOrElse("lo0")
    val port = 30000

    val nif = Pcaps.getDevByName(interface)

    logger.info("Network interface {}", nif)

    val snapLen = 65535
    val timeout = 10

    val handle = nif.openLive(snapLen, PromiscuousMode.PROMISCUOUS, timeout)

    val filter = s"udp port $port"
    logger.info("filter: {}", filter)
    handle.setFilter(filter, BpfCompileMode.OPTIMIZE)

    scala.sys.addShutdownHook {
      logger.info("Stopping ...")
      val ps: PcapStat = handle.getStats
      logger.info(s"Recived: ${ps.getNumPacketsReceived}")
      logger.info(s"Dropped: ${ps.getNumPacketsDropped}")
      logger.info(s"Dropped by inetface: ${ps.getNumPacketsDroppedByIf}")
    }

    val pool = Executors.newCachedThreadPool()
    handle.loop(INF, new MinetestProtoListener(port), pool)

  }

}
