package space.maizy.mozzarella.minetest_monitor

/**
  * Copyright (c) Nikita Kovaliov, maizy.space, 2019
  * See LICENSE.txt for details.
  */

import com.typesafe.scalalogging.LazyLogging
import org.pcap4j.core.PacketListener
import org.pcap4j.packet.{ Packet, UdpPacket }
import scodec.bits.BitVector
import space.maizy.mozzarella.minetest_proto.Protocol
import space.maizy.mozzarella.minetest_proto.original.Direction


class MinetestProtoListener(val serverPort: Int = 30000) extends PacketListener with LazyLogging {

  def describeData(data: Array[Byte]): String = {
    data.map(b => f"$b%02x").mkString(" ")
  }

  override def gotPacket(packet: Packet) {
    logger.trace("packet: {}", packet)
    Option(packet.get(classOf[UdpPacket])).foreach { udpPacket =>
      val header = udpPacket.getHeader
      val mayBeDirection = if (header.getDstPort.valueAsInt == serverPort) {
        Some(Direction.ToServer)
      } else if (header.getSrcPort.valueAsInt == serverPort) {
        Some(Direction.ToClient)
      } else {
        None
      }

      mayBeDirection.foreach { dir =>
        val data = udpPacket.getPayload.getRawData
        val bits = BitVector(data)
        val parsedRes = dir match {
          case Direction.ToServer => Protocol.parseToServer(bits)
          case Direction.ToClient => Protocol.parseToClient(bits)
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

