package com.studioweb.spider.service.routing

import akka.event.Logging.LogLevel
import akka.event.LoggingAdapter
import akka.http.scaladsl.model.{HttpEntity, HttpRequest}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.RouteResult.Complete
import akka.http.scaladsl.server.directives.{LogEntry, _}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink

import scala.concurrent.{ExecutionContext, Future}

trait LoggingDirective {

  def logRequestResult(level: LogLevel, route: server.Route)(implicit m: Materializer, ex: ExecutionContext) =
    DebuggingDirectives.logRequestResult(LoggingMagnet(log => format(log, level)))(route)

  private def entityAsString(entity: HttpEntity)
    (implicit m: Materializer, ex: ExecutionContext): Future[String] = {
    entity.dataBytes
      .map(_.decodeString(entity.contentType.charsetOption.get.value))
      .runWith(Sink.head)
  }

  private def format(logger: LoggingAdapter, level: LogLevel)(request: HttpRequest)(res: Any)
    (implicit m: Materializer, ex: ExecutionContext): Unit = {
    val entry = res match {
      case Complete(response) =>
        entityAsString(response.entity).map(data ⇒ Seq(
          LogEntry(s"Request: ${request.method.name()} ${request.uri} ${response.status}", level),
          LogEntry(s"Response: ${request.method.name()} ${request.uri} ${response.status} $data", level)
        ))
      case other =>
        Future.successful(Seq(LogEntry(s"$other", level)))
    }
    entry.map(_.foreach(_.logTo(logger)))
  }

}
