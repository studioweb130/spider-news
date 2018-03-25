package com.studioweb.spider.service.mail

import org.apache.commons.mail.{HtmlEmail, DefaultAuthenticator, EmailException}


case class EmailMessage(subject: String,
                       recipient: String,
                       from: String,
                       text: String,
                       html: String)

object Mail {

  def sendEmailSync(emailMessage: EmailMessage) {
    try{
      val email = new HtmlEmail()
      email.setTLS(true)
      email.setSSL(false)
      email.setSmtpPort(25)
      email.setHostName("email-smtp.eu-west-1.amazonaws.com")
      email.setAuthenticator(new DefaultAuthenticator(
        "AKIAJK4U4SAGXZPUG22A",
        "Au8swZi+Dawt2GC54mivFc9WemxIyY8rODkKT0XdKDsD"
      ))
      email.setTextMsg(emailMessage.text)
        .addTo(emailMessage.recipient)
        .setFrom(emailMessage.recipient)
        .setSubject(emailMessage.subject)
        .send()
    } catch {
      case e:Exception => {
        println("Create the email messageeeeee")
        println("MESSAGE",e)
      }
    }
  }
}