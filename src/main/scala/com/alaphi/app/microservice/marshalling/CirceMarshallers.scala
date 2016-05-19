package com.alaphi.app.microservice.marshalling

import akka.http.scaladsl.marshalling.{ Marshaller, PredefinedToResponseMarshallers }
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.{ PredefinedFromEntityUnmarshallers, Unmarshaller }
import akka.http.scaladsl.util.{ FastFuture => FF }
import cats.Show
import cats.data.Xor
import io.circe.parser._
import io.circe.{ Decoder, Encoder, Json }

import scala.concurrent.Future
import scala.util.control.NoStackTrace

trait CirceMarshallers
  extends PredefinedFromEntityUnmarshallers
  with PredefinedToResponseMarshallers
  with LowPriorityCirceMarshallers

trait LowPriorityCirceMarshallers {

  implicit val argonautJsonMarshaller: Marshaller[Json, RequestEntity] =
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(_.noSpaces)

  implicit def argonautTMarshaller[T](implicit ET: Encoder[T]): Marshaller[T, RequestEntity] =
    argonautJsonMarshaller.compose[T](ET(_))

  implicit val argonautJsonUnmarshaller: Unmarshaller[HttpEntity, Json] =
    PredefinedFromEntityUnmarshallers.
      stringUnmarshaller.
      flatMap(ec => fm => str => xorToFuture(parse(str)))

  implicit def argonautTUnmarshaller[T](implicit DT: Decoder[T]): Unmarshaller[HttpEntity, T] =
    PredefinedFromEntityUnmarshallers.
      stringUnmarshaller.
      flatMap(ec => fm => str => xorToFuture(decode[T](str)))

  private def xorToFuture[A, B](value: Xor[A, B])(implicit SA: Show[A]): Future[B] =
    value.fold(
      error => FF.failed(new Throwable(SA.show(error)) with NoStackTrace),
      FF.successful)
}

object CirceMarshallers extends CirceMarshallers
