package com.alaphi.app.microservice.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }
import akka.stream.Materializer
import akka.stream.scaladsl.{ Flow, Sink, Source }
import com.typesafe.config.Config

import scala.concurrent.{ ExecutionContext, Future }

class RestClient(val config: Config)(implicit ec: ExecutionContext, mat: Materializer, as: ActorSystem) {

  lazy val remoteServiceConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("http.interface"), config.getInt("http.port"))

  def performRemoteServiceRequest(request: HttpRequest): Future[HttpResponse] =
    Source.single(request).via(remoteServiceConnectionFlow).runWith(Sink.head)

}