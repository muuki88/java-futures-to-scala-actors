package actors

import akka.actor._
import akka.routing.RoundRobinPool
import scala.collection.mutable.{ Map => MutableMap }
import services.scala.ItemService
import services.scala.Item
import ItemServiceAggregator._

class ItemServiceAggregator extends Actor with ActorLogging {

  // Create a pool of actors with RoundRobin routing algorithm
  val worker = context.system.actorOf(
    props = Props[ItemServiceAggregator.ItemServiceActor].withRouter(RoundRobinPool(10)),
    name = "itemService"
  )

  /** aggregation map: (request, sender) -> (client, items) */
  val jobs = MutableMap[Long, Job]()

  // A VERY basic jobId algorithm
  var jobId = 0L

  def receive = {
    case GetItemStatistics(clients) =>
      jobId += 1
      jobs(jobId) = Job(jobId, sender(), clients)
      log info s"Statistics for job [$jobId]"

      // start querying
      clients foreach (worker ! GetItems(_, jobId))

    case Items(client, items, jobId) =>
      val lastJobState = jobs(jobId)
      val newJobState = lastJobState.copy(
        results = lastJobState.results :+ (client, items)
      )

      if (newJobState isFinished ()) {
        // send results and remove job
        newJobState.source ! ItemStatistics(newJobState.results)
        jobs remove jobId
      } else {
        // update job state
        jobs(jobId) = newJobState
      }
  }

}

/**
 * Defining the aggregation API
 */
object ItemServiceAggregator {

  // ---- PUBLIC ----
  case class GetItemStatistics(clients: Seq[Int])
  case class ItemStatistics(results: Seq[(Int, Seq[Item])])

  // ---- INTERNAL ----
  private[actors] case class GetItems(client: Int, job: Long)
  private[actors] case class Items(client: Int, items: Seq[Item], job: Long)

  private[actors] case class Job(id: Long, source: ActorRef, clients: Seq[Int], results: Seq[(Int, Seq[Item])] = Seq.empty) {
    def isFinished(): Boolean = results.size == clients.size
  }

  /** Worker actor similar to ask pattern ServiceActor */
  class ItemServiceActor extends ItemService with Actor {

    def receive = {
      case GetItems(client, job) => sender ! Items(client, getItems(client), job) // async answer
    }
  }

}