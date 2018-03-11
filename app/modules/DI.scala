package modules

import com.google.inject.AbstractModule
import controllers.{MyExecutionContext, MyExecutionContextImpl}
import models.{BotService, BotServiceImpl, ChatService, ChatServiceImpl}

class DI extends AbstractModule {
  def configure(): Unit = {
    bind(classOf[MyExecutionContext]).to(classOf[MyExecutionContextImpl])
    bind(classOf[ChatService]).to(classOf[ChatServiceImpl])
    bind(classOf[BotService]).to(classOf[BotServiceImpl])
  }
}
