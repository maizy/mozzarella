package space.maizy.mozzarella.maze.minecraftsdk

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2018
 * See LICENSE.txt for details.
 */

final case class Material(code: String, data: Int)

// TODO: codegen all materials
object Materials {
  val stone: Material = Material("stone", 1)
  val grass: Material = Material("grass", 2)

  val all = List(stone, grass)
}
