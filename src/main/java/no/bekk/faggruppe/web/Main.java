package no.bekk.faggruppe.web;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import no.bekk.faggruppe.akka.WorkerActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }

    @Bean
    public ActorSystem system() {
        LOG.debug("Creating actor system");
        return ActorSystem.create("AkkaDemo");
    }

    @Bean(name = "workerActor")
    public ActorRef workerActor() {
        LOG.debug("Creating worker actor");
        return system().actorOf(WorkerActor.mkProps(), "workerActor");
    }
}
