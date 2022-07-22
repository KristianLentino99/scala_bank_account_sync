package com.kristianlentino.common.traits
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.kristianlentino.common.models.{Institute, InstituteList, NordigenAccessTokenResponse}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

trait NordigenUnmarshaller extends SprayJsonSupport{

  implicit val nordigenAccessTokenResponseFormat: RootJsonFormat[NordigenAccessTokenResponse] = jsonFormat4(NordigenAccessTokenResponse)
  implicit val institutions: RootJsonFormat[Institute] = jsonFormat6(Institute)
  implicit val instituteListMapper: RootJsonFormat[InstituteList] = jsonFormat1(InstituteList)
}
