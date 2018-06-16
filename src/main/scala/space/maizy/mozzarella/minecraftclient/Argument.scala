package space.maizy.mozzarella.minecraftclient

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2018
 * See LICENSE.txt for details.
 */

import org.json4s.{ JValue, JInt, JString }

sealed trait Argument {
  def toJsonObject: JValue
}

final case class IntArgument(value: Int) extends Argument {
  override def toJsonObject: JValue = JInt(value)
}

final case class StringArgument(value: String) extends Argument {
  override def toJsonObject: JValue = JString(value)
}

object ArgSyntax {
  implicit class IntArg(value: Int) {
    def arg: Argument = IntArgument(value)
  }
  implicit class StringArg(value: String) {
    def arg: Argument = StringArgument(value)
  }
}
