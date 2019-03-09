package space.maizy.mozzarella.minetest_proto.codecs

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import org.scalatest.{ FlatSpec, Matchers }
import scodec.Codec
import scodec.bits._
import space.maizy.mozzarella.minetest_proto.RawPacket
import space.maizy.mozzarella.minetest_proto.data.MagicNumbers

class RawPacketCodecTest extends FlatSpec with Matchers {
  import RawPacketCodec._

  "RawPacketCodec" should "decode valid client data" in {
    val data = hex"4f 45 74 03 00 03 00 00 00 ff e6"
    Codec.decode[RawPacket](data.bits).require.value shouldBe RawPacket(
      protocolVersion = MagicNumbers.protocolVersion.toLong(),
      peerId = 3,
      channel = 0,
      data = hex"00 00 ff e6"
    )
  }

  it should "decode valid server data" in {
    val data = hex"4f 45 74 03 00 01 00 00 00 ff dd"
    Codec.decode[RawPacket](data.bits).require.value shouldBe RawPacket(
      protocolVersion = MagicNumbers.protocolVersion.toLong(),
      peerId = MagicNumbers.serverPeer,
      channel = 0,
      data = hex"00 00 ff dd"
    )
  }

}
