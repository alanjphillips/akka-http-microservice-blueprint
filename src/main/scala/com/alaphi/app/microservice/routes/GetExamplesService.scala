package com.alaphi.app.microservice.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.Segment
import com.alaphi.app.microservice.cassandra.AppDatabase
import com.alaphi.app.microservice.rest.{AppError, User}
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._

class GetExamplesService(val database: AppDatabase) {

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
      path("getexamples" / "something3" / Segment) { screenName => // example REST endpoint for a GET with pathParam
        get {
          complete {
            screenName
          }
        }
      } ~
      path("getexamples" / "users" / IntNumber) { userId =>
        get {
          onSuccess(database.users.getById(userId)) {
            case Some(user) => complete(User(user.id, user.fname, user.lname))
            case None       => complete(BadRequest -> AppError("APP_ERROR_002", "BadRequest for some reason"))
          }
        }
      }
  }
}
