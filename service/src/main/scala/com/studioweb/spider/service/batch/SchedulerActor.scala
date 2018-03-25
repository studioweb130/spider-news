package com.studioweb.spider.service.batch

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import com.studioweb.spider.service.mail.{EmailMessage, Mail}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext
import scala.util.Random


class SchedulerActor()(implicit system: ActorSystem, materializer: ActorMaterializer) extends Actor with LazyLogging {

  implicit val executionContext: ExecutionContext = system.dispatcher

  override def receive: Receive = {

    case "GetNewsInternet" => {

      logger.info("Message GetNewsInternet. Gathering news...")
      val sleepTime:Long = Random.nextInt(7200000).toLong
      logger.info(s"Going to sleep - ${sleepTime} milliseconds")
      Thread.sleep(sleepTime) // TODO: move to akka scheduler = system.scheduler.scheduleOnce(50 milliseconds, testActor, "foo")

      logger.info("Http call.")

      Http().singleRequest(HttpRequest(uri = "http://spider-env.eu-west-1.elasticbeanstalk.com/files/all")).map { success =>
        logger.info(s"HTTP = success")
        Mail.sendEmailSync(EmailMessage(s"News - OK. Sleeping ${sleepTime} milliseconds", "jbarbero@estudioweb130.com", "jsbarbero@gmail.com", success.entity.toString, "done"))
      }.recover {
        case e: Exception => Mail.sendEmailSync(EmailMessage("News Batch executed - ERROR", "jbarbero@estudioweb130.com", "jsbarbero@gmail.com", s"Exception: ${e.getMessage}", "done"))
      }

    }
    case _ => {
      Mail.sendEmailSync(EmailMessage("News Batch executed - WRONG MESSAGE", "jbarbero@estudioweb130.com", "jsbarbero@gmail.com", "Error, message not valid.", "hola2"))
      logger.error("Error, message not valid.")

    }

  }
}


object SchedulerActor {
  def props(system: ActorSystem, materializer: ActorMaterializer) = Props(new SchedulerActor()(system, materializer))
}