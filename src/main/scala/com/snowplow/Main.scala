package com.snowplow

import cats.effect.{ExitCode, IO, IOApp, Sync}
import com.snowplow.infrastructure.{Database, HttpServer}
import com.snowplow.routes.JsonValidationRoute
import com.snowplow.storage.SchemaStorage
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor

object Main extends IOApp {

  private implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  override def run(args: List[String]): IO[ExitCode] =
    for {
      config <- Sync[IO].delay(ConfigFactory.load())
      database = Database[IO]()
      transactor <- database.createTransactor()
      _ <- database.createTable(transactor)
      schemaStorage = SchemaStorage[IO](transactor)
      jsonValidationRoute = JsonValidationRoute[IO](schemaStorage, config)
      server <- HttpServer[IO](ec, config, jsonValidationRoute)
      exitCode <- server.compile.drain.as(ExitCode.Success)
    } yield exitCode
}
