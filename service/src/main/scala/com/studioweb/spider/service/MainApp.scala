package com.studioweb.spider.service

import akka.actor.{Props, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.studioweb.spider.service.batch.SchedulerActor
import com.studioweb.spider.service.providers.ContextProvider
import com.studioweb.spider.service.routing.SpiderRouting
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

class MainApp extends SpiderRouting with LazyLogging with ContextProvider {

  val config = ConfigFactory.load()
  implicit val system = ActorSystem("spider")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val browser: JsoupBrowser = JsoupBrowser()


  val batchActorSystem = ActorSystem("batch")
  val scheduler = QuartzSchedulerExtension(batchActorSystem)
  val batchActor = system.actorOf(SchedulerActor.props(batchActorSystem, materializer))


  def startup() = {

    logger.info("Starting spider batch...")
    scheduler.schedule("GetNewsInternet", batchActor, "GetNewsInternet")
    logger.info("Batch running")

    logger.info("Starting spider service...")
    Http().bindAndHandle(
      logRequestResult(Logging.InfoLevel, spiderResources),
      config.getString("http.interface"),
      config.getInt("http.port")
    ).onComplete {
      case Success(_) =>
        logger.info("Spider service is up")
      case Failure(e) => {
        logger.error(e.getMessage)
        e.printStackTrace()
        sys.exit(1)
      }
    }
  }


}

object MainApp extends App {
  new MainApp().startup
}

