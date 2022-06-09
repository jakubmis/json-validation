package com.snowplow.model

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import sttp.tapir.Schema

case class Response(action: String, id: String, status: String)

object Response {

  def successUpload(id: String) = new Response("uploadSchema", id, "success")
  def successValidation(id: String) = new Response("validateDocument", id, "success")

  private implicit lazy val customConfig: Configuration = Configuration.default.withDefaults
  implicit lazy val schema: Schema[Response] = Schema.derived[Response]
  implicit lazy val decoder: Decoder[Response] = deriveConfiguredDecoder
  implicit lazy val encoder: Encoder[Response] = deriveConfiguredEncoder
}