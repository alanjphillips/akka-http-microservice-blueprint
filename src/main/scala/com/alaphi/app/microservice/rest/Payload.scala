package com.alaphi.app.microservice.rest

sealed trait Payload

final case class AppError(code: String, message: String) extends Payload
final case class AppErrors(errors: List[AppError]) extends Payload

final case class ReversedStringHolder(value: String) extends Payload

final case class Item(name: String, id: Long) extends Payload
final case class Group(items: List[Item]) extends Payload

final case class User(id: Int, fname: String, lname: String) extends Payload

