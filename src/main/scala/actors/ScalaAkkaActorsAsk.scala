package actors

import scala.language.{ postfixOps }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import services.scala.{ Item, ItemService }
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import ItemServiceActor._

object ScalaAkkaActorsAsk extends App {

  val system = ActorSystem()
  val itemService = system.actorOf(Props[ItemServiceActor], "itemService")

  // available clients
  val clients = 1 until 10 toSeq

  // how long until the ask times out
  implicit val timeout = Timeout(10 seconds)
  // start the futures
  val itemFutures: Seq[Future[Seq[Item]]] = clients map { client =>
    (itemService ? GetItems(client)).mapTo[Seq[Item]]
  }

  // --------------------------- //
  // Same code like ScalaFutures //
  // --------------------------- //

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

  // Shutdown actorsystem and exit program
  system.shutdown()
}
