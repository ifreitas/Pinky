package models

import javax.inject.Inject

import org.alicebot.ab.Bot
import play.api.Configuration

import scala.concurrent.{ExecutionContext, Future}

trait BotService extends {
  def newBot(botId: String)(implicit ec: ExecutionContext): Future[Bot] = Future {
    validate(botId)
    doNewBot(botId)
  }

  def validate(botId: String): Unit = if (!exists(botId)) throw new BotNotFoundException(botId)

  private[this] def exists(botId: String): Boolean = {
    val botDir = new java.io.File(pathTo(botId))
    botDir.exists && botDir.isDirectory
  }

  private[this] def doNewBot(botId: String): Bot = new Bot(botId, bots_home)

  private[this] def pathTo(botId: String) = s"$bots_home/bots/$botId"

  protected def bots_home: String
}

class BotServiceImpl @Inject()(config: Configuration) extends BotService {
  override def bots_home: String = config.getOptional[String]("bots_home").getOrElse(System.getProperty("user.dir"))
}

class BotNotFoundException(botId: String) extends RuntimeException(s"Bot '$botId' does not exists")
