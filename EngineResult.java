package chess;

import java.util.ArrayList;

final class EngineResult {
    final boolean executed;
    final Position nextPosition;
    final ReturnPlay.Message message;
    final ArrayList<ReturnPiece> overridePieces; // usually null

    private EngineResult(boolean ex, Position np, ReturnPlay.Message m, ArrayList<ReturnPiece> pieces) {
        this.executed = ex; this.nextPosition = np; this.message = m; this.overridePieces = pieces;
    }
    static EngineResult illegal(Position current)         { return new EngineResult(false, null, ReturnPlay.Message.ILLEGAL_MOVE, null); }
    static EngineResult ok(Position next, ReturnPlay.Message msg) { return new EngineResult(true, next, msg, null); }
    static EngineResult okWithDraw(Position next)         { return new EngineResult(true, next, ReturnPlay.Message.DRAW, null); }
}
