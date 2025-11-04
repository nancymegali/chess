

import java.util.ArrayList;

final class ReturnBuilder {
    static ReturnPlay from(Board board, ArrayList<ReturnPiece> overridePieces) {
        ReturnPlay rp = new ReturnPlay();
        rp.piecesOnBoard = (overridePieces != null) ? overridePieces : board.toReturnPieces();
        rp.message = null;
        return rp;
    }
}
