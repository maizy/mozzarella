package space.maizy.mozzarella.minetest_proto.utils

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.Attempt
import scodec.bits.BitVector

object EncoderSyntax {

  implicit class AttemptBitVectorOpts(attempt: Attempt[BitVector]) {

    def append(next: => Attempt[BitVector]): Attempt[BitVector] = {
      attempt.flatMap { startBits =>
        val nextAttempt = next
        nextAttempt.map { nextBits => startBits ++ nextBits }
      }
    }
  }

}
