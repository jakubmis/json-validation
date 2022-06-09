package com.snowplow.routes

import cats.data.EitherT
import cats.effect.Async
import cats.implicits._
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.snowplow.model._
import com.snowplow.storage.SchemaStorage
import com.typesafe.config.Config
import io.circe.Json
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import sttp.model.{Header, StatusCode}
import sttp.tapir._
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s._
import sttp.tapir.server.interceptor.ValuedEndpointOutput
import sttp.tapir.server.interceptor.decodefailure.DefaultDecodeFailureHandler

import scala.concurrent.ExecutionContext

case class JsonValidationRoute[F[_] : Async](schemaStorage: SchemaStorage[F], config: Config)
                                            (implicit ec: ExecutionContext)
  extends TapirJsonCirce
    with Http4sDsl[F] {

  private val postConfiguration: ServerEndpoint.Full[Unit, Unit, (String, Map[String, Json]), FailedResponse, Response, Any, F] =
    endpoint
      .post
      .in("schema" / path[String])
      .in(jsonBody[Map[String, Json]])
      .out(oneOf(oneOfVariant(StatusCode.Created, jsonBody[Response])))
      .errorOut(jsonBody[FailedResponse])
      .serverLogic[F] {
        case (id: String, data: Map[String, Json]) =>
          (for {
            _ <- EitherT(schemaStorage.get(id).flatMap {
              case Some(_) => FailedResponse.failedUpload(id, "schema with given id already exist").asLeft[Unit].pure[F]
              case None => ().asRight[FailedResponse].pure[F]
            })
            _ <- EitherT(schemaStorage.add(id, data).map(_.asRight[FailedResponse]))
          } yield Response.successUpload(id)).value
      }

  private val getConfiguration: ServerEndpoint.Full[Unit, Unit, String, FailedResponse, SchemaResponse, Any, F] =
    endpoint
      .get
      .in("schema" / path[String])
      .out(jsonBody[SchemaResponse])
      .errorOut(jsonBody[FailedResponse])
      .serverLogic[F] {
        id =>
          (for {
            result <- EitherT(schemaStorage.get(id).flatMap {
              case Some(schema) => schema.asRight[FailedResponse].pure[F]
              case None => FailedResponse.failedGet(id, "schema not found").asLeft[SchemaResponse].pure[F]
            })
          } yield result).value
      }

  private val factory = JsonSchemaFactory.byDefault()
  private val postValidationConfiguration: ServerEndpoint.Full[Unit, Unit, (String, Map[String, Json]), FailedResponse, Response, Any, F] =
    endpoint
      .post
      .in("validate" / path[String])
      .in(jsonBody[Map[String, Json]])
      .out(jsonBody[Response])
      .errorOut(jsonBody[FailedResponse])
      .serverLogic[F] {
        case (id: String, data: Map[String, Json]) =>
          (for {
            schemaString <- EitherT(schemaStorage.get(id).flatMap {
              case Some(schema) => schema.asRight[FailedResponse].pure[F]
              case None => FailedResponse.failedValidation(id, "schema not found").asLeft[SchemaResponse].pure[F]
            })
            schema <- EitherT(factory.getJsonSchema(JsonLoader.fromString(schemaString.asJson.noSpaces)).asRight[FailedResponse].pure[F])
            validation <- EitherT(schema.validate(JsonLoader.fromString(data.asJson.deepDropNullValues.noSpaces)).asRight[FailedResponse].pure[F])
          } yield validation)
            .map(_.isSuccess)
            .map(_ => Response.successValidation(id).asRight[FailedResponse])
            .getOrElse(FailedResponse.failedValidation(id, "validation failed").asLeft[Response])
      }

  def failureResponse(c: StatusCode, hs: List[Header], m: String): ValuedEndpointOutput[_] =
    ValuedEndpointOutput(statusCode.and(headers).and(jsonBody[RejectionResponse]), (c, hs, RejectionResponse(m)))

  val routes: HttpRoutes[F] = Http4sServerInterpreter[F](
    Http4sServerOptions.customInterceptors[F, F].copy(
      decodeFailureHandler = DefaultDecodeFailureHandler.default.copy(response = failureResponse)
    ).options
  ).toRoutes(List(postConfiguration, getConfiguration, postValidationConfiguration))

}