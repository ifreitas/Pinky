package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.Json

case class Message(text:String)
class EchoController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  implicit val residentWrites = Json.writes[Message]

  def index = Action {
    Ok(Json.toJson(Message("Hello!")))
  }
}
