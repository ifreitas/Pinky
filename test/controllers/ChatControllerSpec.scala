package controllers

import base.PinkySpecification
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.mvc.Results
import play.api.test.Helpers._
import play.api.test._

class ChatControllerSpec extends PinkySpecification with GuiceOneAppPerTest with Injecting with Results {

  trait ChatControllerContext {
    val bot        = "sample"
    val session    = "some_session"
    val path       = s"/chat/$bot/$session"
    val controller = inject[ChatController]
    implicit val materializer = app.materializer
  }

  describe("POST") {
    describe("Valid Bot") {
      describe("Empty message") {
        it("should return Bad Request status") {
          new ChatControllerContext {
            val request = FakeRequest(POST, path).withFormUrlEncodedBody(("txt", ""))
            val result  = controller.chat(bot, session).apply(request)

            status(result) shouldBe BAD_REQUEST
            contentType(result) shouldBe Some("application/json")
            println(contentAsString(result))
            contentAsString(result) should include("text is required")
          }
        }
      }
      it("should return a message (json)") {
        new ChatControllerContext {
          val request = FakeRequest(POST, path).withJsonBody(Json.parse("""{ "text": "test" }"""))
          val result  = controller.chat(bot, session).apply(request)

          status(result) shouldBe OK
          contentType(result) shouldBe Some("application/json")
          contentAsString(result) should include("Congratulation. It works!")
        }
      }
      it("should return a message (form)") {
        new ChatControllerContext {
          val request = FakeRequest(POST, path).withFormUrlEncodedBody(("text", "test"))
          val result  = controller.chat(bot, session).apply(request)

          status(result) shouldBe OK
          contentType(result) shouldBe Some("application/json")
          contentAsString(result) should include("Congratulation. It works!")
        }
      }
    }

    describe("Invalid Bot") {
      it("should return Bad Request status") {
        new ChatControllerContext {
          override val bot : String = "none"
          override val path: String = s"$bot.$session"
          val request = FakeRequest(POST, path).withFormUrlEncodedBody(("text", "test"))
          val result  = controller.chat(bot, session).apply(request)

          status(result) shouldBe BAD_REQUEST
          contentType(result) shouldBe Some("application/json")
          contentAsString(result) should include("Bot 'none' does not exists")
        }
      }
    }

  }
}
