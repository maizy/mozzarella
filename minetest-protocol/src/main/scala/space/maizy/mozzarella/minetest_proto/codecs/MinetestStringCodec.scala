package space.maizy.mozzarella.minetest_proto.codecs

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2019
 * See LICENSE.txt for details.
 */

import java.nio.charset.Charset
import scodec.Codec
import scodec.codecs._

object MinetestStringCodec {
  val asciiCharset: Charset = Charset.forName("US-ASCII")

  val stdStringCodec: Codec[String] = variableSizeBytes(uint16, string(asciiCharset))
    .withToString(s"std::string(${asciiCharset.displayName})")
}
