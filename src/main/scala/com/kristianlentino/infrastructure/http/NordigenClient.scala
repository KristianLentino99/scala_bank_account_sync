package com.kristianlentino.infrastructure.http

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.javadsl.model.headers.Authorization
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.kristianlentino.common.traits.ObjectsUnmarshaller
import com.kristianlentino.domain.http.BaseOpenBankingRestClient
import com.kristianlentino.domain.models.{AccessTokenResponse, Institute}
import com.typesafe.config.Config
import spray.json.DefaultJsonProtocol.listFormat

import scala.concurrent.{ExecutionContextExecutor, Future}

class NordigenClient(implicit config: Config) extends BaseOpenBankingRestClient  with ObjectsUnmarshaller {


  private val NORDIGEN_BASE_URL = "https://ob.nordigen.com/"
  override val API_VERSION = Some("v2")
  override val API_PREFIX = Some("api")
  override implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "NordigenRequest")
  override implicit val executionContext: ExecutionContextExecutor = system.executionContext
  override def buildApiURL: String = s"$NORDIGEN_BASE_URL${API_PREFIX.map(value => value + "/").getOrElse("")}${API_VERSION.map(value => value + "/").getOrElse("")}"
  override def getAccessToken: Future[AccessTokenResponse] = {
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
          Unmarshal(entity).to[AccessTokenResponse].flatMap( tokenResponse => {
            Future.successful(tokenResponse)
          })
        case HttpResponse(statusCode, _, entity, _) => {
          sys.error(s"Errore ${statusCode.intValue().toString}")
        }
      }
  }

  override def getInstitutionsList(country: String, accessToken: String): Future[List[Institute]] = {
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
