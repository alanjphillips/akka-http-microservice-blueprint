package com.alaphi.app.microservice.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import com.alaphi.app.microservice.marshalling.CirceMarshallers._
import com.alaphi.app.microservice.modules.{ RestClientMock, StringReverserModule }
import com.alaphi.app.microservice.rest.{ Payload, RestClient, ReversedStringHolder, AppError }
import com.alaphi.app.microservice.testutils.Specs2RouteTest
import io.circe.Decoder._
import io.circe.generic.auto._
import org.specs2.Specification

import scala.concurrent.Future

class RelayExamplesServiceSpec extends Specification with Specs2RouteTest { self =>

  override def is =
    s2"""

     RelayExamplesService

        must return a response for GET requests to relayexamples/reverseonce?param1=abc                 $getReversedOnce
        must return BadRequest HTTP status response for GET to relayexamples/reverseonce?param1=abc     $getReversedOnceBadRequest
        must return a response for POST requests to remoteserver/reverser with application/json payload $getStringReversed
        must return BadRequest HTTP status response for POST requests to remoteserver/reverser          $getStringReversedBadRequest
      """

  def getReversedOnce = {
    Get("/relayexamples/reverseonce?param1=abc") ~> res.relayExamplesRoutes ~> check {
      (status mustEqual OK) and (responseAs[ReversedStringHolder] mustEqual ReversedStringHolder("cba"))
    }
  }

  def getReversedOnceBadRequest = {
    Get("/relayexamples/reverseonce?param1=xyz") ~> resBadRequest.relayExamplesRoutes ~> check {
      (status mustEqual BadRequest) and (responseAs[AppError] mustEqual AppError("APP_ERROR_001", "xyz makes this a BadRequest"))
    }
  }

  def getStringReversed = {
    val postRequestBodyJson = ByteString(
      s"""
         |{
         |  "value": "abc"
         |}
        """.stripMargin)

    Post("/remoteserver/reverser", postRequestBodyJson) ~> res.relayExamplesRoutes ~> check {
      (status mustEqual OK) and (responseAs[ReversedStringHolder] mustEqual ReversedStringHolder("cba"))
    }
  }

  def getStringReversedBadRequest = {
    val postRequestBodyJson = ByteString(
      s"""
         |{
         |  "value": "xyz"
         |}
        """.stripMargin)

    Post("/remoteserver/reverser", postRequestBodyJson) ~> res.relayExamplesRoutes ~> check {
      (status mustEqual BadRequest) and (responseAs[AppError] mustEqual AppError("APP_ERROR_001", "xyz makes this a BadRequest"))
    }
  }

  // Mocks etc
  val restClient: RestClient = new RestClient(ConfigFactory.load())(self.executor, self.materializer, self.system) with RestClientMock
  val stringReverserModule: StringReverserModule = new StringReverserModule(restClient)(self.executor, self.materializer) with StringReverserModuleMock
  val res: RelayExamplesService = new RelayExamplesService(stringReverserModule)

  val stringReverserModuleBadRequest: StringReverserModule = new StringReverserModule(restClient)(self.executor, self.materializer) with StringReverserModuleMockBadRequest
  val resBadRequest: RelayExamplesService = new RelayExamplesService(stringReverserModuleBadRequest)

  trait StringReverserModuleMock {
    self: StringReverserModule =>

    override def getReversedValueFromRemoteService(paramToReverse: String): Future[Payload] = {
      Future {
        ReversedStringHolder(paramToReverse.reverse)
      }
    }
  }

  trait StringReverserModuleMockBadRequest {
    self: StringReverserModule =>

    override def getReversedValueFromRemoteService(paramToReverse: String): Future[Payload] = {
      Future {
        AppError("APP_ERROR_001", "xyz makes this a BadRequest")
      }
    }
  }

}
