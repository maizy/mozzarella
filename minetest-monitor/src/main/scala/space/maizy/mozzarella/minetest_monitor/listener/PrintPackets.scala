package space.maizy.mozzarella.minetest_monitor.listener

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import com.typesafe.scalalogging.LazyLogging
import org.pcap4j.packet.{ Packet, UdpPacket }
import scodec.bits.BitVector
import space.maizy.mozzarella.minetest_monitor.MinetestProtoListener
import space.maizy.mozzarella.minetest_proto.Protocol
import space.maizy.mozzarella.minetest_proto.original.Direction


class PrintPackets extends MinetestProtoListener with LazyLogging {

  def describeData(data: Array[Byte]): String = {
    data.map(b => f"$b%02x").mkString(" ")
  }

  override def gotPacket(packet: Packet): Unit = {
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
        val bits = BitVector(data)
        val parsedRes = dir match {
          case Direction.ToServer => Protocol.decodeToServer(bits)
          case Direction.ToClient => Protocol.decodeToClient(bits)
          case _ => throw new RuntimeException("?")
        }
        parsedRes match {
          case Left(errors) =>
            logger.warn(s"$dir Unable to parse: $errors, data: ${data.map(p => f"$p%02x").mkString(" ")}")

          case Right(value) =>
            logger.info(s"$dir $value")
        }
      }
    }
  }
}

