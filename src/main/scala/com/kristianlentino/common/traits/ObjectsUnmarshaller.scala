package com.kristianlentino.common.traits
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.kristianlentino.domain.models.{AccessTokenResponse, Institute, InstituteList}
import com.kristianlentino.infrastructure.http.requests.LinkBuilderRequest
import com.kristianlentino.infrastructure.http.responses.{CreateBankLinkResponse, Status}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat


trait ObjectsUnmarshaller extends SprayJsonSupport with EnumUnmarshaller {

  implicit val accessTokenResponseFormat: RootJsonFormat[AccessTokenResponse] = jsonFormat4(AccessTokenResponse)
  implicit val institutions: RootJsonFormat[Institute] = jsonFormat6(Institute)
  implicit val instituteListMapper: RootJsonFormat[InstituteList] = jsonFormat1(InstituteList)
  implicit val linkBuilderRequestMapper: RootJsonFormat[LinkBuilderRequest] = jsonFormat2(LinkBuilderRequest)

  //create bank link implicits
  implicit val statusUnmarshaller: RootJsonFormat[Status.Value] = enumFormat(Status)
  implicit val createBankLinkUnmarshaller: RootJsonFormat[CreateBankLinkResponse] = jsonFormat9(CreateBankLinkResponse)
}
