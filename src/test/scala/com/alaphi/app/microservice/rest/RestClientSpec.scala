package com.alaphi.app.microservice.rest

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, _ }
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import com.alaphi.app.microservice.testutils.Specs2RouteTest
import org.specs2.Specification
import org.specs2.concurrent.ExecutionEnv

class RestClientSpec extends Specification with Specs2RouteTest { self =>

  override def is =
    s2"""

     RestClient
        must perform Remote Service Request   $performRequest
   """

  implicit val ee: ExecutionEnv = ExecutionEnv.fromExecutionContext(self.executor)

  def performRequest = {
    restClient.performRemoteServiceRequest(HttpRequest()) must be_==(httpResponse).await
  }

  // Mocks etc
  val responseJson = ByteString(
    s"""
       |{
       |  "value": "cba"
       |}
        """.stripMargin)

  val httpResponse = HttpResponse(status = StatusCodes.OK,
    entity = HttpEntity(ContentType(MediaTypes.`application/json`), responseJson))

  val restClient: RestClient = new RestClient(ConfigFactory.load())(self.executor, self.materializer, self.system) with RestClientMixins

  trait RestClientMixins { self: RestClient =>
    override lazy val remoteServiceConnectionFlow: Flow[HttpRequest, HttpResponse, Any] = Flow[HttpRequest].map(_ => httpResponse)
  }

}