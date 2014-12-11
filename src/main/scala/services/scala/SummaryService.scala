package services.scala

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import SummarySerivce._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

class SummaryService(itemService: ItemService, system: ActorSystem) {

  implicit val timeout = Timeout(5 seconds)
  val summary = system.actorOf(Props[SummaryServiceActor])

  /**
   * @return future result - number of products per client
   */
  def numberOfItems(clientIds: List[Int]): Future[List[NumberOfItems]] = {
    (summary ? SummaryServiceActor.GetNumberOfItems(clientIds))
      .mapTo[List[NumberOfItems]]
  }

}

object SummarySerivce {

  /** Result for numberOfItems */
  case class NumberOfItems(clientId: Int, num: Int)

}