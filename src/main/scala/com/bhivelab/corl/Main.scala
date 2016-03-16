package com.bhivelab.corl

import java.net.URL
import java.nio.charset.StandardCharsets.ISO_8859_1

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
  * Created by trent ahrens on 3/15/16.
  */
object Main extends App {
  implicit val ec = scala.concurrent.ExecutionContext.global

  val log = LoggerFactory.getLogger(getClass)

  val config = ConfigFactory.load().getConfig("corl")

  val tokenUrl = config.getString("token-authority")

  val restUrl = args(0)
  val resource = {
    val url = new URL(restUrl)
    s"${url.getProtocol}://${url.getHost}/"
  }

  val oAuthRequest = OAuthPasswordTokenRequest(
    config.getString("username"),
    config.getString("password"),
    resource,
    config.getString("client-id"),
    config.getString("client-secret")
  )

  val waitOn = OAuth.getAccessToken(tokenUrl, oAuthRequest).flatMap(token =>
    OAuth.get(restUrl, token).map(a => new String(a, ISO_8859_1))
  )

  Await.ready(waitOn, 3 minutes)

  waitOn.value.get match {
    case Failure(cause) => log.error("",cause)
    case Success(result) => System.out.println(result)
  }
}
