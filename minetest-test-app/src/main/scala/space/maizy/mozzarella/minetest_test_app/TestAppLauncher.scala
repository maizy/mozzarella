package space.maizy.mozzarella.minetest_test_app

import java.net.InetSocketAddress
// import scala.concurrent.ExecutionContext
import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props }
import akka.io.{ IO, UdpConnected }
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import space.maizy.mozzarella.minetest_proto.{ PacketOnWire, Protocol }
import space.maizy.mozzarella.minetest_proto.data.PacketType
import space.maizy.mozzarella.minetest_proto.original.ToServerEmpty
import space.maizy.mozzarella.minetest_proto.reliable.ReliablePacket
import space.maizy.mozzarella.minetest_proto.utils.Printer

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

class TestAuthActor(remote: InetSocketAddress) extends Actor with ActorLogging {
  import context.system

  var packetSeq = 65500
  IO(UdpConnected) ! UdpConnected.Connect(self, remote)

  /**
#0 -> ch:0, peer:unassigned, ReliablePacket(#65500: ToServerOriginalPacket(EMPTY))
#1 <- ch:0, peer:server, ReliablePacket(#65500: Control(SetPeerId: newPeerId: 3))
#2 <- ch:0, peer:server, Control(Ack: #65500)
#3 -> ch:0, peer:3     , Control(Ack: #65500)
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
      context.become(ready(sender()))
      val initConnectionPacket = ReliablePacket(nextSeq, PacketType.Original, ToServerEmpty)
      self ! PacketOnWire(channel = 0, peerId = 0, initConnectionPacket)
  }

  def ready(connection: ActorRef): Receive = {
    case UdpConnected.Received(data) =>
      // process data, send it on, etc.
      log.info("receive data: {}", data)


    case p: PacketOnWire =>
      Protocol.encode(p) match {
        case Left(errors) =>
          log.error("Unable to encode packet to server: {}", errors)
        case Right(bits) =>
          log.info("send packet: {}", p)
          log.info("packet bytes: {}", Printer.byteVectorToString(bits.toByteVector))
          val bs = ByteString(bits.toByteArray)
          connection ! bs
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
