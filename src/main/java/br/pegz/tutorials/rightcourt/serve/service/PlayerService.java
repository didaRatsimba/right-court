package br.pegz.tutorials.rightcourt.serve.service;

import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.persistence.enums.Height;
import br.pegz.tutorials.rightcourt.persistence.enums.Side;
import br.pegz.tutorials.rightcourt.persistence.enums.Speed;
import br.pegz.tutorials.rightcourt.serve.exception.PointException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class PlayerService {

    public Play play(Play incomingPlay) throws PointException {
        if (canContinuePlay(incomingPlay)) {
            log.info("Receiving play from {}", incomingPlay);
            return buildResponsePlay(incomingPlay);
        }
        throw new PointException(incomingPlay);
    }

    private boolean canContinuePlay(Play incomingPlay) {
        return !isLeftPoint(incomingPlay) && !isRightPoint(incomingPlay);
    }

    private boolean isLeftPoint(Play incomingPlay) {
        return !isRightPoint(incomingPlay) && Speed.OMFG == incomingPlay.getSpeed() || Height.BEYOND_REACH == incomingPlay.getHeight();
    }

    private boolean isRightPoint(Play incomingPlay) {
        return Side.NET == incomingPlay.getInnerSide() || Side.OUTSIDE == incomingPlay.getInnerSide();
    }

    private Play buildResponsePlay(Play incomingPlay) {
        final Play play = Play.builder()
                .count(incomingPlay.getCount() + 1)
                .effect(!incomingPlay.getEffect())
                .speed(Speed.random())
                .height(Height.random())
                .incomingSide(Side.RIGHT)
                .innerSide(Side.random())
                .build();
        log.info("Built play [{}]", play);
        return play;
    }

}
