package br.pegz.tutorials.rightcourt.serve.resource;

import br.pegz.tutorials.rightcourt.configuration.AMQPConfig;
import br.pegz.tutorials.rightcourt.configuration.CourtRestConfiguration;
import br.pegz.tutorials.rightcourt.configuration.features.enums.RightCourtFeatures;
import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.serve.exception.PointException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.togglz.core.manager.FeatureManager;

@Slf4j
@Service
public class CourtResource {

    private final RestTemplate restTemplate;
    private final CourtRestConfiguration courtRestConfiguration;
    private final RabbitTemplate rabbitTemplate;
    private final FeatureManager featureManager;

    public CourtResource(RestTemplate restTemplate, CourtRestConfiguration courtRestConfiguration, RabbitTemplate rabbitTemplate, FeatureManager featureManager) {
        this.restTemplate = restTemplate;
        this.courtRestConfiguration = courtRestConfiguration;
        this.rabbitTemplate = rabbitTemplate;
        this.featureManager = featureManager;
    }

    public Play sendPlayToOtherSide(Play myPlay) throws PointException {
        log.info("Responding play with {}", myPlay);
        if(featureManager.isActive(RightCourtFeatures.COURT_ASYNC_FEATURE)) {
            sendPlayAsync(myPlay);
            return null;
        }
        return getPlayFromRest(myPlay);

    }

    private void sendPlayAsync(Play myPlay) {
        rabbitTemplate.convertAndSend(AMQPConfig.PLAY_QUEUE_EXCHANGE, "right-play", myPlay);
    }

    @RabbitListener(queues = AMQPConfig.PLAYS_QUEUE)
    public Play receivePlay(Play playMessage) {
        return playMessage;
    }

    private Play getPlayFromRest(Play myPlay) throws PointException {
        Assert.isTrue(courtRestConfiguration.checkLeftCourtStatus(), "Left Court not available, please retry later");
        ResponseEntity<Play> playResponseEntity = restTemplate.postForEntity(CourtRestConfiguration.LEFT_PLAY, myPlay, Play.class);
        if(playResponseEntity.getStatusCode().is5xxServerError()) {
            throw new PointException(myPlay.getIncomingSide());
        } else {
            return playResponseEntity.getBody();
        }
    }


}
