package com.alaphi.app.microservice.modules

import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.alaphi.app.microservice.rest.{AppError, Payload, RestClient, ReversedStringHolder}
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

class StringReverserModule(val restClient: RestClient)(implicit ec: ExecutionContext, mat: Materializer) {

  def getReversedValueFromRemoteService(paramToReverse: String): Future[Payload] = {
    val request = RequestBuilding.Post("/remoteserver/reverser", ReversedStringHolder(paramToReverse))

    restClient.performRemoteServiceRequest(request).flatMap { response =>
      response.status match {
        case OK         => Unmarshal(response.entity).to[ReversedStringHolder]
        case BadRequest => Unmarshal(response.entity).to[AppError]
        case _ => Unmarshal(response.entity).to[String].flatMap { entity =>
          Future(AppError("APP_ERROR", entity))
        }
      }
    }
  }

}
