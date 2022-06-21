package com.utm

import akka.actor.Status.{Failure, Success}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.ByteString

import java.lang.System.Logger
import java.net.InetSocketAddress
import scala.concurrent.duration.DurationInt

object Main {
  def main(args: Array[String]): Unit = {
    val host = "localhost"
    val port = 8080

    println(s"Server started! listening on $host:$port")
    val actorSystem: ActorSystem = ActorSystem.create("myServerActorSystem")
    val handler = actorSystem.actorOf(Props[SimplisticHandler])
    val serverProps = Server.props(new InetSocketAddress(host, port), handler)

    val serverActor: ActorRef = actorSystem.actorOf(serverProps)
    serverActor ! ByteString("Starting server...")
  }
}
