package com.alaphi.app.microservice.testutils

import akka.http.scaladsl.testkit.{ RouteTest, TestFrameworkInterface }
import org.specs2.execute.{ Failure, FailureException }
import org.specs2.specification.core.{ Fragments, SpecificationStructure }
import org.specs2.specification.create.DefaultFragmentFactory

trait Specs2TestFrameworkInterface extends TestFrameworkInterface {

  def cleanUp(): Unit

  def failTest(msg: String): Nothing
}

trait Specs2Interface extends SpecificationStructure with Specs2TestFrameworkInterface {

  override def failTest(msg: String): Nothing = {
    val trace = new Exception().getStackTrace.toList
    val fixedTrace = trace.drop(trace.indexWhere(_.getClassName.startsWith("org.specs2")) - 1)
    throw new FailureException(Failure(msg, stackTrace = fixedTrace))
  }

  override def map(fs: ⇒ Fragments) = super.map(fs).append(DefaultFragmentFactory.step(cleanUp()))
}

trait Specs2RouteTest extends RouteTest with Specs2Interface
