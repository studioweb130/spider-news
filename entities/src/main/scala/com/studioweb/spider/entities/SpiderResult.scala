package com.studioweb.spider.entities

import cats.data.EitherT
import scala.concurrent.{ExecutionContext, Future}
import cats.implicits._

object SpiderResult {

  def apply[A](f: Future[A])(implicit ec: ExecutionContext): SpiderResult[A] =
    apply(f.map(Right.apply).recover {
      case err: Error => Left(err)
      case _ => Left(SpiderError("Unknown Error"))
    })

  def apply[A](f: => Result[A])(implicit ec: ExecutionContext): SpiderResult[A] =
    apply(Future(f))

  def apply[A](f: Future[Result[A]]): SpiderResult[A] =
    EitherT(f)

  def errored[A](error: Error): SpiderResult[A] =
    apply(Future.successful(Left(error)))

  def errored[A](error: Left[Error, Nothing]): SpiderResult[A] =
    apply(Future.successful(error))

  def failed[A](ex: Throwable): SpiderResult[A] =
    apply(Future.failed(ex))

  def successful[A](result: A): SpiderResult[A] =
    apply(Future.successful(Right(result)))

  def successfulFuture[A](result: A)(implicit ec: ExecutionContext): SpiderResult[A] =
    apply(Right(result))

  def successful[A](result: Result[A]): SpiderResult[A] =
    apply(Future.successful(result))

  def foldFutureRight[A](f: => List[SpiderResult[A]])(implicit ec: ExecutionContext): SpiderResult[List[A]] = {
    f.foldRight[SpiderResult[List[A]]](SpiderResult.successful(List())) { (elem, acc) =>
      acc.flatMap(list => elem.map(_ :: list))
    }
  }

  def foldFutureLeft[A](f: => List[SpiderResult[A]])(implicit ec: ExecutionContext): SpiderResult[List[A]] = {
    f.foldLeft[SpiderResult[List[A]]](SpiderResult.successful(List[A]())) { (acc, elem) =>
      acc.flatMap(list => elem.map(list :+ _))
    }
  }

  def sequenceU[T](seq: List[SpiderResult[T]])(implicit ec: ExecutionContext): SpiderResult[List[T]] = {
    seq.sequenceU
  }

}
