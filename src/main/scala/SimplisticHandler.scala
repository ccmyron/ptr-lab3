package com.utm

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp

class SimplisticHandler extends Actor {

  import Tcp._

  val subscriberManager: ActorRef = context.system.actorOf(props = Props[SubscriberManager])

  def receive: Receive = {
    case Received(data) =>
      if (data.utf8String.matches("subscribe: (.*)")) {
        println(data.utf8String)
        subscriberManager ! sender().toString() + " " + data.utf8String
      }

    case PeerClosed => context stop self
  }
}
