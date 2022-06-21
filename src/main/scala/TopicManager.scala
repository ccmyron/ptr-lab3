package com.utm

import akka.actor.{Actor, ActorRef}

import scala.collection.immutable.HashMap
import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class TopicManager extends Actor {
  var subManager: ActorRef = null
  var topicsMap = new HashMap[String, mutable.Queue[String]]

  override def preStart(): Unit = {
    context.system.scheduler.scheduleAtFixedRate(0.milli, 2.seconds, self, "dequeue")
    subManager = Await.result(context.system
      .actorSelection("user/subManager")
      .resolveOne(1.second), 5.second)
  }

  def receive: Receive = {
    case s: String =>
      if (s.contains("post")) {
        handlePost(s)
      } else if (s.contains("dequeue")) {
        handleDequeue()
      }
  }

  def handlePost(s: String): Unit = {
    val topic: String = s.substring(8, s.lastIndexOf(":"))
    val message: String = s.substring(s.lastIndexOf(":") + 2)

    if (topicsMap.contains(topic)) {
      topicsMap(topic).enqueue(message)
    } else {
      topicsMap += (topic -> mutable.Queue(message))
    }

    topicsMap.foreach {
      case (key, value) => println(key + " -> " + value)
    }
  }

  def handleDequeue(): Unit = {
    topicsMap foreach {
      case (key, value) =>
        while (value.nonEmpty) {
          subManager ! key + ":" + value.dequeue()
        }
    }
  }
}
