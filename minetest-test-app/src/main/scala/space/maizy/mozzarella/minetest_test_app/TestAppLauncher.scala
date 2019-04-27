package space.maizy.mozzarella.minetest_test_app

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import java.net.InetSocketAddress
import scala.util.Random
import scodec.bits.BitVector
import space.maizy.mozzarella.minetest_proto.control.{ ControlAck, ControlPacket, ControlSetPeerId }
import space.maizy.mozzarella.minetest_proto.data.ControlType
import space.maizy.mozzarella.minetest_proto.original.{ ToClientHello, ToClientOriginalPacket, ToServerInit }
// import scala.concurrent.ExecutionContext
import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props }
import akka.io.{ IO, UdpConnected }
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import space.maizy.mozzarella.minetest_proto.{ PacketOnWire, Protocol }
import space.maizy.mozzarella.minetest_proto.data.PacketType
import space.maizy.mozzarella.minetest_proto.original.ToServerEmpty
import space.maizy.mozzarella.minetest_proto.reliable.ReliablePacket

class TestAuthActor(remote: InetSocketAddress) extends Actor with ActorLogging {
  import context.system

  var packetSeq = 65500
  var peerId: Int = 0

  IO(UdpConnected) ! UdpConnected.Connect(self, remote)

  /*

for any reliable packet:
-> ch:0, peer:3, Control(Ack: #65500)

#0 -> ch:0, peer:unassigned, ReliablePacket(#65500: ToServerOriginalPacket(EMPTY))
#1 <- ch:0, peer:server, ReliablePacket(#65500: Control(SetPeerId: newPeerId: 3))
#4 -> ch:1, peer:3     , ToServerInit(serializationVersion: 28, minProto: 37, maxProto: 37, player: maizy)
#5 <- ch:0, peer:server, ReliablePacket(#65501: ToClientHello(serializationVersion: 28,compressionMode: 0, proto: 37, allowedAuthMechanism: 2, legacyPlayerNameCasing: maizy))

   */

  private def nextSeq: Int = {
    if (packetSeq >= 65535) {
      packetSeq = 0
    } else {
      packetSeq += 1
    }
    packetSeq
  }

  def receive: Receive = {
    case UdpConnected.Connected =>
      log.info("connected")
      context.become(ready(sender()))
      val initConnectionPacket = ReliablePacket(nextSeq, PacketType.Original, ToServerEmpty)
      self ! PacketOnWire(channel = 0, peerId = peerId, initConnectionPacket)
  }

  def ready(connection: ActorRef): Receive = {
    case UdpConnected.Received(data) =>
      // process data, send it on, etc.
      Protocol.decodeToClient(BitVector(data.toByteBuffer)) match {
        case Left(errors) =>
          log.error("unparsable response {}: {}", data, errors)
        case Right(packet) =>
          log.info("receive: {}", packet)

          // first: assign new pid
          packet.packet match {
            case ReliablePacket(
              seqNum,
              PacketType.Control,
              ControlPacket(
                ControlType.SetPeerId,
                ControlSetPeerId(newPeerId)
              )
            ) =>
              log.info("peer id assigned: {}", newPeerId)
              peerId = newPeerId
            case _ =>
          }

          // response with ack to any reliable packet
          packet.packet match {
            case ReliablePacket(seqNum, _, _) =>
              val ack = PacketOnWire(
                channel = 0,
                peerId = peerId,
                ControlPacket(ControlType.Ack, ControlAck(seqNum))
              )

              self ! ack
            case _ =>
          }

          // process messages
          packet.packet match {
            case ReliablePacket(
              seqNum,
              PacketType.Control,
              ControlPacket(
                ControlType.SetPeerId,
                _
              )
            ) =>
              val toServerInit = PacketOnWire(
                channel = 1,
                peerId = peerId,
                ToServerInit(
                  serializationVersion = 28,
                  compressionMode = 0,
                  minProtoVersion = 37,
                  maxProtoVersion = 37,
                  playerName = s"test-${Random.nextInt(100000)}"
                )
              )

              self ! toServerInit

            case ReliablePacket(
              seqNum,
              PacketType.Original,
              originalPacket: ToClientOriginalPacket
            ) =>
              originalPacket match {
                case h: ToClientHello =>
                  if (h.serializationVersion != 28 || h.compressionMode != 0 || h.protoVersion != 37) {
                    log.error("unsupported protocol version: {}", h)
                    self ! UdpConnected.Disconnect
                  }
                case _ =>
              }

            case _ => log.error("unhandled")
          }
      }


    case p: PacketOnWire =>
      Protocol.encode(p) match {
        case Left(errors) =>
          log.error("Unable to encode packet to server: {}", errors)
        case Right(bits) =>
          log.info("send   : {}", p)
          val bs = ByteString(bits.toByteArray)
          connection ! UdpConnected.Send(bs)
      }

    case UdpConnected.Disconnect => connection ! UdpConnected.Disconnect

    case UdpConnected.Disconnected => context.stop(self)
  }
}

object TestAppLauncher {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()

    implicit val system: ActorSystem = ActorSystem("mozzarella-test-app", config)
    // implicit val ec: ExecutionContext = system.dispatcher

    val serverAddress = new InetSocketAddress("127.0.0.1", 30000)
    system.actorOf(Props(new TestAuthActor(serverAddress)), "test-actor")
  }
}
