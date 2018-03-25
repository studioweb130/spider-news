package com.studioweb.spider.service.routing

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.{Directives, MissingCookieRejection, RejectionHandler, Route, _}
import akka.http.scaladsl.model.StatusCodes._
import com.studioweb.spider.entities.{SpiderError, SpiderResult}
import com.studioweb.spider.entities.json.JsonFormats

trait ResponseDirectives extends Directives with JsonFormats {

  implicit def ontrackRejectionHandler =
    RejectionHandler.newBuilder()
      .handle {
        case AuthorizationFailedRejection ⇒
          complete(HttpResponse(
            status = Forbidden,
            entity = HttpEntity(ContentTypes.`application/json`,
              serialization.writePretty(SpiderError("AuthorizationFailedRejection", "Forbidden request")))))

        case MissingCookieRejection(cookieName) =>
        complete(HttpResponse(
          status = BadRequest,
          entity = HttpEntity(ContentTypes.`application/json`,
            serialization.writePretty(SpiderError("MissingCookieRejection", s"${cookieName} not found")))))

        case MissingHeaderRejection(msg) ⇒
          complete(HttpResponse(
            status = BadRequest,
            entity = HttpEntity(ContentTypes.`application/json`,
              serialization.writePretty(SpiderError("MissingHeaderRejection", s"${msg} missing")))))

        case ValidationRejection(msg, _) ⇒
          complete(HttpResponse(
            status = BadRequest,
            entity = HttpEntity(ContentTypes.`application/json`,
              serialization.writePretty(SpiderError("ValidationRejection", s"${msg} not valid")))))

        case MethodRejection(supported) ⇒
          complete(HttpResponse(
            status = MethodNotAllowed,
            entity = HttpEntity(ContentTypes.`application/json`,
              serialization.writePretty(SpiderError("MethodNotAllowed", s"Method Supported: ${supported.value}")))))

        case _ ⇒
          complete(HttpResponse(
            status = InternalServerError,
            entity = HttpEntity(ContentTypes.`application/json`,
              serialization.writePretty(SpiderError("InternalServerError", "Unknown error.")))))

      }
      .handleNotFound {
        complete(HttpResponse(
          status = NotFound,
          entity = HttpEntity(ContentTypes.`application/json`,
            serialization.writePretty(SpiderError("NotFound", "Request not found.")))))
      }
      .result()


  def completeSpiderService[T: ToResponseMarshaller](f: SpiderResult[T]): Route =
    onSuccess(f.value) {
      case Left(error: Error) => complete(HttpResponse(
        status = BadRequest,
        entity = HttpEntity(ContentTypes.`application/json`, serialization.writePretty(SpiderError)))
      )
      case Left(spiderError: SpiderError) => complete(HttpResponse(
        status = InternalServerError,
        entity = HttpEntity(ContentTypes.`application/json`, serialization.writePretty(spiderError)))
      )
      case Right(success) => complete(ToResponseMarshallable(OK -> success))
    }

}
