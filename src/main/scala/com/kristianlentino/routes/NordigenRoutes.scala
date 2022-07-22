package com.kristianlentino.routes

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.kristianlentino.common.models.{Institute, InstituteList, NordigenAccessTokenResponse}
import com.kristianlentino.common.traits.{BaseAkkaRoute, NordigenUnmarshaller}
import com.kristianlentino.registry.NordigenRegistry
import com.kristianlentino.registry.NordigenRegistry.{GetAccessToken, GetBankList}
import com.typesafe.config.Config
import spray.json._

import scala.concurrent.Future

class NordigenRoutes(nordigenRegistry: ActorRef[NordigenRegistry.Command])(implicit val actorSystem: ActorSystem[_], config: Config) extends BaseAkkaRoute with NordigenUnmarshaller {
  //#user-routes-class
  //#import-json-formats

  def getAccessToken: Future[NordigenAccessTokenResponse] =
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
               * Obviously normally this is wrong, but wel... it's a learning project so anything is permitted
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
