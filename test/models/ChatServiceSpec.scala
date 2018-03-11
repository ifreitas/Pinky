package models

import base.PinkySpecification
import org.alicebot.ab.Chat
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import play.api.cache.AsyncCacheApi

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.{DurationInt, FiniteDuration}

class ChatServiceSpec extends PinkySpecification with MockitoSugar with ScalaFutures {

  val botId            = "bot_1"
  val userMessage      = "hi"
  val conversationId   = "brain_conversation_1"
  val expectedResponse = "hello"
  val cacheKey         = s"$botId.$conversationId"

  val duration: FiniteDuration = 30.minutes

  trait WithValidBot {
    val chat       : Chat            = mock[Chat]
    val futureChat : Future[Chat]    = Future.successful(chat)
    val cache      : AsyncCacheApi   = mock[AsyncCacheApi]
    val botService : BotService      = mock[BotService]
    val chatService: ChatServiceImpl = spy(new ChatServiceImpl(cache, botService))

    when(chat.multisentenceRespond(userMessage)) thenReturn expectedResponse
    when(cache.get[Chat](cacheKey)) thenReturn chatToReturn

    def chatToReturn: Future[Option[Chat]]
  }

  trait WithInvalidBot {
    val cache     : AsyncCacheApi = mock[AsyncCacheApi]
    val botService: BotService    = mock[BotService]
    val chat      : Chat          = mock[Chat]
    val chatService               = new ChatServiceImpl(cache, botService)

    when(cache.get[Chat](cacheKey)) thenReturn Future.successful(None)
    when(botService.newBot(botId)) thenThrow new BotNotFoundException(botId)
  }

  trait WithCachedChat extends WithValidBot {
    @Override def chatToReturn: Future[Some[Chat]] = Future.successful(Some(chat))
  }

  trait WithUncachedChat extends WithValidBot {
    @Override def chatToReturn = Future.successful(None)

    doReturn(futureChat, futureChat).when(chatService).newChat(botId)
    doReturn(futureChat, futureChat).when(chatService).set(cacheKey, futureChat)
  }

  describe("#process") {
    describe("valid botId") {
      describe("cached bot") {
        it("should return a response") {
          new WithCachedChat {
            whenReady(chatService.process(botId, conversationId, userMessage)) { response =>
              response should be(expectedResponse)
            }
          }
        }
      }

      describe("uncached bot") {
        it("should cache it") {
          new WithUncachedChat {
            whenReady(chatService.process(botId, conversationId, userMessage)) { response =>
              verify(chatService, times(1)).set(cacheKey, futureChat)
            }
          }
        }
        it("should return a response") {
          new WithUncachedChat {
            whenReady(chatService.process(botId, conversationId, userMessage)) { response =>
              verify(chat, times(1)).multisentenceRespond(userMessage)
            }
          }
        }
      }
    }

    describe("Invalid botId") {
      it("should throw an exception") {
        new WithInvalidBot {
          whenReady(chatService.process(botId, conversationId, userMessage).failed) { response =>
            response shouldBe a[BotNotFoundException]
          }
        }
      }
    }
  }
}
