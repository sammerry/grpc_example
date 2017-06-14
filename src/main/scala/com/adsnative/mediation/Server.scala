package com.adsnative.mediation

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.example.protos.hello.{GoodByeReply, HelloReply, NameRequest, GreeterGrpc}
import com.typesafe.scalalogging.LazyLogging
import io.grpc.stub.StreamObserver
import io.grpc.{Server, ServerBuilder}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by sam on 6/13/17.
  */


class HelloWorldServer(executionContext: ExecutionContext) { self =>
  private[this] var server: Server = null

  private def start(): Unit = {
    server = ServerBuilder
      .forPort(HelloWorldServer.port)
      .addService(GreeterGrpc.bindService(new GreeterImpl, executionContext))
      .build.start

    sys.addShutdownHook {
      System.err.println("*** shutting down gRPC server since JVM is shutting down")
      self.stop()
      System.err.println("*** server shut down")
    }
  }

  private def stop(): Unit = {
    if (server != null) {
      server.shutdown()
    }
  }

  private def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination()
    }
  }

  private class GreeterImpl extends GreeterGrpc.Greeter {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    override def sayHello(req: NameRequest) = {
      val reply = HelloReply(message = "Say Hello " + req.name)
      Future.successful(reply)
    }

    override def sayGoodbye(req: NameRequest, responseObserver: StreamObserver[GoodByeReply]): Unit = {
      val response1 = Promise[GoodByeReply]
      val response2 = Promise[GoodByeReply]

      Future {
        Thread.sleep(10)
        response1.complete(Try(new GoodByeReply(message="good bye 1")))
      }

      Future {
        Thread.sleep(10)
        response2.complete(Try(new GoodByeReply(message="good bye 2")))
      }

      // build list of futures and send them as they resolve
      val futureList = List(response1.future, response2.future)

      val responses = futureList.map { sleeper =>
        val responsePromise = Promise[GoodByeReply]
        sleeper.onComplete {

          case Success(message) => {
            println(s"Future Response Success: $message")
            responseObserver.onNext(message)
            responsePromise.complete(Try(message))
          }

          case Failure(err) =>
            responsePromise.failure(err)
          }

        // convert to a future
        responsePromise.future
      }


      // wait for all futures resolve before completing the stream
      val responseSequence = Future.sequence(responses)
      Await.ready(responseSequence, Duration.Inf)
        .onComplete { x =>
          println("complete")
          Thread.sleep(1000)
          responseObserver.onCompleted()
        }
    }
  }
}

object HelloWorldServer extends LazyLogging {
  def main(args: Array[String]): Unit = {
    val server = new HelloWorldServer(ExecutionContext.global)
    server.start()
    server.blockUntilShutdown()
  }

  private val port = 50051
}
