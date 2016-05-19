package com.alaphi.app.microservice.actors

import akka.actor.{ Actor, ActorSystem, Props }
import akka.pattern.{ Backoff, BackoffSupervisor }
import akka.stream.{ ActorMaterializer, Materializer }

import scala.concurrent.duration._

class MainActor private (build: Materializer => Props) extends Actor {

  override val receive: Receive = { case _ => }

  implicit val a: ActorSystem = context.system
  implicit val materialiser = ActorMaterializer()

  val workerProps: Props = build(materialiser)
  val supervisorProps: Props = BackoffSupervisor.props(
    Backoff.onFailure(
      workerProps,
      "RoutesActor",
      3.seconds,
      30.seconds,
      0.2))
  context.actorOf(supervisorProps)

}

object MainActor {

  def props(worker: Materializer => Props): Props =
    Props(new MainActor(worker))
}
