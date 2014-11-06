package no.bekk.faggruppe.web;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import no.bekk.faggruppe.akka.messages.DoWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;

@Controller
public class DemoController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("workerActor")
    private ActorRef worker;

    @Autowired
    private ActorSystem system;

    @RequestMapping("/hello")
    @ResponseBody
    public DeferredResult<String> helloWorld() {
        String transId = UUID.randomUUID().toString();
        MDC.put("transactionId", transId);
        try {
            log.debug("Dispatching work");
            DeferredResult<String> result = new DeferredResult<>();
            Future<Object> workerFuture = ask(worker, new DoWork(), Timeout.apply(FiniteDuration.create(5, TimeUnit.SECONDS)));
            workerFuture.onComplete(new OnComplete<Object>() {
                @Override
                public void onComplete(Throwable throwable, Object o) throws Throwable {
                    if(throwable != null) {
                        result.setErrorResult(throwable);
                    }
                    else {
                        result.setResult(o.toString());
                    }
                }
            }, system.dispatcher());
            log.debug("Returning with deferred result");
            return result;
        }
        finally {
            MDC.clear();
        }
    }
}
