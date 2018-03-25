package com.studioweb.spider.service.model


case class SiteModel(name: String, homeurl: String, menus: List[Menu])

case class Menu(name: String, url: String)


case class SiteModelHtml(name: String, html: String, menus: List[MenuHtml])

case class MenuHtml(name: String, html: String)