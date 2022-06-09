package com.snowplow.model

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import sttp.tapir.Schema

case class FailedResponse(action: String, id: String, status: String, message: String)

object FailedResponse {

  def failedValidation(id: String, message: String) = new FailedResponse("validateDocument", id, "error", message)
  def failedUpload(id: String, message: String) = new FailedResponse("uploadSchema", id, "error", message)
  def failedGet(id: String, message: String) = new FailedResponse("getSchema", id, "error", message)

  private implicit lazy val customConfig: Configuration = Configuration.default.withDefaults
  implicit lazy val schema: Schema[FailedResponse] = Schema.derived[FailedResponse]
  implicit lazy val decoder: Decoder[FailedResponse] = deriveConfiguredDecoder
  implicit lazy val encoder: Encoder[FailedResponse] = deriveConfiguredEncoder
}