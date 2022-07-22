package com.kristianlentino.common.models

case class NordigenAccessTokenResponse(
  access: String,
  access_expires: Long,
  refresh: String,
  refresh_expires: Long
)
