package br.pegz.tutorials.rightcourt.serve;

import br.pegz.tutorials.rightcourt.configuration.CourtRestConfiguration;
import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.serve.exception.PointException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class CourtResource {

    private final RestTemplate restTemplate;
    private CourtRestConfiguration courtRestConfiguration;

    @Autowired
    public CourtResource(RestTemplate restTemplate, CourtRestConfiguration courtRestConfiguration) {
        this.restTemplate = restTemplate;
        this.courtRestConfiguration = courtRestConfiguration;
    }

    public Play sendPlayToOtherSide(Play myPlay) throws PointException {
        log.info("Responding play with {}", myPlay);
        Assert.isTrue(courtRestConfiguration.checkLeftCourtStatus(), "Left Court not available, please retry later");
        ResponseEntity<Play> playResponseEntity = restTemplate.postForEntity(CourtRestConfiguration.LEFT_PLAY, myPlay, Play.class);
        if(playResponseEntity.getStatusCode().is5xxServerError()) {
            throw new PointException(myPlay.getIncomingSide());
        } else {
            return playResponseEntity.getBody();
        }
    }

}
