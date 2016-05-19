package com.alaphi.app.microservice.modules

import com.typesafe.config.ConfigFactory
import com.alaphi.app.microservice.rest.{ RestClient, ReversedStringHolder, AppError }
import com.alaphi.app.microservice.testutils.Specs2RouteTest
import org.specs2.Specification
import org.specs2.concurrent.ExecutionEnv

class StringReverserModuleSpec extends Specification with Specs2RouteTest { self =>

  override def is =
    s2"""

     StringReverserModule
        must get reversed value from remotes service   $getReversedValue
        must get reversed value gives HTTP Response with status code == BadRequest  $getReversedValueBadRequest
   """

  implicit val ee: ExecutionEnv = ExecutionEnv.fromExecutionContext(self.executor)

  def getReversedValue = {
    stringReverserModule.getReversedValueFromRemoteService("abc") must be_==(ReversedStringHolder("cba")).await
  }

  def getReversedValueBadRequest = {
    stringReverserModuleBad.getReversedValueFromRemoteService("abc") must be_==(AppError("APP_ERROR_1", "You sent a bad request")).await
  }

  // Mocks etc
  val restClient: RestClient = new RestClient(ConfigFactory.load())(self.executor, self.materializer, self.system) with RestClientMock
  val stringReverserModule: StringReverserModule = new StringReverserModule(restClient)(self.executor, self.materializer)

  val restClientBad: RestClient = new RestClient(ConfigFactory.load())(self.executor, self.materializer, self.system) with RestClientMockBadRequest
  val stringReverserModuleBad: StringReverserModule = new StringReverserModule(restClientBad)(self.executor, self.materializer)

}
