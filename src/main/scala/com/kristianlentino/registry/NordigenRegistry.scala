package com.kristianlentino.registry

//#user-registry-actor
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.kristianlentino.NordigenBankSync.config
import com.kristianlentino.common.models.{Institute, NordigenAccessTokenResponse}
import com.kristianlentino.common.utilities.http.NordigenClient

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps



object NordigenRegistry {
  // actor protocol
  sealed trait Command
  final case class GetAccessToken(replyTo: ActorRef[NordigenAccessTokenResponse]) extends Command
  final case class GetBankList(replyTo: ActorRef[List[Institute]], country: String, accessToken: String) extends Command
  final case class ActionPerformed(description: String)
  def apply(): Behavior[Command] = registry()
  val nordigenClient = new NordigenClient()
  private def registry(): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetAccessToken(replyTo) =>
        val getToken = nordigenClient.getAccessToken.map{ tokenResponse => {
          replyTo ! NordigenAccessTokenResponse(
            access = tokenResponse.access,
            access_expires = tokenResponse.access_expires,
            refresh = tokenResponse.refresh,
            refresh_expires = tokenResponse.refresh_expires
          )
        }}

        Await.result(getToken, 5000 millis)
        Behaviors.same
      case GetBankList(replyTo, country, accessToken) =>
        val bankList = nordigenClient.getInstitutionsList(country,accessToken).map{ bankListResponse => {
          replyTo ! bankListResponse
        }}

        Await.result(bankList, 5000 millis)
        Behaviors.same
    }
}
//#user-registry-actor
