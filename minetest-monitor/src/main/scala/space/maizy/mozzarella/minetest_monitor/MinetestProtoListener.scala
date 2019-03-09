package space.maizy.mozzarella.minetest_monitor

/**
  * Copyright (c) Nikita Kovaliov, maizy.space, 2019
  * See LICENSE.txt for details.
  */

import com.typesafe.scalalogging.LazyLogging
import org.pcap4j.core.PacketListener
import org.pcap4j.packet.{ Packet, UdpPacket }
import scodec.{ Attempt, Codec, DecodeResult }
import scodec.bits.BitVector
import space.maizy.mozzarella.minetest_proto.RawPacket
import space.maizy.mozzarella.minetest_proto.codecs.RawPacketCodec
import space.maizy.mozzarella.minetest_proto.data.MagicNumbers


class MinetestProtoListener(val serverPort: Int = 30000) extends PacketListener with LazyLogging {

  import RawPacketCodec._

  def describeData(data: Array[Byte]): String = {
    data.map(b => f"$b%02x").mkString(" ")
  }

  override def gotPacket(packet: Packet) {
    logger.trace("packet: {}", packet)
    Option(packet.get(classOf[UdpPacket])).foreach { udpPacket =>
      val header = udpPacket.getHeader
      val mayBeDirection = if (header.getDstPort.valueAsInt == serverPort) {
        Some("->")
      } else if (header.getSrcPort.valueAsInt == serverPort) {
        Some("<-")
      } else {
        None
      }

      val data = udpPacket.getPayload.getRawData
      val successfull = mayBeDirection.exists { dir =>
        Codec.decode[RawPacket](BitVector(data)) match {
          case Attempt.Successful(DecodeResult(res, _)) =>
            if(res.protocolVersion == MagicNumbers.protocolVersionInt) {
              logger.info("{} {}", dir, res)
              true
            } else {
              false
            }
          case Attempt.Failure(cause) =>
            false
        }
      }

      if (!successfull) {
        logger.warn(data.map(p => f"$p%02x").mkString(" "))
      }
    }
  }
}

