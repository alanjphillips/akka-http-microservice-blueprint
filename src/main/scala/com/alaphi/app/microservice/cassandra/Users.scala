package com.alaphi.app.microservice.cassandra

import com.websudos.phantom.dsl._

import scala.concurrent.Future

class Users extends CassandraTable[ConcreteUsers, User] {

  object id extends IntColumn(this) with PartitionKey[Int]
  object fname extends StringColumn(this)
  object lname extends StringColumn(this)

  def fromRow(row: Row): User = {
    User(
      id(row),
      fname(row),
      lname(row))
  }
}

// The root connector comes from import com.websudos.phantom.dsl._
abstract class ConcreteUsers extends Users with RootConnector {

  def store(user: User): Future[ResultSet] = {
    insert.value(_.id, user.id)
      .value(_.fname, user.fname)
      .value(_.lname, user.lname)
      .consistencyLevel_=(ConsistencyLevel.ALL)
      .future()
  }

  def getById(id: Int): Future[Option[User]] = {
    select.where(_.id eqs id).one()
  }
}

case class User(
  id: Int,
  fname: String,
  lname: String)
