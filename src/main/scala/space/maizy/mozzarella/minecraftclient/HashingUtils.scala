package space.maizy.mozzarella.minecraftclient

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2018
 * See LICENSE.txt for details.
 */

import java.nio.charset.StandardCharsets.UTF_8

private[minecraftclient] object HashingUtils {
  def sha256(v: String): String =
    String.format(
      "%064x",
      new java.math.BigInteger(1, java.security.MessageDigest.getInstance("SHA-256").digest(v.getBytes(UTF_8)))
    )
}
