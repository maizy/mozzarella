package space.maizy.mozzarella.minecraftsdk

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2018
 * See LICENSE.txt for details.
 */

import scala.concurrent.{ ExecutionContext, Future }
import space.maizy.mozzarella.minecraftclient.MinecraftClient
import space.maizy.mozzarella.minecraftclient.ArgSyntax._

class World(name: String, minecraftClient: MinecraftClient) {
  def addBlock(block: Block)(implicit ex: ExecutionContext): Future[Unit] = {
    // TODO: understand why worlds.world.set_block method doesn't work
    minecraftClient.callMethod(
      "server.run_command",
      List(s"setblock ${block.position.x} ${block.position.y} ${block.position.z} ${block.material.code}".arg)
    ).map(_ => ())
  }
}
