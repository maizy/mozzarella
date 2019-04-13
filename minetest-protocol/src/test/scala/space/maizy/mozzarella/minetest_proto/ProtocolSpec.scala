package space.maizy.mozzarella.minetest_proto

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import java.io.DataInputStream
import org.scalatest.{ FlatSpec, Matchers }
import scodec.bits._
import space.maizy.mozzarella.minetest_proto.data.{ MagicNumbers, PacketType, ToClientCommand, ToServerCommand }
import space.maizy.mozzarella.minetest_proto.original._
import space.maizy.mozzarella.minetest_proto.reliable.ReliablePacket
import space.maizy.mozzarella.minetest_proto.utils.Dump
import cats.syntax.option._

class ProtocolSpec extends FlatSpec with Matchers {

  "Protocol" should "decode all messages in login - logoff dump" in {
    checkDumpDecoded("login-logoff-dump.bin", expectedDumpPackets = 979) { (index, bits, dir, parsed) =>
      println(s"#$index $dir ${parsed.shortDescription}")
    }
  }

  it should "encode all control messages in login - logoff dump" in {
    checkSomeMessagesEncoded("login-logoff-dump.bin", expectedDumpPackets = 979) { (_, _, _, parsed) =>
      parsed.packet.packetType == PacketType.Control
    }
  }

  it should "encode all reliable+control packets in login - logoff dump" in {
    checkSomeMessagesEncoded("login-logoff-dump.bin", expectedDumpPackets = 979) { (_, _, _, parsed) =>
      parsed.packet match {
        case p@ReliablePacket(_, PacketType.Control, _) => true
        case _ => false
      }
    }
  }

  it should "encode all ToServer packets in login - logoff dump" in {
    checkSomeMessagesEncoded("login-logoff-dump.bin", expectedDumpPackets = 979) { (_, _, dir, _) =>
      dir == Direction.ToServer
    }
  }

  it should "encode raw original packet in login - logoff dump" in {
    val packet = OriginalPacket(0x02, hex"1c 00 00 00 25 00 25 00 05 6d 61 69 7a 79")
    val onWire = PacketOnWire(channel = 1, peerId = 3, packet)
    val expected = MagicNumbers.protocolVersion ++ hex"00 03 01 01 00 02 1c 00 00 00 25 00 25 00 05 6d 61 69 7a 79"

    Protocol.encode(onWire).right.get shouldBe expected.toBitVector
  }

  it should "decode & encode some ToServer messages in login - logoff dump" in {
    val supportedOriginalCommands = List(
      ToServerCommand.TOSERVER_EMPTY,
      ToServerCommand.TOSERVER_INIT
    )
    val chunks = readData("login-logoff-dump.bin")
    var hasErrors = false
    for (((dir, data), index) <- chunks.zipWithIndex) {
      val bits = BitVector(data)
      if (dir == Direction.ToServer) {
        val eitherDecoded = Protocol.decodeToServer(bits)
        eitherDecoded shouldBe 'right
        val decoded = eitherDecoded.right.get
        val mayBeOrigPacket = decoded.packet match {
          case p: ToServerOriginalPacket => p.some
          case ReliablePacket(_, _, p: ToServerOriginalPacket) => p.some
          case _ => None
        }

        mayBeOrigPacket match {
          case Some(origPacket) =>
            if (supportedOriginalCommands.contains(origPacket.command)) {
              if (origPacket.isInstanceOf[ToServerUnsupported]) {
                println(s"packet decoded as unsupported: $decoded")
                hasErrors = true
              } else {
                val eitherEncoded = Protocol.encode(decoded)
                eitherEncoded match {
                  case Left(errors) =>
                    hasErrors = true
                    println(s"unable to encode $decoded: ${errors.mkString(";")}")
                  case Right(encoded) =>
                    if (encoded != bits) {
                      println(s"wrong encode for $decoded: $encoded != $bits")
                      hasErrors = true
                    } else {
                      println(s"succussfully decoded & encoded: $decoded")
                    }
                }
              }
            }
          case _ =>
        }
      }
    }

    if (hasErrors) {
      fail("have some errors, see messages above")
    }

  }

  it should "encode & decode ToServerUnsupported packet" in {
    checkToServerDecodeEncodeRoundTrip(
      ToServerUnsupported(ToServerCommand.TOSERVER_CLICK_ACTIVEOBJECT, hex"de ad be af")
    )
  }

  it should "encode & decode ToClientUnsupported packet" in {
    checkToClientDecodeEncodeRoundTrip(
      ToClientUnsupported(ToClientCommand.TOCLIENT_CHAT_MESSAGE_OLD, hex"de ad be af")
    )
  }

  private def checkSomeMessagesEncoded(
      dumpPath: String,
      expectedDumpPackets: Int)(
      messageFilter: (Int, BitVector, Direction.Value, PacketOnWire) => Boolean)
      : Unit = {

    var anyError = false
    checkDumpDecoded(dumpPath, expectedDumpPackets) { (index, bits, dir, parsed) =>
      if (messageFilter(index, bits, dir, parsed)) {
        val encoded = Protocol.encode(parsed)
        encoded match {
          case Left(errors) =>
            println(s"Unable to encode: #$index $dir $parsed => ${errors.mkString("; ")}")
            anyError = true
          case Right(encodedBits) =>
            if (encodedBits != bits) {
              println(s"Wrong encode: #$index $dir $parsed")
              anyError = true
            } else {
              println(s"Encoded successfully: #$index $dir $parsed")
            }
        }
      }
    }

    if (anyError) {
      fail(s"Some messages in $dumpPath not encoded. See output for details")
    }

  }

  private def readData(resourcePath: String): Iterator[Dump.Chunk] = {
    val is = new DataInputStream(this.getClass.getResourceAsStream(resourcePath))
    Dump.read(is)
  }

  private def checkDumpDecoded(
      dumpPath: String, expectedDumpPackets: Int)(
      f: (Int, BitVector, Direction.Value, PacketOnWire) => Unit)
      : Unit = {

    val chunks = readData(dumpPath)

    var chunksAmount = 0
    for (((dir, data), index) <- chunks.zipWithIndex) {
      val bits = BitVector(data)
      val parsedRes = dir match {
        case Direction.ToServer => Protocol.decodeToServer(bits)
        case Direction.ToClient => Protocol.decodeToClient(bits)
      }
      parsedRes match {
        case Left(errors) =>
          fail(s"#$index $dir Unable to parse: $errors, data: ${data.map(p => f"$p%02x").mkString(" ")}")

        case Right(parsed) =>
          f(index, bits, dir, parsed)
      }
      chunksAmount += 1
    }

    chunksAmount shouldBe expectedDumpPackets
  }

  private def checkToServerDecodeEncodeRoundTrip(packet: ToServerOriginalPacket): Unit = {
    val onWire = PacketOnWire(channel = 1, peerId = 3, packet)
    val eitherEncoded = Protocol.encode(onWire)
    eitherEncoded shouldBe 'right
    val encoded = eitherEncoded.right.get
    val eitherDecoded = Protocol.decodeToServer(encoded)
    eitherDecoded shouldBe 'right
    val decoded = eitherDecoded.right.get
    decoded shouldBe onWire
  }

  private def checkToClientDecodeEncodeRoundTrip(packet: ToClientOriginalPacket): Unit = {
    val onWire = PacketOnWire(channel = 1, peerId = 3, packet)
    val eitherEncoded = Protocol.encode(onWire)
    eitherEncoded shouldBe 'right
    val encoded = eitherEncoded.right.get
    val eitherDecoded = Protocol.decodeToClient(encoded)
    eitherDecoded shouldBe 'right
    val decoded = eitherDecoded.right.get
    decoded shouldBe onWire
  }
}
