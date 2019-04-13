package space.maizy.mozzarella.minetest_proto.original

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2019
 * See LICENSE.txt for details.
 */

import scodec.bits.ByteVector
import space.maizy.mozzarella.minetest_proto.data.ToServerCommand
import space.maizy.mozzarella.minetest_proto.data.ToServerCommand.Type
import space.maizy.mozzarella.minetest_proto.utils.Printer

sealed trait ToServerOriginalPacket extends OriginalPacketWithKnownDirection {
  def command: ToServerCommand.Type

  override val direction: Direction.Value = Direction.ToServer

  override def toString: String = s"ToServerOriginalPacket($command)"
}

final case class ToServerUnsupported(override val command: ToServerCommand.Type, payload: ByteVector)
  extends ToServerOriginalPacket {
  override def toString: String = s"ToServerOriginalPacket(" +
      s"Unsupported($command): ${Printer.byteVectorToString(payload)})"
}

object ToServerEmpty extends ToServerOriginalPacket {
  override val command: Type = ToServerCommand.TOSERVER_EMPTY
}

/**
 * client.cpp - Client::sendInit
 *
 * @param serializationVersion - u8
 * @param compressionMode - u16, unused
 * @param minProtoVersion - u16
 * @param maxProtoVersion - u16
 * @param playerName - std::string
 */
final case class ToServerInit(
    serializationVersion: Int,
    compressionMode: Int,
    minProtoVersion: Int,
    maxProtoVersion: Int,
    playerName: String
) extends ToServerOriginalPacket {
  override val command: Type = ToServerCommand.TOSERVER_INIT

  override def toString: String = "ToServerInit(" +
      s"serializationVersion: $serializationVersion, minProto: $minProtoVersion, maxProto: $maxProtoVersion, " +
      s"player: $playerName)"
}
