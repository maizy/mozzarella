package space.maizy.mozzarella.minetest_monitor

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import java.util.concurrent.TimeoutException
import com.typesafe.scalalogging.LazyLogging
import org.pcap4j.core.BpfProgram.BpfCompileMode
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode
import org.pcap4j.core.{ PcapStat, Pcaps }
import cats.syntax.option._


trait App extends LazyLogging {

  val INF: Int = -1

  def launch(interface: String, serverPort: Int,  listener: MinetestProtoListener): Unit = {

    val nif = Pcaps.getDevByName(interface)

    logger.info("Network interface {}", nif)

    val snapLen = 65535
    val timeout = 10

    val handle = nif.openLive(snapLen, PromiscuousMode.PROMISCUOUS, timeout)

    val filter = s"udp port $serverPort"
    logger.info("filter: {}", filter)
    handle.setFilter(filter, BpfCompileMode.OPTIMIZE)

    scala.sys.addShutdownHook {
      logger.info("Stopping ...")
      val ps: PcapStat = handle.getStats
      logger.info(s"Received: ${ps.getNumPacketsReceived}")
      logger.info(s"Dropped: ${ps.getNumPacketsDropped}")
      logger.info(s"Dropped by inetface: ${ps.getNumPacketsDroppedByIf}")
    }

    listener.serverPort = serverPort.some
    // one thread listiner, to preserve order
    while (true) {
      try {
        val packet = handle.getNextPacketEx
        listener.gotPacket(packet)
      } catch {
        case e: TimeoutException =>
      }
    }

    // more efficient but order is missing
    /* val pool = Executors.newCachedThreadPool()
    handle.loop(INF, new MinetestProtoListener(port), pool) */
  }
}
