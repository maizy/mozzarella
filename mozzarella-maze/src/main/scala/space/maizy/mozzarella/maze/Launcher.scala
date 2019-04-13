package space.maizy.mozzarella.maze

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.json4s.native.JsonMethods.{ pretty, render }
import space.maizy.mozzarella.maze.minecraftclient.ArgSyntax._
import space.maizy.mozzarella.maze.minecraftclient.MinecraftClient
import space.maizy.mozzarella.maze.minecraftsdk.{ Block, Materials, Position, World }

object Launcher {
  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val client = new MinecraftClient("http://localhost:25565/api/2", "admin", "changeme")

    val request = client.callMethod(
      "worlds.world.get_block",
      List("world".arg, 6.arg, 4.arg, 1290.arg)
    )

    val res = Await.result(request, 10.seconds)
    res.right.foreach(json => println(pretty(render(json))))

    val world = new World("world", client)

    val baseBlock = Block(Position(6, 4, 1380), Materials.grass)
    val cube10x10 = for (
      x <- 1 to 5;
      y <- 1 to 5;
      z <- 1 to 5
    ) yield Block(
      Position(
        baseBlock.position.x + x,
        baseBlock.position.y + y,
        baseBlock.position.z + z
      ),
      baseBlock.material
    )

    val ready = world.addBlocks(cube10x10.toList)
    println(Await.result(ready, 1.minute))

  }
}
