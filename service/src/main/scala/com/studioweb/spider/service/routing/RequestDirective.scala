package com.studioweb.spider.service.routing

import akka.http.scaladsl.server.{Directive1, MissingHeaderRejection, ValidationRejection}
import akka.http.scaladsl.server.directives._
import com.studioweb.spider.entities.entities.ChannelEnum
import com.typesafe.config.ConfigFactory

trait RequestDirective extends RouteDirectives with BasicDirectives with HeaderDirectives {

  val brandIdHeader = "X-Brand-ID"

  protected def validBrandId(brand: String): Boolean = ConfigFactory.load.getConfig("cms").hasPath(brand)

  def withBrand: Directive1[ChannelEnum] = {
    optionalHeaderValueByName(brandIdHeader).flatMap {
      case Some(header) =>
        if (validBrandId(header)) provide(ChannelEnum(header).get) else reject(ValidationRejection("Brand"))
      case _ => reject(MissingHeaderRejection(brandIdHeader))
    }
  }
}
