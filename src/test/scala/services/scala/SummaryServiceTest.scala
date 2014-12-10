package services.scala

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import akka.actor._
import akka.testkit._
import SummarySerivce._

/**
 * @see http://doc.akka.io/docs/akka/snapshot/scala/testing.html
 * @see http://doc.scalatest.org/2.2.0/index.html#org.scalatest.concurrent.Futures
 * @see http://doc.scalatest.org/2.2.0/index.html#org.scalatest.concurrent.PatienceConfiguration
 *
 */
class SummaryServiceTest extends TestKit(ActorSystem("SummaryService"))
  with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll with ScalaFutures {

  override def afterAll {
    TestKit shutdownActorSystem system
  }

  "A summary service" when {

    // --------------------------------------

    "created with a stable item service" must {
      val summaryService = new SummaryService(new ItemService)

      "return an empty list for no client" in {

        // productsFuture: Future[List[NumberOfProducts]]
        val itemsFuture = summaryService numberOfItems Nil

        whenReady(itemsFuture) { items =>
          items shouldBe empty
        }
      }

      "return a single list for one client" ignore {
        val clientId = 1
        val itemsFuture = summaryService numberOfItems List(clientId)

        whenReady(itemsFuture) { items =>
          items should have size (1)
          items(0).clientId should be(clientId)
          items(0).num > 0 should be(true)
        }
      }

      "return a single list for multiple clients" ignore {
        val clientIds = 1 :: 2 :: 3 :: Nil
        val productsFuture = summaryService numberOfItems clientIds

        whenReady(productsFuture) { item =>
          item should have size (clientIds.size)

          item foreach {
            case NumberOfItems(id, num) =>
              clientIds should contain(id)
              num > 0 should be(true)
          }
        }
      }
    }

    // --------------------------------------

    "created with a flaky item service" must {
      val summaryService = new SummaryService(new FlakyItemService)

      "return an empty list for no client" ignore {
        val itemsFuture = summaryService numberOfItems Nil
        whenReady(itemsFuture) { items =>
          items shouldBe empty
        }
      }

      "return a single list for one client" ignore {
        val clientId = 1
        val productsFuture = summaryService numberOfItems List(clientId)

        whenReady(productsFuture) { products =>
          products should have size (1)
          products(0).clientId should be(clientId)
        }
      }

      "return a single list for multiple clients" ignore {
        val clientIds = 1 :: 2 :: 3 :: Nil
        val itemsFuture = summaryService numberOfItems clientIds

        whenReady(itemsFuture) { items =>
          items should have size (clientIds.size)

          items foreach { item =>
            clientIds should contain(item.clientId)
            item.num > 0 should be(true)
          }
        }
      }
    }
  }

  // Testing the actor implementation

  "A summary service actor" must {

    val testActor: ActorRef = null

    "handle GetNumberOfItems(Nil) requests" ignore {
      testActor ! "GetNumberOfItems(Nil)"

      val items: List[NumberOfItems] = expectMsgType[List[NumberOfItems]]
      items should be(empty)
    }

    "handle GetNumberOfItems(1 :: Nil) requests" ignore {
      testActor ! "GetNumberOfItems(1 :: Nil)"

      val items: List[NumberOfItems] = expectMsgType[List[NumberOfItems]]
      items should have size(1)
    }
    
    
    "handle GetNumberOfItems(1 :: 2 :: 3 :: Nil) requests" ignore {
      testActor ! "GetNumberOfItems(1 :: 2 :: 3 :: Nil)"

      val items: List[NumberOfItems] = expectMsgType[List[NumberOfItems]]
      items should have size(3)
    }
  }

}