package com.utm

import akka.actor.{Actor, ActorRef}

import scala.collection.mutable.ListBuffer

object SubscriberManager extends Actor {

  var addressesTweets = new ListBuffer[ActorRef]
  var addressesUsers = new ListBuffer[ActorRef]

  def receive: Receive = {
    case "" =>
  }
}
