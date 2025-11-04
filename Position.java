package chess;

final class Position {
    Board board;
    Chess.Player toMove;

    // castling/en passant (we’ll use as we implement features)
    boolean whiteCanCastleKing = true, whiteCanCastleQueen = true;
    boolean blackCanCastleKing = true, blackCanCastleQueen = true;
    Integer enPassantFile = null;

    static Position initial() {
        Position p = new Position();
        p.board = Board.initialSetup();
        p.toMove = Chess.Player.white;
        return p;
    }

    Position copy() {
        Position q = new Position();
        q.board = board.copy();
        q.toMove = toMove;
        q.whiteCanCastleKing = whiteCanCastleKing;
        q.whiteCanCastleQueen = whiteCanCastleQueen;
        q.blackCanCastleKing = blackCanCastleKing;
        q.blackCanCastleQueen = blackCanCastleQueen;
        q.enPassantFile = enPassantFile;
        return q;
    }
}
