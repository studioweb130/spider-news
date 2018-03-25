package com.studioweb.spider.entities.json

import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.joda.time.{LocalDateTime, LocalDate}
import org.joda.time.format.DateTimeFormat
import org.json4s.ext.JodaTimeSerializers
import org.json4s._
import org.json4s.native

trait JsonFormats extends JodaSerializers with Json4sSupport{
  implicit val pretty = Json4sSupport.ShouldWritePretty.True
  implicit val jsonFormats = DefaultFormats ++ JodaTimeSerializers.all ++ jodaSerialisers
  implicit val serialization = native.Serialization
}

trait JodaSerializers {

  lazy val cmsDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

  private val jodaDateFormatter = DateTimeFormat.forPattern("YYYY-MM-dd")
  private val jodaDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

  private val localDateSerialiser = new CustomSerializer[LocalDate](f => (
    { case JString(s) => jodaDateFormatter.parseLocalDate(s) },
    { case d: LocalDate => JString(jodaDateFormatter.print(d)) }
    ))

  private val localDateTimeSerialiser = new CustomSerializer[LocalDateTime](f => (
    { case JString(s) => jodaDateTimeFormatter.parseLocalDateTime(s) },
    { case d: LocalDateTime => JString(jodaDateTimeFormatter.print(d)) }
    ))

  val jodaSerialisers = List[Serializer[_]](localDateSerialiser, localDateTimeSerialiser)
}

