package space.maizy.mozzarella.minetest_monitor.listener

/**
  * Copyright (c) Nikita Kovaliov, maizy.space, 2019
  * See LICENSE.txt for details.
  */

import java.io.{ DataOutputStream, FileOutputStream }
import com.typesafe.scalalogging.LazyLogging
import org.pcap4j.packet.{ Packet, UdpPacket }
import space.maizy.mozzarella.minetest_monitor.MinetestProtoListener
import space.maizy.mozzarella.minetest_proto.original.Direction
import space.maizy.mozzarella.minetest_proto.utils.Dump


class DumpPackets(dumpPath: String) extends MinetestProtoListener with LazyLogging {

  val dump = new DataOutputStream(new FileOutputStream(dumpPath))

  def describeData(data: Array[Byte]): String = {
    data.map(b => f"$b%02x").mkString(" ")
  }

  override def gotPacket(packet: Packet) {
    logger.trace("packet: {}", packet)
    Option(packet.get(classOf[UdpPacket])).foreach { udpPacket =>
      val header = udpPacket.getHeader
      val mayBeDirection = (serverPort, header.getDstPort.valueAsInt, header.getSrcPort.valueAsInt) match {
        case (Some(port), dstPort, _) if port == dstPort =>
          Some(Direction.ToServer)
        case (Some(port), _, srcPort) if port == srcPort =>
          Some(Direction.ToClient)
        case _ =>
          None
      }

      mayBeDirection.foreach { dir =>
        val data = udpPacket.getPayload.getRawData
        logger.info(s"$dir  ${data.size}")
        Dump.write(dump, dir, data)
      }
    }
  }
}

