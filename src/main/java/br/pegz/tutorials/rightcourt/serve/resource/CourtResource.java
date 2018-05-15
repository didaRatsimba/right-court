package br.pegz.tutorials.rightcourt.serve.resource;

import br.pegz.tutorials.rightcourt.configuration.PlayExchange;
import br.pegz.tutorials.rightcourt.persistence.Play;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.reactive.StreamEmitter;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
public final class CourtResource {

    
    @SendTo(PlayExchange.PLAY_OUTPUT)
    public void sendPlay(Play play) {
        log.info("Responding play with {}", play);

    }

}
