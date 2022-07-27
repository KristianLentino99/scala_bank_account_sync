package com.kristianlentino.infrastructure.routes

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives.headerValueByName
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.kristianlentino.domain.models.AccessTokenResponse
import com.typesafe.config.Config

import scala.concurrent.Future

abstract class BaseOpenBankingRoutes(implicit val system: ActorSystem[_], config: Config) {
  // If ask takes more time than this to complete the request is failed
  implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))
  def getAccessToken: Future[AccessTokenResponse]
  protected def parseToken(toApply: String => Route) = headerValueByName("Authorization") { authToken =>
    /**
     * Obviously normally this is wrong, but well... it's a learning project so everything is permitted
     */
    val parsedToken = authToken.replace("Bearer ", "")
    toApply(parsedToken)
  }
}
