package com.kristianlentino.common.traits

trait EnumUnmarshaller {
  import spray.json._
  import spray.json.DefaultJsonProtocol._

  // enum is implicit here, that's why we needed implicit objects
  implicit def enumFormat[A <: Enumeration](implicit enum: A): RootJsonFormat[enum.Value] =
    new RootJsonFormat[enum.Value] {
      def read(value: JsValue): enum.Value = value match {
        case JsString(s) =>
          enum.withName(s)
        case x =>
          deserializationError("Expected JsString, but got " + x)
      }

      def write(obj: enum.Value) = JsString(obj.toString)
    }
}
