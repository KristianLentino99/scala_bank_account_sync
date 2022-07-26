package com.kristianlentino.domain.routes

import akka.actor.typed.ActorSystem
import akka.util.Timeout
import com.kristianlentino.domain.models.AccessTokenResponse
import com.typesafe.config.Config

import scala.concurrent.Future

abstract class BaseOpenBankingRoutes(implicit val system: ActorSystem[_], config: Config) {
  // If ask takes more time than this to complete the request is failed
  implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))
  def getAccessToken: Future[AccessTokenResponse]
}
