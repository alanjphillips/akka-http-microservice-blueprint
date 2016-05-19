package com.alaphi.app.microservice

import akka.actor.ActorSystem
import com.alaphi.app.microservice.actors.{ MainActor, RoutesActor }

object Boot extends App {

  implicit val system = ActorSystem("MICROSERVICE-A")

  system.actorOf(MainActor.props(RoutesActor.props _))
}
