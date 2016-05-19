package com.alaphi.app.microservice.modules

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, _ }
import akka.util.ByteString
import com.alaphi.app.microservice.rest.RestClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RestClientMock {
  self: RestClient =>

  override def performRemoteServiceRequest(request: HttpRequest): Future[HttpResponse] = {
    val responseJson = ByteString(
      s"""
         |{
         |  "value": "cba"
         |}
        """.stripMargin)

    Future {
      HttpResponse(status = StatusCodes.OK,
        entity = HttpEntity(ContentType(MediaTypes.`application/json`), responseJson))
    }
  }
}

trait RestClientMockBadRequest extends RestClientMock {
  self: RestClient =>

  override def performRemoteServiceRequest(request: HttpRequest): Future[HttpResponse] = {
    val responseJson = ByteString(
      s"""
         |{
         |  "code": "APP_ERROR_1",
         |  "message": "You sent a bad request"
         |}
        """.stripMargin)

    Future {
      HttpResponse(status = StatusCodes.BadRequest,
        entity = HttpEntity(ContentType(MediaTypes.`application/json`), responseJson))
    }
  }
}