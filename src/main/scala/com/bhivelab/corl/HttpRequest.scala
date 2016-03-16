package com.bhivelab.corl

/**
  * Created by trent ahrens on 3/16/16.
  */

object HttpVerbs {
  val get = HttpVerb("GET")
  val post = HttpVerb("POST")
}

case class HttpVerb(verb: String)

case class HttpRequest(verb: HttpVerb, url: String, headers: Map[String,String], data: Array[Byte])

