# json-validation-service
json-validation-service

1. Compile project using sbt compile
2. Use your favourite IDE to run com.snowplow.Main
3. Use sbt test to run tests
------------
Validate json against json schema

Using this specification you will need to build a REST-service for validating JSON documents against JSON Schemas.
This REST-service should allow users to upload JSON Schemas and store them at unique URI and then validate JSON documents against these URIs.
Additionally, this service will "clean" every JSON document before validation: remove keys for which the value is null.
Client-side software as well as GUI is out of scope of this specification - user can choose any tool able to communicate via HTTP like curl or write their own.
------------
Requirements:
- POST    /schema/SCHEMAID        - Upload a JSON Schema with unique `SCHEMAID`
- GET     /schema/SCHEMAID        - Download a JSON Schema with unique `SCHEMAID`
- POST    /validate/SCHEMAID      - Validate a JSON document against the JSON Schema identified by `SCHEMAID`