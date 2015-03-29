
import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{Dns, IO, Tcp}
import akka.util.ByteString
import java.net.InetSocketAddress

object Client {
  def props(name: String, port: Int, replies: ActorRef) =
    Props(classOf[Client], name, port, replies)
}

class Client(name: String, port: Int, listener: ActorRef) extends Actor {

  import Dns._
  import Tcp._
  import context.system

  IO(Dns) ! Resolve(name)

  def receive = {
    case Resolved(name, ipv4s, ipv6s) =>
      Console.println("IPv4 addresses " + ipv4s)
      Console.println("IPv6 addresses " + ipv6s)
      IO(Tcp) ! Connect(new InetSocketAddress(ipv6s.head, port))

    case CommandFailed(_: Connect) =>
      listener ! "connect failed"
      context stop self

    case c @ Connected(remote, local) =>
      listener ! c
      val connection = sender()
      connection ! Register(self)
      context become {
        case data: ByteString =>
          connection ! Write(data)
        case CommandFailed(w: Write) =>
          // O/S buffer was full
          listener ! "write failed"
        case Received(data) =>
          listener ! data
        case "close" =>
          connection ! Close
        case _: ConnectionClosed =>
          listener ! "connection closed"
          context stop self
      }
  }
}

