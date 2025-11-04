package chess;

final class Square {
    final int file; // 0..7 (a..h)
    final int rank; // 0..7 (1..8)

    Square(int f, int r) { this.file = f; this.rank = r; }

    static int fileCharToIndex(char f) { return f - 'a'; }
    static int rankCharToIndex(char r) { return r - '1'; }
    static boolean onBoard(int f, int r) { return f>=0 && f<8 && r>=0 && r<8; }
}
