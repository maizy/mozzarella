package space.maizy.mozzarella.minetest_monitor

/**
  * Copyright (c) Nikita Kovaliov, maizy.space, 2019
  * See LICENSE.txt for details.
  */

import com.typesafe.scalalogging.LazyLogging
import org.pcap4j.core.PacketListener
import org.pcap4j.packet.{ Packet, UdpPacket }


class MinetestProtoListener(val serverPort: Int = 30000) extends PacketListener with LazyLogging {

  def describeData(data: Array[Byte]): String = {
    data.map(b => f"${b}%02x").mkString(" ")
  }

  override def gotPacket(packet: Packet) {
    logger.trace("packet: {}", packet)
    Option(packet.get(classOf[UdpPacket])).foreach { udpPacket =>
      val header = udpPacket.getHeader
      if (header.getDstPort.valueAsInt == serverPort) {
        logger.info("-> " + describeData(udpPacket.getPayload.getRawData))
      } else if (header.getSrcPort.valueAsInt == serverPort) {
        logger.info("<- " + describeData(udpPacket.getPayload.getRawData))
      }
    }
  }
}

