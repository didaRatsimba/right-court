package br.pegz.tutorials.rightcourt.serve.service;

import br.pegz.tutorials.rightcourt.configuration.PlayExchange;
import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.persistence.enums.Height;
import br.pegz.tutorials.rightcourt.persistence.enums.Side;
import br.pegz.tutorials.rightcourt.persistence.enums.Speed;
import br.pegz.tutorials.rightcourt.score.ScoreNotifierService;
import br.pegz.tutorials.rightcourt.serve.exception.PointException;
import br.pegz.tutorials.rightcourt.serve.resource.CourtResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Slf4j
@Service
public final class PlayService {

    private final CourtResource courtResource;
    private final ScoreNotifierService scoreNotifierService;

    public PlayService(CourtResource courtResource, ScoreNotifierService scoreNotifierService) {
        this.courtResource = courtResource;
        this.scoreNotifierService = scoreNotifierService;
    }

    public void serve() {
        courtResource.sendPlay(getServePlay());
    }

    @StreamListener(PlayExchange.PLAY_INPUT)
    public void handlePlay(Play incomingPlay) throws PointException {
        if(canContinuePlay(incomingPlay)) {
            courtResource.sendPlay(buildResponsePlay(incomingPlay));
        } else {
            notifyPoint(incomingPlay);
        }
    }

    private boolean canContinuePlay(Play incomingPlay) {
        return !isLeftPoint(incomingPlay) && !isRightPoint(incomingPlay);
    }

    private void notifyPoint(Play incomingPlay) throws PointException {
        if (isLeftPoint(incomingPlay)) {
            scoreNotifierService.notifyFoePoint(incomingPlay.getCount());
            throw new PointException(Side.LEFT);
        } else if (isRightPoint(incomingPlay)) {
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

    private Play getServePlay() {
        return Play.builder()
                .count(0)
                .effect(true)
                .height(Height.LOW)
                .speed(Speed.FAST)
                .incomingSide(Side.RIGHT)
                .innerSide(Side.RIGHT)
                .build();
    }
}
