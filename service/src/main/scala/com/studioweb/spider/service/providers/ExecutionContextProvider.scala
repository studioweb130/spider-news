package com.studioweb.spider.service.providers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import scala.concurrent.ExecutionContext

trait ContextProvider {
  implicit val executionContext: ExecutionContext
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit val browser: JsoupBrowser
}
