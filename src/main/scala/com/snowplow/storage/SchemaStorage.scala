package com.snowplow.storage

import cats.effect.Async
import com.snowplow.model.SchemaResponse
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.EncoderOps

case class SchemaStorage[F[_]: Async](transactor: Transactor[F]) {

  def add(id: String, schema: Map[String, Json]): F[SchemaResponse] = {
    sql"""INSERT INTO schemas
           (id, schema)
          VALUES (
            $id, ${schema.asJson.noSpaces}
         )
         """.update
      .withUniqueGeneratedKeys[SchemaResponse]("id", "schema")
      .transact(transactor)
  }

  def get(id: String): F[Option[SchemaResponse]] = {
    sql"""SELECT id, schema
          FROM schemas
          WHERE id = $id
       """
      .query[SchemaResponse]
      .option
      .transact(transactor)
  }
}
