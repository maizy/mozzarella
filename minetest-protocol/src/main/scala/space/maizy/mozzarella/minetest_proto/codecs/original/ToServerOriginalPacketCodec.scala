package space.maizy.mozzarella.minetest_proto.codecs.original

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import cats.syntax.either._
import scodec.Attempt.{ Failure, Successful }
import scodec.bits.BitVector
import scodec.{ Attempt, Codec, DecodeResult, Encoder }
import scodec.codecs._
import space.maizy.mozzarella.minetest_proto.codecs.MinetestStringCodec
import space.maizy.mozzarella.minetest_proto.data.ToServerCommand
import space.maizy.mozzarella.minetest_proto.original._


object ToServerOriginalPacketCodec {
  import OriginalPacketCodec._

  val toServerEmptyCodec: Codec[ToServerEmpty.type] = Codec[ToServerEmpty.type](
    (c: ToServerEmpty.type) => Attempt.Successful(BitVector.empty),
    (bits: BitVector) => Attempt.Successful(DecodeResult(ToServerEmpty, bits))
  )

  val toServerInitCodec: Codec[ToServerInit] =
  {
    ("serializationVersion" | uint8) ::
    ("compressionMode" | uint16) ::
    ("minProtoVersion" | uint16) ::
    ("maxProtoVersion" | uint16) ::
    ("playerName" | MinetestStringCodec.stdStringCodec)
  }.as[ToServerInit]

  implicit val toServerOriginalPacketEncoder: Encoder[ToServerOriginalPacket] =
    Encoder[ToServerOriginalPacket] { c: ToServerOriginalPacket =>
      val payloadAttempt = c match {
        case ToServerUnsupported(command, payload) => Attempt.Successful(payload.toBitVector)
        case ToServerEmpty => toServerEmptyCodec.encode(ToServerEmpty)
        case p: ToServerInit => toServerInitCodec.encode(p)
      }

      payloadAttempt.flatMap { payloadBits =>
        val originalPacket = OriginalPacket(c.command.asInt, payloadBits.toByteVector)
        originalPacketCodec.encode(originalPacket)
      }
    }


  def decode(packet: OriginalPacket): Either[List[String], ToServerOriginalPacket] = {
    ToServerCommand.index.get(packet.commandCode) match {
      case Some(command) =>
        val bits = packet.payload.toBitVector
        val parsed = command match {
          case ToServerCommand.TOSERVER_EMPTY => toServerEmptyCodec.decode(bits)
          case ToServerCommand.TOSERVER_INIT => toServerInitCodec.decode(bits)
          case _ =>
            Attempt.Successful(DecodeResult(
              ToServerUnsupported(command, packet.payload),
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
        List(s"unknown server command code in $packet").asLeft
    }
  }

}
