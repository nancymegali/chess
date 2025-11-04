package chess;

final class ParsedInput {
    final boolean ok;
    final Move move;

    private ParsedInput(boolean ok, Move move) { this.ok = ok; this.move = move; }
    static ParsedInput ok(Move m) { return new ParsedInput(true, m); }
    static ParsedInput bad()      { return new ParsedInput(false, null); }
}
