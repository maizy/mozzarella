package space.maizy.mozzarella.minecraftclient

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2018
 * See LICENSE.txt for details.
 */

import java.nio.charset.StandardCharsets.UTF_8
import scala.concurrent.{ ExecutionContext, Future }
import cats.data.NonEmptyList
import cats.syntax.either._
import dispatch._
import org.json4s.JsonAST.JArray
import org.json4s.{ JObject, JString, JValue }
import org.json4s.native.JsonMethods.{ compact, parseOpt, render }

class MinecraftClient(
  jsonapiBaseUrl: String,
  username: String,
  password: String
) {
  private val httpClient: Http = Http.default

  // TODO: multi actions requests

  def callMethod(methodName: String, arguments: List[Argument])
                (implicit ec: ExecutionContext): Future[Either[NonEmptyList[String], JValue]] = {
    val payload = JArray(List(buildCall(methodName, arguments)))

    val request = url(s"${jsonapiBaseUrl.stripSuffix("/")}/call")
      .POST
      .setBody(compact(render(payload)))
      .setContentType("application/json", UTF_8)

    httpClient(request > as.Response(r => r)).map { resp =>
      resp.getStatusCode match {
        case 200 =>
          parseOpt(resp.getResponseBodyAsStream) match {
            case Some(JArray(List(res: JObject))) => res.asRight

            // TODO: describe parse errors
            case _ => NonEmptyList.of("Non parsable response body").asLeft
          }

        // TODO: error object, parse error body
        case i =>
          NonEmptyList.of(s"Error in http request. Status code: $i.\n${resp.getResponseBody}}").asLeft
      }
    }
  }

  private def buildCall(methodName: String, arguments: List[Argument]): JObject = {
    JObject(
      "name" -> JString(methodName),
      "username" -> JString(username),
      "key" -> JString(genKey(methodName)),
      "arguments" -> JArray(arguments.map(_.toJsonObject))
    )
  }

  private def genKey(methodName: String): String = HashingUtils.sha256(s"$username$methodName$password")
}
