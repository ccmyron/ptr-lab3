package com.utm

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp
import akka.util.ByteString

class SimplisticHandler extends Actor {

  import Tcp._

  //akka://myServerActorSystem/user/subManager
  val subscriberManager: ActorRef = context.system.actorOf(props = Props[SubscriberManager], name = "subManager")
  //akka://myServerActorSystem/user/topicManager
  val topicManager: ActorRef = context.system.actorOf(props = Props[TopicManager], name = "topicManager")

  def receive: Receive = {
    case Received(data) =>
      val dataStr: String = data.utf8String
      if (dataStr.matches("(un)?subscribe:(.*)")) {
        subscriberManager.tell(dataStr, sender())
      } else if (dataStr.matches("post to (.*): (.*)")) {
        topicManager ! dataStr
      } else {
        sender() ! Write(ByteString("Wrong format, try again"))
      }

    case s: String =>
      sender() ! Write(ByteString(s))

    case PeerClosed => context stop self
  }
}
