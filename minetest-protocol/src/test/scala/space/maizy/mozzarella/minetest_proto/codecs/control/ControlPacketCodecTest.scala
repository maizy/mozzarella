package space.maizy.mozzarella.minetest_proto.codecs.control

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import org.scalatest.{ FlatSpec, Matchers }
import scodec.Codec
import scodec.bits._
import space.maizy.mozzarella.minetest_proto.control.{ ControlPacket, ControlAck, EmptyPayload }
import space.maizy.mozzarella.minetest_proto.data.ControlType

class ControlPacketCodecTest extends FlatSpec with Matchers {

  import ControlPacketCodec._

  private val disconnect = ControlPacket(ControlType.Disconnect, EmptyPayload)
  private val disconnectEncoded = hex"03".bits

  private val ack = ControlPacket(ControlType.Ack, ControlAck(65500))
  private val ackEncoded = hex"00 ff dc".bits

  "ControlPacketCodec" should "decode control packets" in {

    Codec.decode[ControlPacket](disconnectEncoded).require.value shouldBe disconnect

    hex"ff dc".toInt(signed = false) shouldBe 65500
    Codec.decode[ControlPacket](ackEncoded).require.value shouldBe ack
  }

  // TODO
  ignore should "encode control packets" in {
    Codec.encode[ControlPacket](disconnect).require shouldBe disconnectEncoded
    Codec.encode[ControlPacket](ack).require shouldBe ackEncoded
  }

}
