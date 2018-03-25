package com.studioweb.spider

import cats.data.EitherT

import scala.concurrent.Future

package object entities {
  type Result[+A] = Either[Error, A]
  type SpiderResult[A] = EitherT[Future, Error, A]
}
