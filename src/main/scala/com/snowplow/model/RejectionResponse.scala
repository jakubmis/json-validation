package com.snowplow.model

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import sttp.tapir.Schema

case class RejectionResponse(message: String)

object RejectionResponse {
  private implicit lazy val customConfig: Configuration = Configuration.default.withDefaults
  implicit lazy val schema: Schema[RejectionResponse] = Schema.derived[RejectionResponse]
  implicit lazy val decoder: Decoder[RejectionResponse] = deriveConfiguredDecoder
  implicit lazy val encoder: Encoder[RejectionResponse] = deriveConfiguredEncoder
}

