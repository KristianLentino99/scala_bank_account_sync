package com.kristianlentino.infrastructure.routes

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.kristianlentino.common.traits.{Command, ObjectsUnmarshaller}
import com.kristianlentino.domain.models.{AccessTokenResponse, Institute, InstituteList}
import com.kristianlentino.infrastructure.http.requests.LinkBuilderRequest
import com.kristianlentino.infrastructure.http.responses.CreateBankLinkResponse
import com.kristianlentino.infrastructure.registry.NordigenRegistry._
import com.typesafe.config.Config
import spray.json._

import scala.concurrent.Future
class NordigenRoutes(nordigenRegistry: ActorRef[Command])(implicit val actorSystem: ActorSystem[_], config: Config) extends BaseOpenBankingRoutes with ObjectsUnmarshaller {

  def getAccessToken: Future[AccessTokenResponse] =
    nordigenRegistry.ask(GetAccessToken)

  def getBankList(country: String,parsedToken: String): Future[List[Institute]] =
    nordigenRegistry.ask(replyTo => GetBankList(replyTo, country,parsedToken))

  def createLink(institutionId: String, redirectUri: String, accessToken: String): Future[CreateBankLinkResponse] =
    nordigenRegistry.ask(replytTo => CreateBankList(replytTo, institutionId, redirectUri, accessToken))

  val authTokenRoutes: Route =
    pathPrefix("nordigen" / "getToken"){
      concat(
        pathEnd{
          get{
            onSuccess(getAccessToken){ accessToken =>
              complete(StatusCodes.OK -> accessToken.toJson)
            }
          }
        }
      )
    }

  val bankListRoutes: Route = pathPrefix("nordigen" / "bankList"){
    concat(
      pathEnd{
        get {
          parseToken { parsedToken =>
            parameters("country".as[String]) { country =>
              onSuccess(getBankList(country,parsedToken)) { bankList =>
                complete(StatusCodes.OK -> InstituteList(items = bankList))
              }
            }
          }
        }
      }
    )
  }

  val linkBuilderRoute: Route = pathPrefix("nordigen" / "linkBuilder"){
    concat(
      pathEnd{
        post {
          parseToken { parsedToken =>
            entity(as[LinkBuilderRequest]) { request =>
              onSuccess(createLink(request.institution_id, request.redirect_uri, parsedToken)) { bankLinkResponse =>
                complete(StatusCodes.OK -> bankLinkResponse)
              }
            }
          }
        }
      }
    )
  }

  val nordigenRoutes: Route = authTokenRoutes ~ bankListRoutes ~ linkBuilderRoute
}
