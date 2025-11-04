package chess;

final class Move {
    final int fromFile, fromRank;
    final int toFile, toRank;
    final Kind promotion;   // null unless specified
    final boolean drawOffer;

    Move(int ff, int fr, int tf, int tr, Kind promo, boolean drawOffer) {
        this.fromFile = ff; this.fromRank = fr;
        this.toFile = tf;   this.toRank = tr;
        this.promotion = promo;
        this.drawOffer = drawOffer;
    }
}
