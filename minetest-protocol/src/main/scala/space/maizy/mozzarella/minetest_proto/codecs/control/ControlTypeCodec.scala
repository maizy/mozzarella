package space.maizy.mozzarella.minetest_proto.codecs.control

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.{ Attempt, Codec, Decoder, Encoder, Err }
import space.maizy.mozzarella.minetest_proto.data.ControlType

object ControlTypeCodec {

  private val decoder: Decoder[ControlType.Type] = scodec.codecs.uint8.emap { res =>
    ControlType.index.get(res) match {
      case Some(value) => Attempt.successful(value)
      case None => Attempt.failure(Err(s"unknown control type $res"))
    }
  }

  private val encoder: Encoder[ControlType.Type] = Encoder { t => Attempt.successful(t.asByte.bits) }

  implicit val controlTypeCodec: Codec[ControlType.Type] = Codec(encoder, decoder)
}
