package com.alaphi.app.microservice.actors

import akka.actor.{ Actor, ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import com.alaphi.app.microservice.cassandra.{ AppDatabase, AppDatabaseConfig }
import com.typesafe.config.{ Config, ConfigFactory }
import com.alaphi.app.microservice.modules.StringReverserModule
import com.alaphi.app.microservice.rest.RestClient
import com.alaphi.app.microservice.routes._

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext }

class RoutesActor private (m: Materializer)
  extends Actor {

  implicit val actorSystem: ActorSystem = context.system
  implicit val executor: ExecutionContext = actorSystem.dispatcher
  implicit val materialiser: Materializer = m
  implicit val config: Config = ConfigFactory.load()

  val restClient: RestClient = new RestClient(config)(executor, materialiser, actorSystem)
  val stringReverserModule: StringReverserModule = new StringReverserModule(restClient)(executor, materialiser)
  val appDatabaseConfig = new AppDatabaseConfig(config)
  val database = new AppDatabase(appDatabaseConfig)

  val pes: PostExamplesService = new PostExamplesService(database)
  val ges: GetExamplesService = new GetExamplesService(database)
  val res: RelayExamplesService = new RelayExamplesService(stringReverserModule)

  // These routes will be renamed/reformulated to contain real routes
  val routes = {
    pes.postExamplesRoutes ~
      ges.getExamplesRoutes ~
      res.relayExamplesRoutes
  }

  val bindingFuture = Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))

  override def receive: Receive = { case _ => }

  override def postStop(): Unit = {
    Await.result(bindingFuture.flatMap(_.unbind()), Duration.Inf)
  }
}

object RoutesActor {

  def props(materialiser: Materializer): Props =
    Props(new RoutesActor(materialiser))
}
