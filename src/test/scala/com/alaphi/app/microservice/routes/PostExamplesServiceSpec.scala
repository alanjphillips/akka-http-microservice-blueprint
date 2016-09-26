package com.alaphi.app.microservice.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ HttpEntity, MediaTypes }
import akka.util.ByteString
import com.alaphi.app.microservice.cassandra.AppDatabase
import com.alaphi.app.microservice.rest.Item
import com.alaphi.app.microservice.testutils.Specs2RouteTest
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.Decoder._
import io.circe.generic.auto._
import org.specs2.Specification
import org.specs2.mock.Mockito

class PostExamplesServiceSpec extends Specification with Specs2RouteTest with Mockito {

  override def is =
    s2"""

     PostExamplesService

        must return a response for POST requests to /postexamples/something2            $postSomething2
        must return a response for POST requests to /postexamples/something3            $postSomething3
   """

  def postSomething2 = {
    Post("/postexamples/something2") ~> pes.postExamplesRoutes ~> check {
      (status mustEqual OK) and (responseAs[Item] mustEqual Item("testItem", 987))
    }
  }

  def postSomething3 = {
    val postRequestBodyJson = ByteString(
      s"""
         |{
         |    "items" : [
         |        {
         |            "name": "item1",
         |            "id": 123
         |        },
         |        {
         |            "name": "item2",
         |            "id": 1234
         |        }
         |    ]
         |}
        """.stripMargin)

    Post("/postexamples/something3", HttpEntity(MediaTypes.`application/json`, postRequestBodyJson)) ~> pes.postExamplesRoutes ~> check {
      (status mustEqual OK) and (responseAs[String] mustEqual """Group size: 2 items: item1, item2""")
    }
  }

  val database = mock[AppDatabase]
  val pes: PostExamplesService = new PostExamplesService(database)

}
