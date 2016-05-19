package com.alaphi.app.microservice.routes

import akka.http.scaladsl.model.StatusCodes._
import com.alaphi.app.microservice.marshalling.CirceMarshallers._
import com.alaphi.app.microservice.testutils.Specs2RouteTest
import org.specs2.Specification

class GetExamplesServiceSpec extends Specification with Specs2RouteTest {

  override def is =
    s2"""

     GetExamplesService

        must return a response for GET requests to /getexamples/something1?param1=abc   $getSomething1
        must return a response for GET requests to URI containing path param            $getSomething3
   """

  def getSomething1 = {
    Get("/getexamples/something1?param1=abc") ~> ges.getExamplesRoutes ~> check {
      (status mustEqual OK) and (responseAs[String] mustEqual """"abc"""")
    }
  }

  def getSomething3 = {
    Get("/getexamples/something3/apathparam") ~> ges.getExamplesRoutes ~> check {
      (status mustEqual OK) and (responseAs[String] mustEqual """"apathparam"""")
    }
  }

  val ges: GetExamplesService = new GetExamplesService

}
