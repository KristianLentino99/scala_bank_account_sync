package com.kristianlentino.infrastructure.http.responses

import com.kristianlentino.infrastructure.http.responses.Status.Status

object Status extends Enumeration {
  type Status = Value
  val CR,LN,EX,RJ,UA,GA,SA,GC = Value
}
//TODO: refactor the types to be more strict
case class CreateBankLinkResponse(
   id: String,
   institution_id: String,
   redirect: String,
   redirect_immediate: Boolean,
   status: Status,
   agreement: Option[String] = None,
   accounts: List[String] = List.empty[String],
   reference: Option[String] = None,
   link: String
)
