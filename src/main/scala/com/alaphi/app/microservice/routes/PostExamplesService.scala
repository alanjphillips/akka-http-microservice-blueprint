package com.alaphi.app.microservice.routes

import akka.http.scaladsl.server.Directives._
import com.alaphi.app.microservice.cassandra.{AppDatabase, User}
import com.alaphi.app.microservice.rest
import com.alaphi.app.microservice.rest.{Group, Item}
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._

class PostExamplesService(val database: AppDatabase) {

  def dbToPayloadUser(user: User) = rest.User(user.id, user.fname, user.lname)
  def payloadToDbUser(user: rest.User) = User(user.id, user.fname, user.lname)

  val postExamplesRoutes = {
    path("postexamples" / "something2") {
      // example REST endpoint for a POST that responds with Item instance which is marshalled to JSON using circe
      post {
        complete {
          Item("testItem", 987)
        }
      }

    } ~
      path("postexamples" / "something3") {
        // example REST endpoint for a POST with application/json payload that is unmarshalled to Group instance using circe
        post {
          entity(as[Group]) { group => // will unmarshal JSON to Group
            val itemsCount = group.items.size
            val itemNames = group.items.map(_.name).mkString(", ")
            complete(s"Group size: $itemsCount items: $itemNames")
          }
        }
      } ~
      path("postexamples" / "users") {
        // example REST endpoint for a POST with application/json payload that is unmarshalled to Group instance using circe
        post {
          entity(as[rest.User]) { u => // will unmarshal JSON to Group
            onSuccess(database.users.store(payloadToDbUser(u))) { resultSet =>
              complete(u) // will return saved value after storing in cassandra
            }
          }
        }
      }
  }

}
