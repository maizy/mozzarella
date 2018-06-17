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

final case class ApiCall(method: String, arguments: List[Argument])
final case class ApiCallResult(call: ApiCall, result: Either[NonEmptyList[String], JObject])

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
    val request = buildRequest(payload)

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

  def callMethod(apiCall: ApiCall)(implicit ec: ExecutionContext): Future[Either[NonEmptyList[String], JValue]] =
    callMethod(apiCall.method, apiCall.arguments)

  def callMethods(calls: List[ApiCall])(implicit ec: ExecutionContext): Future[List[ApiCallResult]] = {
    val indexed = calls.zipWithIndex.map { case (c, i) => i.toString -> c }.toMap

    val callsJson = indexed
      .map{ case (i, c) => buildCall(c.method, c.arguments, Some(i)) }
      .toList
    val payload = JArray(callsJson)
    val req = buildRequest(payload)

    httpClient(req > as.Response(r => r)).map { resp =>
      resp.getStatusCode match {
        case 200 =>
          parseOpt(resp.getResponseBodyAsStream) match {
            case Some(results: JArray) =>
              val returnedResults = results.arr
                .collect {
                  case result: JObject if result.obj.exists(_._1 == "tag") &&
                      result.obj.find(_._1 == "tag").get._2.isInstanceOf[JString] =>
                    val id = result.obj.find(_._1 == "tag").get._2.asInstanceOf[JString].s
                    // TODO: parse operation result
                    id -> result
                }.toMap

              indexed.map{ case (id, call) =>
                returnedResults.get(id) match {
                  case Some(result) => ApiCallResult(call, result.asRight)
                  case _ => ApiCallResult(call, NonEmptyList.one("Result is missing").asLeft)
                }
              }.toList

            // TODO: describe parse errors
            case _ =>
              val error = NonEmptyList.of("Non parsable response body").asLeft
              calls.map(c => ApiCallResult(c, error))

          }

        // TODO: error object, parse error body
        case i =>
          val error = NonEmptyList.of(s"Error in http request. Status code: $i.\n${resp.getResponseBody}}").asLeft
          calls.map(c => ApiCallResult(c, error))
      }
    }
  }

  private def buildRequest(payload: JArray) = {
    url(s"${jsonapiBaseUrl.stripSuffix("/")}/call")
      .POST
      .setBody(compact(render(payload)))
      .setContentType("application/json", UTF_8)
  }

  private def buildCall(methodName: String, arguments: List[Argument], tag: Option[String] = None): JObject = {
    val callFields = List(
      "name" -> JString(methodName),
      "username" -> JString(username),
      "key" -> JString(genKey(methodName)),
      "arguments" -> JArray(arguments.map(_.toJsonObject))
    )

    val fieldsWithTag = tag match {
      case Some(t) => ("tag" -> JString(t)) +: callFields
      case _ => callFields
    }
    JObject(fieldsWithTag: _*)
  }

  private def genKey(methodName: String): String = HashingUtils.sha256(s"$username$methodName$password")
}
