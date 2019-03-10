package space.maizy.mozzarella.minetest_proto.codecs

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.{ Attempt, Codec, Decoder, Encoder, Err }
import space.maizy.mozzarella.minetest_proto.data.PacketType

object PacketTypeCodec {

  private val decoder: Decoder[PacketType.Type] = scodec.codecs.uint8.emap { res =>
    PacketType.index.get(res) match {
      case Some(value) => Attempt.successful(value)
      case None => Attempt.failure(Err(s"unknown packet type $res"))
    }
  }

  private val encoder: Encoder[PacketType.Type] = Encoder { t => Attempt.successful(t.asByte.bits) }

  implicit val packetTypeCodec: Codec[PacketType.Type] = Codec(encoder, decoder)
}
