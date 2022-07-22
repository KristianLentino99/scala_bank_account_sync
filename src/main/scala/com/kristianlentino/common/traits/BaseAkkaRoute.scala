package com.kristianlentino.common.traits

import akka.actor.typed.ActorSystem
import akka.util.Timeout
import com.typesafe.config.Config

abstract class BaseAkkaRoute(implicit val system: ActorSystem[_], config: Config) {
  //#user-routes-class
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))
}
