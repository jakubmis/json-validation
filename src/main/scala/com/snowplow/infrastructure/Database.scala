package com.snowplow.infrastructure

import cats.effect.Async
import cats.implicits.toFunctorOps
import doobie.ExecutionContexts
import doobie.h2.H2Transactor
import doobie.implicits._

case class Database[F[_]: Async]() {

  def createTransactor(): F[H2Transactor[F]] =
    (for {
      ce <- ExecutionContexts.fixedThreadPool[F](32)
      xa <- H2Transactor.newH2Transactor[F]("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "", ce)
    } yield xa).allocated.map(_._1)

  def createTable(transactor: H2Transactor[F]): F[Int] =
    sql"""
    CREATE TABLE schemas (
      id VARCHAR PRIMARY KEY,
      schema VARCHAR NOT NULL
    )
  """.update.run.transact(transactor)
}
