package models

import base.PinkySpecification
import org.alicebot.ab.Bot
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import play.api.Configuration

import scala.concurrent.ExecutionContext.Implicits.global

class BotServiceSpec extends PinkySpecification with MockitoSugar with ScalaFutures {

  trait BotServiceContext {
    val bot       : Bot            = mock[Bot]
    val botId     : String         = "bot_1"
    val config    : Configuration  = mock[Configuration]
    when(config.getOptional[String]("bots_home")).thenReturn(Some(System.getProperty("user.dir")))
    val botService: BotServiceImpl = spy(new BotServiceImpl(config))
  }

  describe("#newBot") {
    describe("valid botId") {
      it("should return the bot") {
        new BotServiceContext {
          doNothing when botService validate botId
          whenReady(botService.newBot(botId)) { bot =>
            bot shouldBe a[Bot]
          }
        }
      }
    }

    describe("Invalid botId") {
      it("should throw BotNotFoundException") {
        new BotServiceContext {
          whenReady(botService.newBot(botId).failed) { exception =>
            exception shouldBe a[BotNotFoundException]
          }
        }
      }
    }
  }
}
