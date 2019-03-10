package space.maizy.mozzarella.minetest_proto.data

import scala.language.implicitConversions
import scodec.bits.{ BitVector, ByteVector }
import scodec.{ Attempt, Codec, DecodeResult, Err }

/**
 * https://github.com/minetest/minetest/blob/master/src/network/connection.h
 * size: u8
 */
object PacketType extends Enumeration {

  type Type = Value

  protected case class Val(asInt: Int) extends super.Val {
    val asByte: ByteVector = ByteVector(asInt)

    override def toString(): String = super.toString().toLowerCase()
  }

  implicit def valueToVal(x: Value): Val = x.asInstanceOf[Val]

  val Control = Val(0)
  val Original = Val(1)
  val Split = Val(2)
  val Reliable = Val(3)

  val index: Map[Int, PacketType.Type] = values.map(v => v.asInt -> v).toMap
  val byteIndex: Map[ByteVector, PacketType.Type] = values.map(v => v.asByte -> v).toMap

  implicit val packetTypeCodec: Codec[PacketType.Type] = Codec[PacketType.Type](
    (t: PacketType.Type) => Attempt.successful(t.asByte.bits),
    (buf: BitVector) => scodec.codecs.uint8.decode(buf).flatMap{ res =>
      Attempt.fromOption(index.get(res.value), Err("unknown packet type")).map(DecodeResult(_, res.remainder))
    }
  )
}
