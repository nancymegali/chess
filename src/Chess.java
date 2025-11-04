

import java.util.ArrayList;

public class Chess {

    enum Player { white, black }

    static Position position;

    public static void start() {
        position = Position.initial();
    }

    public static ReturnPlay play(String move) {
        if (position == null) start();

        String raw = (move == null) ? "" : move.trim();

        if (raw.equals("resign")) {
            ReturnPlay rp = new ReturnPlay();
            rp.piecesOnBoard = position.board.toReturnPieces();
            rp.message = (position.toMove == Player.white)
                    ? ReturnPlay.Message.RESIGN_BLACK_WINS
                    : ReturnPlay.Message.RESIGN_WHITE_WINS;
            return rp;
        }

        ParsedInput parsed = MoveParser.parseInput(raw);
        if (!parsed.ok) {
            ReturnPlay bad = new ReturnPlay();
            bad.piecesOnBoard = position.board.toReturnPieces();
            bad.message = ReturnPlay.Message.ILLEGAL_MOVE;
            return bad;
        }

        EngineResult er = Engine.apply(position, parsed);

        if (er.executed && er.nextPosition != null) {
            position = er.nextPosition;
        }

        ReturnPlay out = new ReturnPlay();
        out.piecesOnBoard = position.board.toReturnPieces();
        out.message = er.message; // may be null / DRAW / ILLEGAL_MOVE / CHECK later
        return out;
    }
}
