import java.util.concurrent.TimeUnit

import com.example.protos.hello.{GoodByeReply, HelloReply, NameRequest, GreeterGrpc}
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import org.scalameter.api.Reporter
import org.scalameter.reporting.RegressionReporter
import org.scalatest._
import java.util.concurrent.TimeUnit.MILLISECONDS
import org.scalameter.api._
import org.scalatest.concurrent.Eventually

import scala.concurrent.duration.Duration
import scala.concurrent.{Promise, Await, Future, ExecutionContext}
import scala.util.Try

/**
  * Created by sam on 6/14/17.
  */
object StreamClient {
  val channel = ManagedChannelBuilder
    .forAddress("127.0.0.1", 50051)
    .idleTimeout(1000, MILLISECONDS)
    .usePlaintext(true).build
}

class TestRequests extends FlatSpec with Matchers {
  val channel = StreamClient.channel

  "HTTP2 Hello request" should "return a value" in {
    val request = NameRequest(name = "That Other Name")
    val blockingStub = GreeterGrpc.blockingStub(channel)
      .withDeadlineAfter(3, TimeUnit.SECONDS)
    val reply: HelloReply = blockingStub.sayHello(request)
    println(reply)
  }
}

class AsyncTestRequests extends AsyncFlatSpec with Eventually {
  val channel = StreamClient.channel

  it should "eventually return 2 bids" in {
    val testFinished = Promise[Assertion]

    val responseObserver = new StreamObserver[GoodByeReply] {
      var responses = List[GoodByeReply]()

      override def onError(err: Throwable) {
        info("Error Thrown")
        err.printStackTrace()
        val responseAssertion = Try(assert(false == true))
        testFinished.complete(responseAssertion)
      }

      override def onNext(byeReply: GoodByeReply): Unit = {
        responses = responses ++ List(byeReply)
        println(byeReply)
      }

      override def onCompleted(): Unit = {
        info("Finished RecordRoute")
        val responseAssertion = Try(assert(responses.length == 2))
        testFinished.complete(responseAssertion)
      }
    }

    val request = NameRequest(name = "Gimme Names")
    val stub = GreeterGrpc.stub(channel)
      .withDeadlineAfter(3, TimeUnit.SECONDS)
    stub.sayGoodbye(request, responseObserver)
    Await.ready(testFinished.future, Duration.Inf)
  }
}
