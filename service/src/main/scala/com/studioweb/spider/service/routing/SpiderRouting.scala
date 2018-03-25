package com.studioweb.spider.service.routing

import akka.http.scaladsl.server
import com.studioweb.spider.service.providers.ContextProvider
import com.studioweb.spider.service.services.SpiderService
import com.typesafe.scalalogging.LazyLogging
import cats.implicits._

trait SpiderRouting extends LoggingDirective with ResponseDirectives with RequestDirective with LazyLogging {

  this: ContextProvider =>

  lazy val spiderService = SpiderService()(executionContext, logger, system, materializer, browser)

  val spiderResources: server.Route =
    get {
      pathPrefix("files") {
        pathPrefix("all") {
          pathEndOrSingleSlash {
            completeSpiderService {
              for {
                htmls <- spiderService.getAllHtmls()
                completeListResult <- spiderService.createFiles(htmls)
              } yield completeListResult
            }
          }
        }
      } ~ {
        pathPrefix("ping") {
          pathEndOrSingleSlash {
            complete("PING OK.")
          }
        }
      }
    }
}
