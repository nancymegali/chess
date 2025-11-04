package chess;

final class Engine {

    static EngineResult apply(Position pos, ParsedInput parsed) {
        Move mv = parsed.move;
        Board b = pos.board;

        Piece p = b.get(mv.fromFile, mv.fromRank);
        if (p == null) return EngineResult.illegal(pos);

        // turn check
        if ((p.color == Color.WHITE && pos.toMove != Chess.Player.white) ||
            (p.color == Color.BLACK && pos.toMove != Chess.Player.black)) {
            return EngineResult.illegal(pos);
        }

        // friendly block
        Piece dest = b.get(mv.toFile, mv.toRank);
        if (dest != null && dest.color == p.color) return EngineResult.illegal(pos);

        // piece-specific legality
        boolean legal = switch (p.kind) {
            case PAWN   -> legalPawnBasic(pos, mv, p);
            case KNIGHT -> legalKnight(mv);
            case BISHOP -> legalBishop(b, mv);
            case ROOK   -> legalRook(b, mv);
            case QUEEN  -> legalQueen(b, mv);
            case KING   -> legalKing(mv);
        };
        if (!legal) return EngineResult.illegal(pos);

        // tentatively execute on a copy
        Position next = pos.copy();
        next.enPassantFile = null; // placeholders until we add full ep logic
        next.board.set(mv.fromFile, mv.fromRank, null);
        next.board.set(mv.toFile, mv.toRank, p);

        // promotion not handled yet (we'll add next); ignore mv.promotion for now

        // switch turn
        Chess.Player mover = pos.toMove;
        next.toMove = (mover == Chess.Player.white) ? Chess.Player.black : Chess.Player.white;

        // === LEGALITY: cannot leave/move into self-check ===
        Color moverColor = (mover == Chess.Player.white) ? Color.WHITE : Color.BLACK;
        if (isKingInCheck(next.board, moverColor)) {
            return EngineResult.illegal(pos);
        }

        // draw? (always accepted after executing the move)
        if (mv.drawOffer) return EngineResult.okWithDraw(next);

        // optional: we could set CHECK if we just checked the opponent
        Color oppColor = (moverColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        ReturnPlay.Message msg = isKingInCheck(next.board, oppColor) ? ReturnPlay.Message.CHECK : null;

        return EngineResult.ok(next, msg);
    }

    // ---------------- movement rules ----------------

    private static boolean legalKnight(Move m) {
        int df = Math.abs(m.toFile - m.fromFile);
        int dr = Math.abs(m.toRank - m.fromRank);
        return (df == 1 && dr == 2) || (df == 2 && dr == 1);
    }

    private static boolean legalKing(Move m) {
        int df = Math.abs(m.toFile - m.fromFile);
        int dr = Math.abs(m.toRank - m.fromRank);
        return Math.max(df, dr) == 1; // no castling yet
    }

    private static boolean legalBishop(Board b, Move m) {
        int df = m.toFile - m.fromFile;
        int dr = m.toRank - m.fromRank;
        if (Math.abs(df) != Math.abs(dr)) return false;
        int stepF = Integer.signum(df);
        int stepR = Integer.signum(dr);
        return pathClear(b, m.fromFile, m.fromRank, m.toFile, m.toRank, stepF, stepR);
    }

    private static boolean legalRook(Board b, Move m) {
        int df = m.toFile - m.fromFile;
        int dr = m.toRank - m.fromRank;
        if (!((df == 0) ^ (dr == 0))) return false; // must be straight
        int stepF = Integer.signum(df);
        int stepR = Integer.signum(dr);
        return pathClear(b, m.fromFile, m.fromRank, m.toFile, m.toRank, stepF, stepR);
    }

    private static boolean legalQueen(Board b, Move m) {
        int df = Math.abs(m.toFile - m.fromFile);
        int dr = Math.abs(m.toRank - m.fromRank);
        if (!(df == dr || df == 0 || dr == 0)) return false;
        int stepF = Integer.signum(m.toFile - m.fromFile);
        int stepR = Integer.signum(m.toRank - m.fromRank);
        return pathClear(b, m.fromFile, m.fromRank, m.toFile, m.toRank, stepF, stepR);
    }

    private static boolean pathClear(Board b, int ff, int fr, int tf, int tr, int stepF, int stepR) {
        int f = ff + stepF, r = fr + stepR;
        while (f != tf || r != tr) {
            if (b.get(f, r) != null) return false;
            f += stepF; r += stepR;
        }
        return true;
    }

    private static boolean legalPawnBasic(Position pos, Move m, Piece p) {
        int dir = (p.color == Color.WHITE) ? +1 : -1;
        int startRank = (p.color == Color.WHITE) ? 1 : 6;

        int df = m.toFile - m.fromFile;
        int dr = m.toRank - m.fromRank;

        // single forward
        if (df == 0 && dr == dir) {
            return pos.board.get(m.toFile, m.toRank) == null;
        }
        // double from start (both empty)
        if (df == 0 && dr == 2 * dir && m.fromRank == startRank) {
            if (pos.board.get(m.toFile, m.fromRank + dir) != null) return false;
            return pos.board.get(m.toFile, m.toRank) == null;
        }
        // diagonal capture
        if (Math.abs(df) == 1 && dr == dir) {
            Piece dest = pos.board.get(m.toFile, m.toRank);
            return dest != null && dest.color != p.color;
        }
        return false;
    }

    // ---------------- check detection ----------------

    private static boolean isKingInCheck(Board b, Color color) {
        // find king
        int kf = -1, kr = -1;
        for (int f = 0; f < 8; f++) {
            for (int r = 0; r < 8; r++) {
                Piece p = b.get(f, r);
                if (p != null && p.color == color && p.kind == Kind.KING) {
                    kf = f; kr = r; break;
                }
            }
            if (kf != -1) break;
        }
        if (kf == -1) return true; // no king found => treat as in check/illegal

        Color enemy = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
        return isSquareAttackedBy(b, kf, kr, enemy);
    }

    private static boolean isSquareAttackedBy(Board b, int f, int r, Color attacker) {
        // pawns
        int pawnDir = (attacker == Color.WHITE) ? +1 : -1;
        int pr = r - pawnDir; // squares from which a pawn would attack (reverse)
        if (Square.onBoard(f - 1, pr)) {
            Piece p = b.get(f - 1, pr);
            if (p != null && p.color == attacker && p.kind == Kind.PAWN) return true;
        }
        if (Square.onBoard(f + 1, pr)) {
            Piece p = b.get(f + 1, pr);
            if (p != null && p.color == attacker && p.kind == Kind.PAWN) return true;
        }

        // knights
        int[][] K = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};
        for (int[] d : K) {
            int nf = f + d[0], nr = r + d[1];
            if (Square.onBoard(nf, nr)) {
                Piece p = b.get(nf, nr);
                if (p != null && p.color == attacker && p.kind == Kind.KNIGHT) return true;
            }
        }

        // king (adjacent)
        for (int df = -1; df <= 1; df++) for (int dr = -1; dr <= 1; dr++) {
            if (df == 0 && dr == 0) continue;
            int nf = f + df, nr = r + dr;
            if (Square.onBoard(nf, nr)) {
                Piece p = b.get(nf, nr);
                if (p != null && p.color == attacker && p.kind == Kind.KING) return true;
            }
        }

        // sliders: rook/queen (orthogonal)
        int[][] RAYS_ORTHO = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : RAYS_ORTHO) {
            if (rayHits(b, f, r, d[0], d[1], attacker, Kind.ROOK, Kind.QUEEN)) return true;
        }

        // sliders: bishop/queen (diagonal)
        int[][] RAYS_DIAG = {{1,1},{1,-1},{-1,1},{-1,-1}};
        for (int[] d : RAYS_DIAG) {
            if (rayHits(b, f, r, d[0], d[1], attacker, Kind.BISHOP, Kind.QUEEN)) return true;
        }

        return false;
    }

    private static boolean rayHits(Board b, int f, int r, int df, int dr, Color attacker, Kind k1, Kind k2) {
        int nf = f + df, nr = r + dr;
        while (Square.onBoard(nf, nr)) {
            Piece x = b.get(nf, nr);
            if (x != null) {
                return x.color == attacker && (x.kind == k1 || x.kind == k2);
            }
            nf += df; nr += dr;
        }
        return false;
    }
}
