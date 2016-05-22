package com.alaphi.app.microservice.cassandra

import com.typesafe.config.Config
import com.websudos.phantom.dsl._

class AppDatabaseConfig(val config: Config) {
  val hosts = Seq(config.getString("cassandra.host"))
  val appKeyspace: KeySpaceDef = ContactPoints(hosts).keySpace("app_keyspace")
}

class AppDatabase(val appDatabaseConfig: AppDatabaseConfig) extends Database(appDatabaseConfig.appKeyspace) {
  object users extends ConcreteUsers with appDatabaseConfig.appKeyspace.Connector
}