package com.kristianlentino.domain.models

case class Institute(
  id: String,
  name: String,
  bic: Option[String] = None,
  transaction_total_days: Option[String],
  countries: List[String] = List.empty,
  logo: String
)

case class InstituteList(
  items: List[Institute]
)