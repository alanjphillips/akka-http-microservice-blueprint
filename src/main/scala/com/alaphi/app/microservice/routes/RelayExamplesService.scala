package com.alaphi.app.microservice.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.alaphi.app.microservice.modules._
import com.alaphi.app.microservice.rest.{AppError, ReversedStringHolder}
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._

class RelayExamplesService(val stringReverserModule: StringReverserModule) {

  val relayExamplesRoutes = {
    path("relayexamples" / "reverseonce") {
      // example REST endpoint for a GET which calls another REST Service
      (get & parameters("param1")) { param1 =>

        onSuccess(stringReverserModule.getReversedValueFromRemoteService(param1)) {
          case r: ReversedStringHolder => complete(r)
          case e: AppError             => complete(BadRequest -> e)
          case _                       => complete(BadRequest -> AppError("APP_ERROR_002", "BadRequest for some reason"))
        }
      }
    } ~
      path("remoteserver" / "reverser") { // This is here just for demonstration purposes, it pretends to be a remote REST endpoint, called by getReversedValueFromRemoteService
        post {
          entity(as[ReversedStringHolder]) { reversedStringHolder =>
            reversedStringHolder match {
              case ReversedStringHolder("xyz") => complete(BadRequest -> AppError("APP_ERROR_001", "xyz makes this a BadRequest"))
              case _                           => complete(ReversedStringHolder(reversedStringHolder.value.reverse))
            }
          }
        }
      }
  }

}