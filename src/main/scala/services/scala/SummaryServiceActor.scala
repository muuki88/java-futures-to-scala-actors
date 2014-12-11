package services.scala

import akka.actor._
import akka.pattern.pipe
import scala.concurrent._

class SummaryServiceActor extends Actor {

  import SummaryServiceActor._
  import SummarySerivce._
  import context._

  def receive: PartialFunction[Any, Unit] = {
    case GetNumberOfItems(clientIds) =>
      val futures = clientIds.map { id =>
        Future {
          val itemService = new ItemService
          NumberOfItems(id, itemService.getItems(id).size)
        } recover {
          case e: IllegalStateException => NumberOfItems(id, -1)
        }
      }

      val result = Future.sequence(futures)

      result pipeTo sender
  }
}

object SummaryServiceActor {

  case class GetNumberOfItems(clientIds: List[Int])
}