package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.http.ContentTypes.JSON

/**
 * Specs2 tests
 */
class MessageControllerSpec extends Specification {
  
  "MessageController" should {
    
    "getMessage should return JSON" in new WithApplication {
      val result = controllers.MessageController.getMessage(FakeRequest())

      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
      charset(result) must beSome("utf-8")
      contentAsString(result) must contain("Hello from Scala")
    }

  }
}