package com.snowplow

import io.circe.literal.JsonStringContext

trait Inputs {
  val successUpload =
    json"""
     {
        "action": "uploadSchema",
        "id": "config-schema",
        "status": "success"
      }"""

  val successValidation =
    json"""
      {
        "action": "validateDocument",
        "id": "config-schema",
        "status": "success"
      }
      """

  val json =
    json"""
      {
        "source": "/home/alice/image.iso",
        "destination": "/mnt/storage",
        "timeout": null,
        "chunks": {
          "size": 1024,
          "number": null
        }
      }"""

  val configSchema =
    json"""{
            "schema": "http://json-schema.org/draft-04/schema#",
            "type": "object",
            "properties": {
              "source": {
                "type": "string"
              },
              "destination": {
                "type": "string"
             },
             "timeout": {
               "type": "integer",
               "minimum": 0,
               "maximum": 32767
             },
             "chunks": {
               "type": "object",
               "properties": {
                 "size": {
                   "type": "integer"
                 },
                 "number": {
                   "type": "integer"
                 }
               },
               "required": ["size"]
             }
           },
           "required": ["source", "destination"]
         }"""
}
