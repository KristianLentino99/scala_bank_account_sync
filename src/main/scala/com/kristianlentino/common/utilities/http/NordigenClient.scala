package com.kristianlentino.common.utilities.http

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.javadsl.model.headers.Authorization
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.kristianlentino.common.models.{Institute, NordigenAccessTokenResponse}
import com.kristianlentino.common.traits.NordigenUnmarshaller
import com.typesafe.config.Config
import spray.json.DefaultJsonProtocol.listFormat

import scala.concurrent.{ExecutionContextExecutor, Future}

class NordigenClient(implicit config: Config) extends NordigenUnmarshaller {


  private val NORDIGEN_BASE_URL = "https://ob.nordigen.com/"
  private val API_VERSION = "v2"
  private val API_PREFIX = "api"
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "NordigenRequest")
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  private def buildApiURL: String = s"$NORDIGEN_BASE_URL$API_PREFIX/$API_VERSION/"
  def getAccessToken: Future[NordigenAccessTokenResponse] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = s"${buildApiURL}token/new/" ,
        entity = HttpEntity(ContentTypes.`application/json`,
          s"""
             | {     "secret_id":"${config.getString("my-app.nordigen.secret-id")}",     "secret_key":"${config.getString("my-app.nordigen.secret-key")}"}
             |""".stripMargin),
      )
    )

    responseFuture
      .flatMap {
        case HttpResponse(StatusCodes.OK, _, entity, _) =>
          Unmarshal(entity).to[NordigenAccessTokenResponse].flatMap( tokenResponse => {
            Future.successful(tokenResponse)
          })
        case HttpResponse(statusCode, _, entity, _) => {
          sys.error(s"Errore ${statusCode.intValue().toString}")
        }
      }
  }

  def getInstitutionsList(country: String, accessToken: String): Future[List[Institute]] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(
      HttpRequest(
        method = HttpMethods.GET,
        headers = Seq(
          Authorization.oauth2(accessToken)
        ),
        uri = s"${buildApiURL}institutions/?country=$country",
      )
    )

    responseFuture
      .flatMap {
        case HttpResponse(StatusCodes.OK, _, entity, _) =>
          Unmarshal(entity).to[List[Institute]].flatMap( institutionsList => {
            Future.successful(institutionsList)
          })
        case HttpResponse(statusCode, _, _, _) => {
          sys.error(s"Errore ${statusCode.intValue().toString}")
        }
      }
  }
}
