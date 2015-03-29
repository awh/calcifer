import java.net.InetSocketAddress

import akka.actor.{Props, ActorSystem}

/**
 * Created by awh on 25/03/2015.
 */
object IOMain extends App {

  val system = ActorSystem("myactorsystem")

  val listener = system.actorOf(Props[Listener])

  val client = system.actorOf(Client.props("localhost", 6667, listener))

  Console.println("Starting...");

}
