package actors

import akka.actor.Actor
import services.scala.ItemService
import services.scala.Item
import ItemServiceActor._

class ItemServiceActor extends ItemService with Actor {

  def receive = {
    case GetItems(client) => sender ! getItems(client) // async answer
  }
}

object ItemServiceActor {

  case class GetItems(client: Int)
}