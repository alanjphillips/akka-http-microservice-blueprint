package com.alaphi.app.microservice.cassandra

import com.websudos.phantom.dsl._

object Defaults {

  val hosts = Seq("0.0.0.0")

  val connector = ContactPoints(hosts).keySpace("app_keyspace")
}

class AppDatabase(val keyspace: KeySpaceDef) extends Database(keyspace) {
  object users extends ConcreteUsers with keyspace.Connector
}

object AppDatabase extends AppDatabase(Defaults.connector)
