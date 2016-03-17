package com.bhivelab.corl

import java.util.concurrent.TimeUnit

import com.ning.http.client.{AsyncHttpClient, Request, RequestBuilder, Response}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by trent ahrens on 3/9/16.
  */
object Http {
  def apply(req: HttpRequest)(implicit executionContext: ExecutionContext): Future[HttpResponse] = {
    Future {
      val client = new AsyncHttpClient()
      val response = client.executeRequest(requestTransform(req)).get(60, TimeUnit.SECONDS)
      client.close()
      responseTransform(response)
    }
  }

  private def requestTransform(req: HttpRequest): Request = {
    val rb = new RequestBuilder(req.verb.verb).setUrl(req.url)
    req.headers.foreach {
      case (key,value) => rb.setHeader(key,value)
    }

    req.data match {
      case Some(body) => rb.setBody(body)
      case None =>
    }

    rb.build()
  }

  private def responseTransform(resp: Response): HttpResponse =
    HttpResponse(resp.getStatusCode, resp.getResponseBodyAsBytes)
}
