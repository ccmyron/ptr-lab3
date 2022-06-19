package com.utm

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp
import akka.util.ByteString

import scala.collection.mutable.ListBuffer

class SimplisticHandler extends Actor {

  import Tcp._

  var addressesTweets = new ListBuffer[ActorRef]
  var addressesUsers = new ListBuffer[ActorRef]

  def receive: Receive = {
    case Received(data) =>
      //       val message = data.utf8String

      if (data.utf8String.contains("users_topic")) {
        if (addressesUsers.nonEmpty) {
          addressesUsers.foreach(userSubscriber => {
            userSubscriber ! Write(ByteString("SERVER_Users_topics: ").concat(ByteString(data.utf8String)))
          })
        }
      } else if (data.utf8String.contains("tweets_topic")) {
        if (addressesTweets.nonEmpty) {
          addressesTweets.foreach(tweetsSubscriber => {
            tweetsSubscriber ! Write(ByteString("SERVER_Tweets_topics: ").concat(ByteString(data.utf8String)))
          })
        }
      }
      else if (data.utf8String.equals("subscribe to tweets")) {
        println("Client " + sender().path + " subscribed to tweets")
        addressesTweets += sender()
        sender ! Write(ByteString("subscribed you to tweets"))
      }
      else if (data.utf8String.equals("subscribe to users")) {
        println("Client " + sender().path + " subscribed to users")
        addressesUsers += sender()
      }
      else if (data.utf8String.equals("unsubscribe from users")) {
        println("Client " + sender().path + " unsubscribed from users")
        addressesUsers -= sender()
      }
      else if (data.utf8String.equals("unsubscribe from tweets")) {
        println("Client " + sender().path + " unsubscribed from tweets")
        addressesTweets -= sender()
      }
    case PeerClosed => context stop self
  }
}
