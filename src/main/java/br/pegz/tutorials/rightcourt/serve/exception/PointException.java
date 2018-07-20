package br.pegz.tutorials.rightcourt.serve.exception;

import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.persistence.enums.Side;

public class PointException extends Throwable {

    private Side side;
    private int playCount;
    private int code = 100000;

    private PointException(Side side, int playCount) {
        super("Point for side: " + side);
        this.side = side;
        this.playCount = playCount;
        if(Side.LEFT == side) {
            code += 10;
        } else if (Side.RIGHT == side){
            code += 11;
        }
    }

    public PointException(Play play) {
        this(play.getIncomingSide(), play.getCount());
    }

    public int getPlayCount() {
        return playCount;
    }

    public int getCode() {
        return code;
    }

    public Side getSide() {
        return side;
    }

    @Override
    public String toString() {
        return String.format("{\"error\":\"%s\",\"errorCode\":%d}", getMessage(), code);
    }
}
