package com.utm

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp.{PeerClosed, Received, Write}

import scala.collection.mutable.ListBuffer

class SubscriberManager extends Actor {

  var addressesUsers = new ListBuffer[ActorRef]

  def receive: Receive = {
    case s: String =>
      println(s)
      val topicsStr: String = s.substring(s.lastIndexOf(":") + 1)
      val topics: Array[String] = topicsStr.split(",").map(_.trim)
      topics.foreach(println)

    case PeerClosed => context stop self
  }
}
