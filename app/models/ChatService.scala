package models

import javax.inject.{Inject, Singleton}

import org.alicebot.ab.Chat
import play.api.cache.AsyncCacheApi
import play.cache.NamedCache

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

trait ChatService {
  def process(botId: String, conversationId: String, userMessage: String)(implicit ec: ExecutionContext): Future[String]

  def set(key: String, chat: Future[Chat])(implicit ec: ExecutionContext): Future[Chat]

  def newChat(botId: String)(implicit ec: ExecutionContext): Future[Chat]
}

@Singleton
class ChatServiceImpl @Inject()(@NamedCache("chat-cache") chatCache: AsyncCacheApi, botService: BotService) extends ChatService {
  private val defaultDuration = 30.minutes

  def process(botId: String, conversationId: String, userMessage: String)(implicit ec: ExecutionContext): Future[String] = {
    val key = s"$botId.$conversationId"
    chatCache.get[Chat](key).flatMap {
      case None       => set(key, newChat(botId))
      case Some(chat) => Future.successful(chat)
    }.map(_.multisentenceRespond(userMessage))
  }

  def set(key: String, futureChat: Future[Chat])(implicit ec: ExecutionContext): Future[Chat] = {
    for {
      chat <- futureChat
      _    <- chatCache.set(key, chat, defaultDuration)
    } yield chat
  }

  def newChat(botId: String)(implicit ec: ExecutionContext): Future[Chat] = newBot(botId).map(new Chat(_))

  private def newBot(botId: String)(implicit ec: ExecutionContext) = botService.newBot(botId)
}
