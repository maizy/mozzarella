package space.maizy.mozzarella.minetest_proto.utils

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scodec.{ Attempt, DecodeResult }
import scodec.bits.BitVector

object DecoderSyntax {

  implicit class AttemptDecodeOpts[T](attempt: Attempt[DecodeResult[T]]) {

    def decodeRemaining[U](
        decodeF: (T, BitVector) => Attempt[DecodeResult[U]]): Attempt[DecodeResult[(T, U)]] = {
      attempt.flatMap{ firstRes =>
        val firstValue = firstRes.value
        val nextAttempt = decodeF(firstValue, firstRes.remainder)
        nextAttempt.map(p => p.map((firstValue, _)))
      }
    }
  }

  implicit class AttemptDecodeResultTuple2Opts[T, U](attempt: Attempt[DecodeResult[(T, U)]]) {
    def combine[R](f: (T, U) => R): Attempt[DecodeResult[R]] = {
      attempt.map(p => p.map(pair => f(pair._1, pair._2)))
    }
  }

}
