package space.maizy.mozzarella.minetest_proto.codecs.original

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import cats.syntax.either._
import scodec.Attempt.{ Failure, Successful }
import scodec.bits.BitVector
import scodec.codecs._
import scodec.{ Attempt, Codec, DecodeResult, Encoder }
import space.maizy.mozzarella.minetest_proto.codecs.MinetestStringCodec
import space.maizy.mozzarella.minetest_proto.data.ToClientCommand
import space.maizy.mozzarella.minetest_proto.original.{ OriginalPacket, ToClientHello, ToClientOriginalPacket, ToClientUnsupported }

object ToClientOriginalPacketCodec {

  import OriginalPacketCodec._

  val toClientHelloCodec: Codec[ToClientHello] =
  {
    ("serializationVersion" | uint8) ::
    ("compressionMode" | uint16) ::
    ("protoVersion" | uint16) ::
    ("allowedAuthMechanism" | uint32) ::
    ("legacyPlayerNameCasing" | MinetestStringCodec.stdStringCodec)
  }.as[ToClientHello]

  implicit val toClientOriginalPacketEncoder: Encoder[ToClientOriginalPacket] =
    Encoder[ToClientOriginalPacket] { c: ToClientOriginalPacket =>
      val payloadAttempt: Attempt[BitVector] = c match {
        case ToClientUnsupported(command, payload) =>
          Attempt.Successful(payload.toBitVector)

        case p: ToClientHello => toClientHelloCodec.encode(p)
      }

      payloadAttempt.flatMap { payloadBits =>
        val originalPacket = OriginalPacket(c.command.asInt, payloadBits.toByteVector)
        originalPacketCodec.encode(originalPacket)
      }
    }

  def decode(packet: OriginalPacket): Either[List[String], ToClientOriginalPacket] = {
    ToClientCommand.index.get(packet.commandCode) match {
      case Some(command) =>
        val bits = packet.payload.toBitVector
        val parsed: Attempt[DecodeResult[ToClientOriginalPacket]] = command match {
          case ToClientCommand.TOCLIENT_HELLO => toClientHelloCodec.decode(bits)
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
