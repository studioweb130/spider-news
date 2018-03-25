package com.studioweb.spider.entities


sealed trait Error extends Product with Serializable {
  def code: String
  def message: String
}

case class SpiderError(code: String = "SERVICE_ERROR", message: String = "Service Error")  extends Error