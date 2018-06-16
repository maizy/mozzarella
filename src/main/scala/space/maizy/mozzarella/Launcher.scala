package space.maizy.mozzarella

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2018
 * See LICENSE.txt for details.
 */

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import space.maizy.mozzarella.minecraftclient.MinecraftClient
import space.maizy.mozzarella.minecraftclient.ArgSyntax._
import org.json4s.native.JsonMethods.{ pretty, render }

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

  }
}
