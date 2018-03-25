package com.studioweb.spider.entities.entities

trait Enum[A] {
  val values: List[A]
  def apply(s: String): Option[A] = values.find(_.toString.toLowerCase == s.toLowerCase)
}

sealed trait ChannelEnum extends Product with Serializable {
  def name: String
}

object ChannelEnum extends Enum[ChannelEnum] {
  case object BBC extends ChannelEnum {
    val name = "bbc"
  }

  val values = List(BBC)
}

