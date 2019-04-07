package space.maizy.mozzarella.minetest_proto

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import java.io.DataInputStream
import org.scalatest.{ FlatSpec, Matchers }
import scodec.bits.BitVector
import space.maizy.mozzarella.minetest_proto.data.PacketType
import space.maizy.mozzarella.minetest_proto.original.Direction
import space.maizy.mozzarella.minetest_proto.utils.Dump

class ProtocolSpec extends FlatSpec with Matchers {

  "Protocol" should "decode all messages in login - logoff dump" in {
    checkDump("login-logoff-dump.bin", expectedDumpPackets = 979) { (index, bits, dir, parsed) =>
      println(s"#$index $dir $parsed")
    }
  }

  it should "encode all toServer control messages in login - logoff dump" in {
    checkSomeMessagesEncoded("login-logoff-dump.bin", expectedDumpPackets = 979) { (_, _, dir, parsed) =>
      dir == Direction.ToServer && parsed.packet.packetType == PacketType.Control
    }
  }

  private def checkSomeMessagesEncoded(
      dumpPath: String,
      expectedDumpPackets: Int)(
      messageFilter: (Int, BitVector, Direction.Value, PacketOnWire) => Boolean)
      : Unit = {

    var anyError = false
    checkDump(dumpPath, expectedDumpPackets) { (index, bits, dir, parsed) =>
      if (messageFilter(index, bits, dir, parsed)) {
        val encoded = Protocol.encodeToServer(parsed)
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

  private def checkDump(
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
}
