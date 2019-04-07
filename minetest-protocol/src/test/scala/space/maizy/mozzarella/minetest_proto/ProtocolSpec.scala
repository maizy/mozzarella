package space.maizy.mozzarella.minetest_proto

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import java.io.DataInputStream
import org.scalatest.{ FlatSpec, Matchers }
import scodec.bits.BitVector
import space.maizy.mozzarella.minetest_proto.original.Direction
import space.maizy.mozzarella.minetest_proto.utils.Dump

class ProtocolSpec extends FlatSpec with Matchers {

  private def readData(resourcePath: String): Iterator[Dump.Chunk] = {
    val is = new DataInputStream(this.getClass.getResourceAsStream(resourcePath))
    Dump.read(is)
  }

  "Protocol" should "parse all messages in login - logoff dump" in {
    val chunks = readData("login-logoff-dump.bin")

    var chunksAmount = 0
    for (((dir, data), index) <- chunks.zipWithIndex) {
      val bits = BitVector(data)
      val parsedRes = dir match {
        case Direction.ToServer => Protocol.parseToServer(bits)
        case Direction.ToClient => Protocol.parseToClient(bits)
      }
      parsedRes match {
        case Left(errors) =>
          fail(s"#$index $dir Unable to parse: $errors, data: ${data.map(p => f"$p%02x").mkString(" ")}")

        case Right(parsed) =>
          println(s"#$index $dir $parsed")
      }
      chunksAmount += 1
    }

    chunksAmount shouldBe 979
  }
}
