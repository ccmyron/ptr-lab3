package com.utm

import akka.actor.{Actor, ActorRef}

import scala.collection.immutable.HashMap
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class SubscriberManager extends Actor {

  var handler: ActorRef = null
  var subscribersAndTopicsMap = new HashMap[ActorRef, List[String]]

  override def preStart(): Unit = {
    handler = Await.result(context.system
      .actorSelection("user/$a")
      .resolveOne(1.second), 5.second)
  }

  def receive: Receive = {
    case s: String =>
      if (s.substring(0, 2).equals("su")) {
        handleSubscription(s, sender(), isSubbing = true)
      } else if (s.substring(0, 2).equals("un")) {
        handleSubscription(s, sender(), isSubbing = false)
      } else {
        handleNotify(s)
      }
  }

  def handleSubscription(s: String, sub: ActorRef, isSubbing: Boolean): Unit = {
    val topicsStr: String = s.substring(s.lastIndexOf(":") + 1)
    val topics: List[String] = topicsStr.split(",").map(_.trim).distinct.toList
    val subscriber: ActorRef = sub

    if (isSubbing) {
      if (subscribersAndTopicsMap.contains(subscriber)) {
        val newTopics: List[String] = subscribersAndTopicsMap(subscriber) ::: topics
        subscribersAndTopicsMap += (subscriber -> newTopics.distinct)
      } else {
        subscribersAndTopicsMap += (subscriber -> topics)
      }
    } else if (!isSubbing) {
      if (subscribersAndTopicsMap.contains(subscriber)) {
        val updatedTopics: List[String] = subscribersAndTopicsMap(subscriber).filter(!topics.contains(_))
        subscribersAndTopicsMap += (subscriber -> updatedTopics)
      }
    }

    subscribersAndTopicsMap foreach {
      case (key, value) => println(key + " -> " + value)
    }
  }

  def handleNotify(s: String): Unit = {
    val topic: String = s.substring(0, s.indexOf(":"))
    val message: String = s.substring(s.lastIndexOf(":") + 1)

    subscribersAndTopicsMap foreach {
      case (key, value) =>
        if (value.contains(topic)) {
          handler.tell("topic=" + topic + ": " + message, key)
        }
    }
  }
}
