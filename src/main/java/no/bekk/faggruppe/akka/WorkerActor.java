package no.bekk.faggruppe.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.DiagnosticLoggingAdapter;
import akka.event.Logging;
import no.bekk.faggruppe.akka.messages.DoWork;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorkerActor extends UntypedActor {

    private final DiagnosticLoggingAdapter log = Logging.getLogger(this);

    public static Props mkProps() {
        return Props.create(WorkerActor.class);
    }

    public void onReceive(Object msg) {
        Map<String, Object> mdc = new HashMap<>();
        mdc.put("transactionId", UUID.randomUUID().toString());
        log.setMDC(mdc);
        try {
            if (msg instanceof DoWork) {
                log.debug("Received work!");
                Thread.sleep(2000);
                sender().tell("Hello world!", self());
            } else {
                unhandled(msg);
            }
        }
        catch(Exception e) {
            log.error(e, "Error occured");
        }
        finally {
            log.clearMDC();
        }
    }
}
