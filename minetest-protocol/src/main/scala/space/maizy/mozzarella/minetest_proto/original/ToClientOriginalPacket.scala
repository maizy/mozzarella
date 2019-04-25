package space.maizy.mozzarella.minetest_proto.original

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2019
 * See LICENSE.txt for details.
 */

import scodec.bits.ByteVector
import space.maizy.mozzarella.minetest_proto.data.ToClientCommand
import space.maizy.mozzarella.minetest_proto.utils.Printer


sealed trait ToClientOriginalPacket extends OriginalPacketWithKnownDirection {
  def command: ToClientCommand.Type
  override val direction: Direction.Value = Direction.ToClient
}

final case class ToClientUnsupported(override val command: ToClientCommand.Type, payload: ByteVector)
  extends ToClientOriginalPacket {

  override def toString: String = "ToClientOriginalPacket(" +
      s"Unsupported($command): ${Printer.byteVectorToString(payload)})"
}

/**
 * https://github.com/minetest/minetest/blob/5.0.1/src/network/serverpackethandler.cpp -
 *    handleCommand_Init
 *
 * @param serializationVersion - u8
 * @param compressionMode - u16, unused
 * @param protoVersion - u16
 * @param legacyPlayerNameCasing - std::string
 */
final case class ToClientHello(
    serializationVersion: Int,
    compressionMode: Int,
    protoVersion: Int,
    // TODO: parse bit flags: serverpackethandler.cpp - AuthMechanism
    allowedAuthMechanism: Long,
    legacyPlayerNameCasing: String
) extends ToClientOriginalPacket {
  override val command: ToClientCommand.Type = ToClientCommand.TOCLIENT_HELLO

  override def toString: String = s"ToClientHello(serializationVersion: $serializationVersion," +
    s"compressionMode: $compressionMode, proto: $protoVersion, allowedAuthMechanism: $allowedAuthMechanism, " +
    s"legacyPlayerNameCasing: $legacyPlayerNameCasing)"
}
