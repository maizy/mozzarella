package space.maizy.mozzarella.minetest_proto.utils

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import java.io.{ DataInputStream, DataOutputStream }
import scala.util.Try
import space.maizy.mozzarella.minetest_proto.original.Direction

/**
 * for tests only
 *
 * read & write has unsafe implementation
 */
object Dump {

  type Chunk = (Direction.Value, Array[Byte])

  private val div = Array(
    0x00.toByte, 0x00.toByte, 0x0A.toByte, 0x0A.toByte,
    0x0A.toByte, 0x0A.toByte, 0x00.toByte, 0x00.toByte
  )

  def write(file: DataOutputStream, dir: Direction.Value, data: Array[Byte]): Unit = {
    val directionByte = dir match {
      case Direction.ToServer => 0x01.toByte
      case Direction.ToClient => 0x02.toByte
    }
    file.write(div)
    file.write(Array(directionByte))
    file.writeInt(data.length)
    file.write(data)

    file.flush()
  }

  def read(input: DataInputStream): Iterator[Chunk] = {
    new Iterator[Chunk] {
      var chunk: Option[Chunk] = readChunk()  // read one chunk ahead
      var enought: Boolean = false

      override def hasNext: Boolean = chunk.isDefined

      override def next(): (Direction.Value, Array[Byte]) = {
        val current = chunk.get
        chunk = readChunk()
        current
      }

      private def readChunk(): Option[Chunk] = {
        if (enought) {
          throw new RuntimeException("Sorry, no more chunks 🤷‍")
        }
        val chunk = Try {
          val diffBuffer = new Array[Byte](div.length)
          input.read(diffBuffer)
          require(diffBuffer.toList == div.toList)

          val directionByte = input.readByte()

          val direction = if (directionByte == 0x01.toByte) {
            Direction.ToServer
          } else if (directionByte == 0x02.toByte) {
            Direction.ToClient
          } else {
            throw new RuntimeException("Unknown direction")
          }

          val dataLength = input.readInt()
          val dataBuffer = new Array[Byte](dataLength)
          input.read(dataBuffer)
          (direction, dataBuffer)
        }.toOption

        enought = chunk.isEmpty
        chunk
      }
    }
  }

}
