package com.jayas.gonotify.gattest

import java.util.UUID

import scala.util.Properties._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.config.HttpProtocolBuilder.toHttpProtocol
import io.gatling.http.request.builder.HttpRequestBuilder.toActionBuilder

import scala.concurrent.duration.DurationInt

/**
 * Performance tests the websocket notification feature of gonotify
 */
class GoNotifyWSTest extends Simulation {

 val maxUsers = envOrPropOrElse("maxUsers", "600").toInt
 val testDuration = envOrPropOrElse("testDuration", "60").toInt
 val messagingInterval = envOrPropOrElse("messagingInterval", "10").toInt
 val userRampupDuration = envOrPropOrElse("userRampupDuration", "30").toInt
 
  /**
   * http protocol to be used for performance test
   */
  val httpAndWSConf = http
    .baseURL("https://gonotify.herokuapp.com")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .wsBaseURL("wss://gonotify.herokuapp.com")
    .shareConnections

  /**
   * data feeder for performance test (each scenario will be fed by this feeder)
   */
  val feeder = new Feeder[String] {

    // always return true as this feeder can be polled infinitively
    override def hasNext: Boolean = true
    var counter : Long = 0

    override def next: Map[String, String] = {
     counter = counter +1
      Map("clientId" -> UUID.randomUUID().toString,
          "counter" -> counter.toString)

    }
  }
 
 
  def notifyMsg = "This is test message # ${counter}"
  val bodyForNotifyMsg = StringBody("{\"message\": \""+notifyMsg+"\"}".stripMargin)
  
  val openWebSocket = exec(ws("reg_listen_ws_client")
    .open("/ws/${clientId}"))
  
  val notifyMessage = exec(http("notify_msg")
    .post("/notify/${clientId}")
    .body(bodyForNotifyMsg).asJSON)
    .pause(1 seconds)

  def listenForMessage(listenDuration: Int, numOfMessages: Int) = ws("Listen For Messages")
    .check(wsListen.within(listenDuration).expect(numOfMessages)
      .regex(notifyMsg))

  val scn = scenario("gonotify Load Test ")
    .feed(feeder)
    .exec(openWebSocket)

    .during(testDuration, "", false) {
      exec(listenForMessage(messagingInterval, 1))
        .exec(notifyMessage)
        .pause(messagingInterval seconds)

    }
    .exec(ws("Close WS").close)
  

  /**
   * Starts performance test
   */
  setUp(scn.inject(rampUsers(maxUsers) over (userRampupDuration)).protocols(httpAndWSConf))
  
  def envOrPropOrElse(key : String, default: String) : String = {
    envOrElse(key, propOrElse(key, default))
  }

}
