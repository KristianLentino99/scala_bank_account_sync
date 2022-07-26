package com.kristianlentino.infrastructure.routes

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.kristianlentino.common.traits.ObjectsUnmarshaller
import com.kristianlentino.domain.models.{AccessTokenResponse, Command, Institute, InstituteList}
import com.kristianlentino.domain.routes.BaseOpenBankingRoutes
import com.kristianlentino.infrastructure.registry.NordigenRegistry._
import com.typesafe.config.Config
import spray.json._

import scala.concurrent.Future
class NordigenRoutes(nordigenRegistry: ActorRef[Command])(implicit val actorSystem: ActorSystem[_], config: Config) extends BaseOpenBankingRoutes with ObjectsUnmarshaller {
  //#user-routes-class
  //#import-json-formats

  def getAccessToken: Future[AccessTokenResponse] =
    nordigenRegistry.ask(GetAccessToken)

  def getBankList(country: String,parsedToken: String): Future[List[Institute]] =
    nordigenRegistry.ask(replyTo => GetBankList(replyTo, country,parsedToken))

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
          headerValueByName("Authorization") { authToken =>
            parameters("country".as[String]) { country =>
              /**
               * Obviously normally this is wrong, but well... it's a learning project so everything is permitted
               */
              val parsedToken = authToken.replace("Bearer ", "")
              onSuccess(getBankList(country,parsedToken)) { bankList =>
                complete(StatusCodes.OK -> InstituteList(items = bankList))
              }
            }
          }

        }
      }
    )
  }

  val nordigenRoutes: Route = authTokenRoutes ~ bankListRoutes
}
