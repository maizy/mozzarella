package space.maizy.mozzarella.minecraftsdk

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2018
 * See LICENSE.txt for details.
 */

import scala.concurrent.{ ExecutionContext, Future }
import space.maizy.mozzarella.minecraftclient.{ ApiCall, ApiCallResult, MinecraftClient }
import space.maizy.mozzarella.minecraftclient.ArgSyntax._

class World(name: String, minecraftClient: MinecraftClient) {
  def addBlock(block: Block)(implicit ex: ExecutionContext): Future[Boolean] = {
    // TODO: understand why worlds.world.set_block method doesn't work
    minecraftClient
      .callMethod(buildSetBlockCall(block))
      .map(_.isRight)
  }

  def addBlocks(blocks: List[Block])(implicit ex: ExecutionContext): Future[List[(Block, ApiCallResult)]] = {
    val calls = blocks.map(buildSetBlockCall)
    // FIXME: split into chunks
    minecraftClient.callMethods(calls).map { results =>
      blocks.zip(results).map { case (block, callRes) =>
        (block, callRes)
      }
    }
  }

  private def buildSetBlockCall(block: Block): ApiCall =
    ApiCall(
      "server.run_command",
      List(s"setblock ${block.position.x} ${block.position.y} ${block.position.z} ${block.material.code}".arg)
    )
}
