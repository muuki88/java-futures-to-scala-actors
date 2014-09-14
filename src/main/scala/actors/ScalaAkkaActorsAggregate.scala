package actors

import scala.language.{ postfixOps }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import services.scala.{ Item, ItemService }
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import actors.ItemServiceAggregator._

object ScalaAkkaActorsAggregate extends App {

  val system = ActorSystem()
  val itemService = system.actorOf(Props[ItemServiceAggregator], "itemServiceAggregator")

  // available clients
  val clients = 1 until 10 toSeq

  // how long until the ask times out
  implicit val timeout = Timeout(10 seconds)
  // start the futures
  val statsFuture: Future[ItemStatistics] = (itemService ? GetItemStatistics(clients)).mapTo[ItemStatistics]

  val itemsFuture = statsFuture map {
    case ItemStatistics(results) => results.map(_._2).flatten
  }

  val items = Await.result(itemsFuture, 10 seconds)

  // --------------------------- //
  // Same code like ScalaFutures //
  // --------------------------- //

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

  // Shutdown actorsystem and exit program
  system.shutdown()
}