package services.scala

import scala.concurrent._
import SummarySerivce._

class SummaryService(itemService: ItemService) {

  /**
   * @return future result - number of products per client
   */
  def numberOfItems(clientIds: List[Int]): Future[List[NumberOfItems]] = ???

}

object SummarySerivce {

  /** Result for numberOfItems */
  case class NumberOfItems(clientId: Int, num: Int)

}