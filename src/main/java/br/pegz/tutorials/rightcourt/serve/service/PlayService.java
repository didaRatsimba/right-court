package br.pegz.tutorials.rightcourt.serve.service;

import br.pegz.tutorials.rightcourt.configuration.PlayExchange;
import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.persistence.enums.Height;
import br.pegz.tutorials.rightcourt.persistence.enums.Side;
import br.pegz.tutorials.rightcourt.persistence.enums.Speed;
import br.pegz.tutorials.rightcourt.score.ScoreNotifierService;
import br.pegz.tutorials.rightcourt.serve.exception.PointException;
import br.pegz.tutorials.rightcourt.serve.exception.StopPlayingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.reactive.StreamEmitter;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import static br.pegz.tutorials.rightcourt.persistence.enums.Side.LEFT;
import static br.pegz.tutorials.rightcourt.persistence.enums.Side.RIGHT;

@Slf4j
@Service
public final class PlayService {

    private final ScoreNotifierService scoreNotifierService;
    private final PlayerService playerService;

    public PlayService(ScoreNotifierService scoreNotifierService, PlayerService playerService) {
        this.scoreNotifierService = scoreNotifierService;
        this.playerService = playerService;
    }

    @StreamEmitter
    @SendTo(PlayExchange.PLAY_OUTPUT)
    public Play serve() {
        return Play.builder()
                .count(0)
                .effect(true)
                .height(Height.LOW)
                .speed(Speed.FAST)
                .incomingSide(Side.RIGHT)
                .innerSide(Side.RIGHT)
                .build();
    }

    @StreamListener(PlayExchange.PLAY_INPUT)
    @SendTo(PlayExchange.PLAY_OUTPUT)
    public Play handlePlay(Play incomingPlay) throws StopPlayingException {
        try {
            return playerService.play(incomingPlay);
        } catch (PointException poex) {
            notifyPoint(poex);
            throw new StopPlayingException(poex);
        }
    }

    private void notifyPoint(PointException poex) {
        if(poex.getSide() == LEFT) {
            scoreNotifierService.notifyFoePoint(poex.getPlayCount());
        } else if (poex.getSide() == RIGHT) {
            scoreNotifierService.notifyMyPoint(poex.getPlayCount());
        }
    }


}
