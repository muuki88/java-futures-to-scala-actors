package futures

import scala.language.{ postfixOps }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import services.scala.{ Item, ItemService }

object ScalaFutures extends App {

  val clients = 1 until 10 toSeq

  // start the futures
  val itemFutures: Seq[Future[Seq[Item]]] = clients map { client =>
    Future {
      new ItemService getItems client
    }
  }

  // convert list of futures to future of results
  val resultFuture: Future[Seq[Seq[Item]]] = Future sequence itemFutures

  // flatten the result
  val itemsFuture: Future[Seq[Item]] = resultFuture map (_.flatten)

  // blocking until all futures are finished
  val items = Await.result(itemsFuture, 10 seconds)

  val groupedByPrice = items groupBy (_.price)

  // show results 
  groupedByPrice foreach {
    case (price, items) =>
      val itemsPerClient = items groupBy (_.client)
      val output = s"""|==== PRICE $price =====
          |  Items   : ${items.size}
          |  Clients : ${itemsPerClient.size}
      """.stripMargin
      println(output)
  }
}