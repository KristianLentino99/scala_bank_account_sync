package com.kristianlentino.domain.http

import akka.actor.typed.ActorSystem
import com.kristianlentino.domain.models.{AccessTokenResponse, Institute}

import scala.concurrent.{ExecutionContextExecutor, Future}

abstract class BaseOpenBankingRestClient {
  def buildApiURL: String
  val API_PREFIX: Option[String]
  val API_VERSION: Option[String]

  //implicits
  implicit val system: ActorSystem[Nothing]
  implicit val executionContext: ExecutionContextExecutor

  def getAccessToken: Future[AccessTokenResponse]
  def getInstitutionsList(country: String, accessToken: String): Future[List[Institute]]
}
