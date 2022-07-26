package com.kristianlentino.common.traits
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.kristianlentino.domain.models.{Institute, InstituteList, AccessTokenResponse}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

trait ObjectsUnmarshaller extends SprayJsonSupport{

  implicit val accessTokenResponseFormat: RootJsonFormat[AccessTokenResponse] = jsonFormat4(AccessTokenResponse)
  implicit val institutions: RootJsonFormat[Institute] = jsonFormat6(Institute)
  implicit val instituteListMapper: RootJsonFormat[InstituteList] = jsonFormat1(InstituteList)
}
