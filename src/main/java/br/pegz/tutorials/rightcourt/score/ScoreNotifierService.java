package br.pegz.tutorials.rightcourt.score;

import br.pegz.tutorials.rightcourt.configuration.PlayExchange;
import br.pegz.tutorials.rightcourt.persistence.enums.Side;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScoreNotifierService {


    @SendTo(PlayExchange.SCORE_OUTPUT)
    public String notifyFoePoint(Integer count) {
        log.info("Notifying point for LEFT in the play #{}", count);
        return getNotifyScore(Side.LEFT, count);
    }

    @SendTo(PlayExchange.SCORE_OUTPUT)
    public String  notifyMyPoint(Integer count) {
        log.info("Notifying point for RIGHT in the play #{}", count);
        return getNotifyScore(Side.RIGHT, count);
    }

    private String getNotifyScore(Side winningSide, Integer count) {
        return String.format("{\"pointWinner\":\"%s\",\"playsCount\":%d}", winningSide, count);
    }
}
