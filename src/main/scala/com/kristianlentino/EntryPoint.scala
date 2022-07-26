package com.kristianlentino

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.kristianlentino.infrastructure.registry.NordigenRegistry
import com.kristianlentino.infrastructure.routes.NordigenRoutes
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.{Failure, Success}

//#main-class
object EntryPoint {

  implicit val config: Config = ConfigFactory.load("application.conf")

  //#start-http-server
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    // Akka HTTP still needs a classic ActorSystem to start
    import system.executionContext

    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
  //#start-http-server
  def main(args: Array[String]): Unit = {
    //#server-bootstrapping
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val nordigenActor = context.spawn(NordigenRegistry(), "NordigenRegistryActor")
      context.watch(nordigenActor)

      val routes = new NordigenRoutes(nordigenActor)(context.system,config)
      startHttpServer(routes.nordigenRoutes)(context.system)

      Behaviors.empty
    }

    val system = ActorSystem[Nothing](rootBehavior, "AkkaHttpServer")
    //#server-bootstrapping
  }
}
//#main-class
