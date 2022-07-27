package com.kristianlentino.infrastructure.registry

//#user-registry-actor
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.kristianlentino.EntryPoint.config
import com.kristianlentino.common.traits.Command
import com.kristianlentino.domain.models.{AccessTokenResponse, Institute}
import com.kristianlentino.infrastructure.http.responses.CreateBankLinkResponse
import com.kristianlentino.infrastructure.http.{BaseOpenBankingRestClient, NordigenClient}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps


object NordigenRegistry {

  final case class GetAccessToken(replyTo: ActorRef[AccessTokenResponse]) extends Command
  final case class GetBankList(replyTo: ActorRef[List[Institute]], country: String, accessToken: String) extends Command
  final case class CreateBankList(replyTo: ActorRef[CreateBankLinkResponse], institutionId: String, redirectUri: String, accessToken: String) extends Command

  def apply(): Behavior[Command] = registry()
  val restApiClient: BaseOpenBankingRestClient = new NordigenClient()
  private def registry(): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetAccessToken(replyTo) =>
        val getToken = restApiClient.getAccessToken.map{ tokenResponse => {
          replyTo ! AccessTokenResponse(
            access = tokenResponse.access,
            access_expires = tokenResponse.access_expires,
            refresh = tokenResponse.refresh,
            refresh_expires = tokenResponse.refresh_expires
          )
        }}

        Await.result(getToken, 5000 millis)
        Behaviors.same
      case GetBankList(replyTo, country, accessToken) =>
        val bankList = restApiClient.getInstitutionsList(country,accessToken).map{ bankListResponse => {
          replyTo ! bankListResponse
        }}

        Await.result(bankList, 5000 millis)
        Behaviors.same
      case CreateBankList(replyTo, institutionId, redirectUri, accessToken) =>
        val bankList = restApiClient.createBankLink(institutionId, redirectUri ,accessToken)
          .map{ bankLinkResponse => {
            replyTo ! bankLinkResponse
          }}

        Await.result(bankList, 5000 millis)
        Behaviors.same
    }
}
//#user-registry-actor
