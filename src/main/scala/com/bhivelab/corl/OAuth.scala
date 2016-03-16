package com.bhivelab.corl

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

import com.bhivelab.corl.HttpVerbs.post
import com.fasterxml.jackson.databind.JsonNode
import com.lolboxen.json._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by trent ahrens on 3/15/16.
  */
object OAuth {
  def getAccessToken(endpoint: String, request: OAuthPasswordTokenRequest)(implicit executionContext: ExecutionContext): Future[OAuthCredentials] = {
    val data = format(Map(
      "grant_type" -> "password",
      "username" -> request.username,
      "password" -> request.password,
      "resource" -> request.resource,
      "client_id" -> request.clientId,
      "client_secret" -> request.clientSecret
    ))

    val headers = Map(
      "Content-Type" -> "application/x-www-form-urlencoded",
      "Charset" ->  "utf-8"
    )

    val httpRequest = HttpRequest(post, endpoint, headers, data)

    val transformer = parse _ andThen throwOnApiError andThen toTokenAccessResponse

    Http(httpRequest).map(transformer)
  }

  def get(endpoint: String, creds: OAuthCredentials)(implicit executionContext: ExecutionContext): Future[Array[Byte]] = {
    val headers = Map(
      "Accept" -> "application/json;odata=verbose",
      "Authorization" ->  s"${creds.tokenType} ${creds.accessToken}"
    )

    val httpRequest = HttpRequest(HttpVerbs.get, endpoint, headers, Array.empty)

    Http(httpRequest).map(_.data)
  }

  private def throwOnApiError(json: JsonNode): JsonNode = {
    if ((json \ "error").nodes.nonEmpty)
      throw new OAuthException(s"received error from iDP: `${json.toString}`")
    json
  }

  private def toTokenAccessResponse(json: JsonNode): OAuthCredentials = {
    (for {
      accessToken <- (json \ "access_token").asOpt[String]
      tokenType <- (json \ "token_type").asOpt[String]
    } yield OAuthCredentials(accessToken, tokenType))
      .getOrElse(throw new OAuthException(s"expected to find access_token and token_type but found none in `${json.toString}`"))
  }

  private def parse(s: HttpResponse): JsonNode = {
    try {
      Json.parse(new String(s.data, UTF_8))
    } catch {
      case cause: Throwable => throw new Exception(s"could not parse as json: `$s`", cause)
    }
  }

  private def format(m: Map[String,String]): Array[Byte] = {
    def e(p: String) = URLEncoder.encode(p, UTF_8.displayName())
    m.map(kv => s"${kv._1}=${e(kv._2)}").mkString("&").getBytes(UTF_8)
  }
}
