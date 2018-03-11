package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import models.ChatService
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.concurrent.CustomExecutionContext
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

trait MyExecutionContext extends ExecutionContext

@Singleton
class MyExecutionContextImpl @Inject()(system: ActorSystem) extends CustomExecutionContext(system, "my-context") with MyExecutionContext

case class Message(text: String)

@Singleton
class ChatController @Inject()(chatService: ChatService, controllerComponents: ControllerComponents)(implicit myExecutionContext: MyExecutionContext) extends AbstractController(controllerComponents) {
  implicit val messageWrites = Json.writes[Message]
  val messageForm = Form(
    mapping(
      "text" -> nonEmptyText
    )(Message.apply)(Message.unapply)
  )

  def chat(botId: String, chatId: String) = Action.async { implicit request =>
    handleMessage {
      chatService.process(botId, chatId, _)
    }
  }

  private[this] def handleMessage(block: String => Future[String])(implicit request: Request[AnyContent]): Future[Result] = {
    messageForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(badRequest("The message's text is required."))
      },
      validMessage => {
        block(validMessage.text).map(ok).recover { case exception => badRequest(exception.getLocalizedMessage) }
      }
    )
  }

  private[this] def ok(msg: String) = Ok(Json.toJson(Message(msg)))

  private[this] def badRequest(exception: String) = BadRequest(Json.obj("error" -> exception))
}
