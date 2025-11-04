

import java.util.ArrayList;

final class Board {
    private final Piece[][] sq = new Piece[8][8]; // [file][rank]

    Piece get(int f, int r) { return sq[f][r]; }
    void  set(int f, int r, Piece p) { sq[f][r] = p; }

    Board copy() {
        Board b = new Board();
        for (int f=0; f<8; f++) for (int r=0; r<8; r++) b.sq[f][r] = sq[f][r];
        return b;
    }

    static Board initialSetup() {
        Board b = new Board();
        // pawns
        for (int f=0; f<8; f++) {
            b.set(f, 1, new Piece(Color.WHITE, Kind.PAWN)); // rank 2
            b.set(f, 6, new Piece(Color.BLACK, Kind.PAWN)); // rank 7
        }
        // white back rank (rank 1)
        b.set(0,0,new Piece(Color.WHITE,Kind.ROOK));
        b.set(1,0,new Piece(Color.WHITE,Kind.KNIGHT));
        b.set(2,0,new Piece(Color.WHITE,Kind.BISHOP));
        b.set(3,0,new Piece(Color.WHITE,Kind.QUEEN));
        b.set(4,0,new Piece(Color.WHITE,Kind.KING));
        b.set(5,0,new Piece(Color.WHITE,Kind.BISHOP));
        b.set(6,0,new Piece(Color.WHITE,Kind.KNIGHT));
        b.set(7,0,new Piece(Color.WHITE,Kind.ROOK));
        // black back rank (rank 8)
        b.set(0,7,new Piece(Color.BLACK,Kind.ROOK));
        b.set(1,7,new Piece(Color.BLACK,Kind.KNIGHT));
        b.set(2,7,new Piece(Color.BLACK,Kind.BISHOP));
        b.set(3,7,new Piece(Color.BLACK,Kind.QUEEN));
        b.set(4,7,new Piece(Color.BLACK,Kind.KING));
        b.set(5,7,new Piece(Color.BLACK,Kind.BISHOP));
        b.set(6,7,new Piece(Color.BLACK,Kind.KNIGHT));
        b.set(7,7,new Piece(Color.BLACK,Kind.ROOK));
        return b;
    }

    ArrayList<ReturnPiece> toReturnPieces() {
        ArrayList<ReturnPiece> out = new ArrayList<>();
        for (int f=0; f<8; f++) for (int r=0; r<8; r++) {
            Piece p = sq[f][r];
            if (p == null) continue;
            ReturnPiece rp = new ReturnPiece();
            rp.pieceFile = ReturnPiece.PieceFile.values()[f];
            rp.pieceRank = r + 1;
            if (p.color == Color.WHITE) {
                rp.pieceType = switch (p.kind) {
                    case PAWN -> ReturnPiece.PieceType.WP;
                    case ROOK -> ReturnPiece.PieceType.WR;
                    case KNIGHT -> ReturnPiece.PieceType.WN;
                    case BISHOP -> ReturnPiece.PieceType.WB;
                    case QUEEN -> ReturnPiece.PieceType.WQ;
                    case KING -> ReturnPiece.PieceType.WK;
                };
            } else {
                rp.pieceType = switch (p.kind) {
                    case PAWN -> ReturnPiece.PieceType.BP;
                    case ROOK -> ReturnPiece.PieceType.BR;
                    case KNIGHT -> ReturnPiece.PieceType.BN;
                    case BISHOP -> ReturnPiece.PieceType.BB;
                    case QUEEN -> ReturnPiece.PieceType.BQ;
                    case KING -> ReturnPiece.PieceType.BK;
                };
            }
            out.add(rp);
        }
        return out;
    }
}
