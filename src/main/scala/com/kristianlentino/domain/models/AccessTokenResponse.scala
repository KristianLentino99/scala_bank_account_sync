package com.kristianlentino.domain.models

case class AccessTokenResponse(
  access: String,
  access_expires: Long,
  refresh: String,
  refresh_expires: Long
)
