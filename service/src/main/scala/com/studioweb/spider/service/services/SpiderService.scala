package com.studioweb.spider.service.services

import java.io.{File, PrintWriter}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.studioweb.spider.entities.SpiderResult
import com.studioweb.spider.service.model.{Menu, MenuHtml, SiteModel, SiteModelHtml}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.concurrent.blocking
import scala.collection.JavaConversions._
import scala.concurrent.{ExecutionContext, Future}

class SpiderService()(implicit executionContext: ExecutionContext, log: Logger, system: ActorSystem,
                      materializer: ActorMaterializer, browser: JsoupBrowser) {

  lazy val configService = ConfigFactory.load()

  def sitesURLs(): List[SiteModel] = {
    def createMenu(config: Config): Menu = Menu(config.getString("name"), config.getString("url"))

    val sites = configService.getStringList("sites.list")

    sites.map { site =>
      val menuList = configService.getConfigList(s"sites.${site}.menu").map(createMenu(_)).toList
      SiteModel(site, configService.getString(s"sites.${site}.home"), menuList)
    }.toList

  }


  def htmlByUrl(url: String): Future[String] = {
    Future(browser.get(url).toHtml)
  }

  def getAllHtmls(): SpiderResult[List[SiteModelHtml]] = SpiderResult {
    log.info(s"Request to create the file.")
    Future.sequence(sitesURLs().map { site =>
      htmlByUrl(site.homeurl).flatMap { homeHtml =>
        Future.sequence(site.menus.map(menu => htmlByUrl(menu.url).map { htmlMenu => MenuHtml(menu.name, htmlMenu) }))
          .map(menusHTML => SiteModelHtml(site.name, homeHtml, menusHTML))
      }
    })
  }


  def createFiles(siteHTMLList: List[SiteModelHtml]): SpiderResult[List[String]] = SpiderResult {

    val dateString = DateTimeFormat.forPattern("yyyyMMdd_HH").print(DateTime.now())
    log.info(s"creating files - list length: ${siteHTMLList.length}. Date String: ${dateString}")

    Future.sequence(siteHTMLList.map { siteHTML =>
      for {
        homeFile <- createLocalFile(siteHTML.html)
        homeFileName <- uploadFileToS3(homeFile, s"${dateString}/${siteHTML.name}/home.html")
        result <- Future.sequence(siteHTML.menus.map { menuHtml =>
          createLocalFile(menuHtml.html).flatMap { menuFile =>
            uploadFileToS3(menuFile, s"${dateString}/${siteHTML.name}/${menuHtml.name}.html")
          }
        })
      } yield (homeFileName :: result)
    }).map(_.flatten)
  }

  def createLocalFile(html: String): Future[File] = Future {
    blocking {
      val path = s"/tmp/${DateTime.now().getMillis}.html"
      val file = new File(path)
      val writer = new PrintWriter(file)
      writer.write(html)
      writer.close()
      file
    }
  }

  def uploadFileToS3(fileToUpload: File, fileName: String): Future[String] = Future {
    blocking {
      val yourAWSCredentials = new BasicAWSCredentials(configService.getString("aws.aws_access_key"),
        configService.getString("aws.aws_secret_key"))
      val amazonS3Client = new AmazonS3Client(yourAWSCredentials)
      amazonS3Client.putObject(configService.getString("aws.bucket"), fileName, fileToUpload)
      fileName
    }
  }
}


object SpiderService {
  def apply()(implicit executionContext: ExecutionContext, log: Logger, system: ActorSystem,
              materializer: ActorMaterializer, browser: JsoupBrowser) = {
    new SpiderService
  }
}

