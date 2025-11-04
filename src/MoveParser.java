

final class MoveParser {
    // Accepts: "e2 e4", "g7 g8 N", optional trailing " draw?"
    static ParsedInput parseInput(String raw) {
        String s = raw.trim();
        if (s.isEmpty()) return ParsedInput.bad();

        boolean draw = false;
        if (s.endsWith(" draw?")) { draw = true; s = s.substring(0, s.length()-6).trim(); }

        String[] parts = s.split("\\s+");
        if (parts.length < 2 || parts.length > 3) return ParsedInput.bad();

        String a = parts[0], b = parts[1];
        if (a.length()!=2 || b.length()!=2) return ParsedInput.bad();

        int ff = Square.fileCharToIndex(a.charAt(0));
        int fr = Square.rankCharToIndex(a.charAt(1));
        int tf = Square.fileCharToIndex(b.charAt(0));
        int tr = Square.rankCharToIndex(b.charAt(1));
        if (!Square.onBoard(ff,fr) || !Square.onBoard(tf,tr)) return ParsedInput.bad();

        Kind promo = null;
        if (parts.length == 3) {
            promo = switch (parts[2]) {
                case "Q" -> Kind.QUEEN;
                case "R" -> Kind.ROOK;
                case "B" -> Kind.BISHOP;
                case "N" -> Kind.KNIGHT;
                default  -> null;
            };
            if (promo == null) return ParsedInput.bad();
        }
        return ParsedInput.ok(new Move(ff, fr, tf, tr, promo, draw));
    }
}
