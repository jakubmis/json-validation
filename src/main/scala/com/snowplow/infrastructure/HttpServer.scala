package com.snowplow.infrastructure

import cats.effect.ExitCode
import cats.effect.kernel.Async
import cats.syntax.all._
import com.snowplow.routes.JsonValidationRoute
import com.typesafe.config.Config
import fs2.Stream
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.dsl.io.{->, /, GET, Root}
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.{HttpRoutes, Response, Status}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt


class HttpServer[F[_] : Async](ec: ExecutionContext, config: Config, jsonValidationRoute: JsonValidationRoute[F]) {

  private val healthService: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "meta" / "ready" =>
      for {
        result <- Response[F](status = Status.Ok).withEntity("Ready").pure[F]
      } yield result
    }

  private val httpApp = Router("/" -> (healthService <+> jsonValidationRoute.routes)).orNotFound

  val stream: Stream[F, ExitCode] = BlazeServerBuilder
    .apply[F](ec)
    .withConnectorPoolSize(1)
    .withoutBanner
    .withResponseHeaderTimeout(2.seconds)
    .withIdleTimeout(5.seconds)
    .bindHttp(config.getInt("http.port"), config.getString("http.interface"))
    .withHttpApp(httpApp)
    .serve

}

object HttpServer {

  def apply[F[_] : Async](ec: ExecutionContext,
                          config: Config,
                          jsonValidationRoute: JsonValidationRoute[F]
                         ): F[Stream[F, ExitCode]] = {
    new HttpServer(ec, config, jsonValidationRoute).stream.pure[F]
  }

}
