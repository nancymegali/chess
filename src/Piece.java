

enum Color { WHITE, BLACK }
enum Kind  { KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN }

final class Piece {
    final Color color;
    final Kind  kind;
    Piece(Color c, Kind k) { this.color = c; this.kind = k; }
}
