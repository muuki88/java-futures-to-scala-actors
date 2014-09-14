package services.scala

import scala.language.{ postfixOps }
import scala.util.Random.{ nextLong, nextInt, nextDouble }

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