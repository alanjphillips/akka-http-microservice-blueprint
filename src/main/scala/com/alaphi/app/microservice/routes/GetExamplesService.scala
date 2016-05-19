package com.alaphi.app.microservice.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.alaphi.app.microservice.cassandra.AppDatabase
import com.alaphi.app.microservice.marshalling.CirceMarshallers._
import com.alaphi.app.microservice.rest.{ AppError, User }
import io.circe.generic.auto._

class GetExamplesService {

  val getExamplesRoutes = {
    path("getexamples" / "something1") {
      // example REST endpoint for a GET with requestParam named 'param1'
      (get & parameters("param1")) {
        param1 =>
          {
            complete {
              param1
            }
          }
      }
    } ~
      path("getexamples" / "something3" / Rest) { screenName => // example REST endpoint for a GET with pathParam
        get {
          complete {
            screenName
          }
        }
      } ~
      path("getexamples" / "users" / Rest) { userId =>
        get {
          onSuccess(AppDatabase.users.getById(userId.toInt)) {
            case Some(user) => complete(User(user.id, user.fname, user.lname))
            case None       => complete(BadRequest -> AppError("APP_ERROR_002", "BadRequest for some reason"))
          }
        }
      }
  }
}
