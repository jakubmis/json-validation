package com.snowplow

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.snowplow.infrastructure.Database
import com.snowplow.routes.JsonValidationRoute
import com.snowplow.storage.SchemaStorage
import com.typesafe.config.ConfigFactory
import io.circe.Json
import org.http4s.Request
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.client.Client
import org.http4s.client.dsl.io._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.server.Router
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContextExecutor

class JsonValidationTest extends AnyFlatSpec with Matchers with Http4sDsl[IO] with Inputs {

  private implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
  private val config = ConfigFactory.load()
  private val database = Database[IO]()
  private val transactor = database.createTransactor().unsafeRunSync()
  private val schemaStorage = SchemaStorage[IO](transactor)
  private val jsonValidationRoute = JsonValidationRoute[IO](schemaStorage, config)
  private val httpApp = Router("/" -> jsonValidationRoute.routes).orNotFound
  database.createTable(transactor).unsafeRunSync()

  it should "execute test given in github" in {
    val client: Client[IO] = Client.fromHttpApp(httpApp)

    val uploading: Request[IO] = POST(configSchema, uri"/schema/config-schema")
    val uploadingResponse: IO[Json] = client.expect[Json](uploading)
    val uploadingResult: Json = uploadingResponse.unsafeRunSync()
    uploadingResult shouldBe successUpload

    val validation: Request[IO] = POST(json, uri"/validate/config-schema")
    val validationResponse: IO[Json] = client.expect[Json](validation)
    val validationResult: Json = validationResponse.unsafeRunSync()
    validationResult shouldBe successValidation
  }
}
