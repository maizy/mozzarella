package space.maizy.mozzarella.minetest_proto.codecs.original

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import cats.syntax.either._
import scodec.Attempt.{ Failure, Successful }
import scodec.bits.BitVector
import scodec.{ Attempt, DecodeResult, Encoder }
import space.maizy.mozzarella.minetest_proto.data.ToClientCommand
import space.maizy.mozzarella.minetest_proto.original.{ OriginalPacket, ToClientOriginalPacket, ToClientUnsupported }

object ToClientOriginalPacketCodec {

  import OriginalPacketCodec._

  implicit val toClientOriginalPacketEncoder: Encoder[ToClientOriginalPacket] =
    Encoder[ToClientOriginalPacket] { c: ToClientOriginalPacket =>
      val payloadAttempt: Attempt[BitVector] = c match {
        case ToClientUnsupported(command, payload) =>
          Attempt.Successful(payload.toBitVector)
      }

      payloadAttempt.flatMap { payloadBits =>
        val originalPacket = OriginalPacket(c.command.asInt, payloadBits.toByteVector)
        originalPacketCodec.encode(originalPacket)
      }
    }

  def decode(packet: OriginalPacket): Either[List[String], ToClientOriginalPacket] = {
    ToClientCommand.index.get(packet.commandCode) match {
      case Some(command) =>
        val parsed: Attempt[DecodeResult[ToClientOriginalPacket]] = command match {
          case _ =>
            Attempt.Successful(DecodeResult(
              ToClientUnsupported(command, packet.payload),
              BitVector.empty
            ))
        }

        parsed match {
          case Successful(DecodeResult(res, reminder)) if reminder.isEmpty =>
            res.asRight
          case Successful(_) => List("have remaining bits after decoding").asLeft
          case Failure(err) => List(err.messageWithContext).asLeft
        }
      case None =>
        List(s"unknown client command code in $packet").asLeft
    }
  }

}
