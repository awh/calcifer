import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.util.ByteString

class Listener extends Actor {

  val log = Logging(context.system, this)

  def receive = {
    case bs: ByteString => Console.println(bs.decodeString("UTF8"))
    case s => Console.println(s.toString)
  }

}
