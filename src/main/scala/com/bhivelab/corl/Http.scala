package com.bhivelab.corl

import java.io.{IOException, InputStream}
import java.net.{HttpURLConnection, URL}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by trent ahrens on 3/9/16.
  */
object Http {
  def apply(req: HttpRequest)(implicit executionContext: ExecutionContext): Future[HttpResponse] = {
    Future {
      val conn = new URL(req.url).openConnection().asInstanceOf[HttpURLConnection]
      conn.setDoOutput(true)
      conn.setUseCaches(false)

      req.headers.foreach {
        case (key,value) => conn.setRequestProperty(key,value)
      }

      conn.setRequestMethod(req.verb.verb)

      req.verb match {
        case post =>
          conn.setRequestProperty("Content-Length", req.data.length.toString)
          conn.getOutputStream.write(req.data)
      }

      readFully(conn)
    }
  }

  private def readFully(conn: HttpURLConnection): HttpResponse = {
    def consume(in: InputStream): Array[Byte] = Stream.continually(in.read()).takeWhile(_ != -1).map(_.toByte).toArray
    try {
      HttpResponse(conn.getResponseCode, consume(conn.getInputStream))
    } catch {
      case cause: IOException if conn.getErrorStream != null =>
        HttpResponse(conn.getResponseCode, consume(conn.getInputStream))
    }
  }
}
