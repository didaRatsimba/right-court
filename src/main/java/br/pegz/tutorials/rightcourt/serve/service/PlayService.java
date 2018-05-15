package br.pegz.tutorials.rightcourt.serve.service;

import br.pegz.tutorials.rightcourt.configuration.PlayExchange;
import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.persistence.enums.Height;
import br.pegz.tutorials.rightcourt.persistence.enums.Side;
import br.pegz.tutorials.rightcourt.persistence.enums.Speed;
import br.pegz.tutorials.rightcourt.score.ScoreNotifierService;
import br.pegz.tutorials.rightcourt.serve.exception.PointException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.reactive.StreamEmitter;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class PlayService {

    private final ScoreNotifierService scoreNotifierService;

    public PlayService(ScoreNotifierService scoreNotifierService) {
        this.scoreNotifierService = scoreNotifierService;
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
    public Play handlePlay(Play incomingPlay) throws PointException {
        if (canContinuePlay(incomingPlay)) {
            return buildResponsePlay(incomingPlay);
        }
        notifyPoint(incomingPlay);
        return null;
    }

    private boolean canContinuePlay(Play incomingPlay) {
        return !isLeftPoint(incomingPlay) && !isRightPoint(incomingPlay);
    }

    private void notifyPoint(Play incomingPlay) throws PointException {
        if (isLeftPoint(incomingPlay)) {
            scoreNotifierService.notifyFoePoint(incomingPlay.getCount());
            throw new PointException(Side.LEFT);
        } else {
            scoreNotifierService.notifyMyPoint(incomingPlay.getCount());
            throw new PointException(Side.RIGHT);
        }
    }

    private boolean isLeftPoint(Play incomingPlay) {
        return !isRightPoint(incomingPlay) && Speed.OMFG == incomingPlay.getSpeed() || Height.BEYOND_REACH == incomingPlay.getHeight();
    }

    private boolean isRightPoint(Play incomingPlay) {
        return Side.NET == incomingPlay.getInnerSide() || Side.OUTSIDE == incomingPlay.getInnerSide();
    }

    private Play buildResponsePlay(Play incomingPlay) {
        return Play.builder()
                .count(incomingPlay.getCount()+1)
                .effect(!incomingPlay.getEffect())
                .speed(Speed.random())
                .height(Height.random())
                .incomingSide(Side.RIGHT)
                .innerSide(Side.random())
                .build();
    }
}
