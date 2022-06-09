package com.snowplow.model

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import sttp.tapir.Schema

case class SchemaResponse(id: String, data: String)

object SchemaResponse {
  private implicit lazy val customConfig: Configuration = Configuration.default.withDefaults
  implicit lazy val decoder: Decoder[SchemaResponse] = deriveConfiguredDecoder
  implicit lazy val encoder: Encoder[SchemaResponse] = deriveConfiguredEncoder
  implicit lazy val schema: Schema[SchemaResponse] = Schema.derived[SchemaResponse]
}
