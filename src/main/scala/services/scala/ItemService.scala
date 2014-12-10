package services.scala

import scala.language.{ postfixOps }
import scala.util.Random.{ nextLong, nextInt, nextDouble }
import scala.collection.immutable.Seq

case class Item(id: String, client: Int, price: Double)

class ItemService {

  val prices = (10.0 to 100.0 by 10)

  def getItems(client: Int): Seq[Item] = {
    val result = (0 to 50).map { _ =>
      Item(id = nextLong.toHexString, client = client, price = prices(math.abs(nextInt) % 9))
    }.toSeq
    // waiting for db, webservices,...
    Thread sleep (nextDouble * 100.0).toLong
    println(s"${Thread.currentThread.getName} : finished")
    result
  }

}

class FlakyItemService extends ItemService {
  
  /**
   * creates random errors plus a stable error for clientId == 2
   */
  override def getItems(client: Int) : Seq[Item] = {
    val result = super.getItems(client)
    
    // flakiness
    if(nextDouble < 0.2 || client == 2) {
      throw new IllegalStateException(s"Something went wrong for client $client")
    }
    
    result
  }
  
}