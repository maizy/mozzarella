package space.maizy.mozzarella

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2018
 * See LICENSE.txt for details.
 */

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.{ Duration, DurationInt }
import space.maizy.mozzarella.minecraftclient.MinecraftClient
import space.maizy.mozzarella.minecraftclient.ArgSyntax._
import org.json4s.native.JsonMethods.{ pretty, render }
import space.maizy.mozzarella.minecraftsdk.{ Block, Materials, Position, World }

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

    val baseBlock = Block(Position(6, 4, 1350), Materials.grass)
    val cube10x10 = for (
      x <- 1 to 10;
      y <- 1 to 10;
      z <- 1 to 10
    ) yield Block(
      Position(
        baseBlock.position.x + x,
        baseBlock.position.y + y,
        baseBlock.position.z + z
      ),
      baseBlock.material
    )

    val ready = Future.sequence(cube10x10.map(world.addBlock))
    Await.result(ready, Duration.Inf)

  }
}
