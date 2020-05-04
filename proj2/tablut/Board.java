package tablut;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.Formatter;

import static tablut.Move.mv;
import static tablut.Piece.BLACK;
import static tablut.Piece.WHITE;
import static tablut.Piece.EMPTY;
import static tablut.Piece.KING;
import static tablut.Square.SQUARE_LIST;
import static tablut.Square.sq;


/**
 * The state of a Tablut Game.
 *
 * @author Laiming Huang
 */
class Board {

    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 9;
    /**
     * The throne (or castle) square and its four surrounding squares..
     */
    static final Square THRONE = sq(4, 4),
        NTHRONE = sq(4, 5),
        STHRONE = sq(4, 3),
        WTHRONE = sq(3, 4),
        ETHRONE = sq(5, 4);
    /**
     * Initial positions of attackers.
     */
    static final Square[] INITIAL_ATTACKERS = {
        sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
        sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
        sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
        sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };
    /**
     * Initial positions of defenders of the king.
     */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };
    /**
     * The array of directions.
     */
    private static final int[][] DIR = {
        {0, 1}, {1, 0}, {0, -1}, {-1, 0}
    };
    /**
     * Piece whose turn it is (WHITE or BLACK).
     */
    private Piece _turn;
    /**
     * Cached value of winner on this board, or EMPTY if it has not been
     * computed.
     */
    private Piece _winner;
    /**
     * Number of (still undone) moves since initial position.
     */
    private int _moveCount;
    /**
     * True when current board is a repeated position (ending the game).
     */
    private boolean _repeated;
    /**
     * The move limit.
     */
    private int _moveLimit;
    /**
     * The board.
     */
    private Piece[][] myBoard;
    /**
     * The list of hashcodes of histories of board.
     */
    private HashSet<String> myHashes;
    /**
     * Number of pieces captured by current move.
     */
    private int numCaptured = 0;
    /**
     * HashMap.
     */
    private HashMap<Piece, HashSet<Square>> pieceSquareMap;
    /**
     * List used to store square for latest move.
     */
    private ArrayList<Board> myHistory;

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        if (model == this) {
            return;
        }

        myBoard = new Piece[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                myBoard[i][j] = model.myBoard[i][j];
            }
        }

        pieceSquareMap = new HashMap<>();
        pieceSquareMap.put(BLACK, new HashSet<>());
        pieceSquareMap.put(WHITE, new HashSet<>());
        pieceSquareMap.put(KING, new HashSet<>());

        _turn = model._turn;
        _repeated = model._repeated;
        _moveCount = model._moveCount;
        _moveLimit = model._moveLimit;
        _winner = model._winner;
        myHistory = model.myHistory;
        myHashes = model.myHashes;

        for (Square sq : model.pieceSquareMap.get(BLACK)) {
            pieceSquareMap.get(BLACK).add(sq);
        }
        for (Square sq : model.pieceSquareMap.get(WHITE)) {
            pieceSquareMap.get(WHITE).add(sq);
        }

        Square kingPos = model.kingPosition();
        pieceSquareMap.get(KING).add(kingPos);
    }

    /**
     * Clears the board to the initial position.
     */
    void init() {

        myBoard = new Piece[SIZE][SIZE];

        pieceSquareMap = new HashMap<>();
        pieceSquareMap.put(BLACK, new HashSet<>());
        pieceSquareMap.put(WHITE, new HashSet<>());
        pieceSquareMap.put(KING, new HashSet<>());

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Square tempSquare = sq(j, i);
                List<Square> ia = Arrays.asList(INITIAL_ATTACKERS);
                List<Square> id = Arrays.asList(INITIAL_DEFENDERS);
                if (ia.contains(tempSquare)) {
                    myBoard[i][j] = BLACK;
                    pieceSquareMap.get(BLACK).add(tempSquare);
                } else if (id.contains(tempSquare)) {
                    myBoard[i][j] = WHITE;
                    pieceSquareMap.get(WHITE).add(tempSquare);
                } else if (tempSquare.equals(THRONE)) {
                    myBoard[i][j] = KING;
                    pieceSquareMap.get(KING).add(tempSquare);
                } else {
                    myBoard[i][j] = EMPTY;
                }
            }
        }

        _turn = BLACK;
        _winner = null;
        _moveCount = 0;
        myHashes = new HashSet<String>();
        _repeated = false;
        _moveLimit = Integer.MAX_VALUE;
        numCaptured = 0;
        myHistory = new ArrayList<>();
    }

    /**
     * Set the move limit to LIM.  It is an error if 2*LIM <= moveCount().
     * @param n The move limit.
     */
    void setMoveLimit(int n) {
        if (2 * n > moveCount()) {
            _moveLimit = n;
        } else {
            throw new IllegalArgumentException("Invalid move limit!");
        }
    }

    /**
     * Return a Piece representing whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the winner in the current position, or null if there is no winner
     * yet.
     */
    Piece winner() {
        return _winner;
    }

    /**
     * Returns true iff this is a win due to a repeated position.
     */
    boolean repeatedPosition() {
        return _repeated;
    }

    /**
     * Return the number of moves since the initial position that have not been
     * undone.
     */
    int moveCount() {
        return _moveCount;
    }

    /**
     * Return location of the king.
     */
    Square kingPosition() {
        if (!Arrays.asList(pieceSquareMap.get(KING).toArray()).isEmpty()
            && Arrays.asList(pieceSquareMap.get(KING).toArray()) != null) {
            return (Square) Arrays.asList
                (pieceSquareMap.get(KING).toArray()).get(0);
        } else {
            return null;
        }
    }

    /**
     * Return the contents the square at S.
     */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW <= 9.
     */
    final Piece get(int col, int row) {
        return myBoard[row][col];
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /**
     * Set square S to P.
     */
    final void put(Piece p, Square s) {
        if (get(s) != EMPTY) {
            if (p.equals(EMPTY)) {
                pieceSquareMap.get(get(s)).remove(s);
            } else {
                pieceSquareMap.get(p).add(s);
            }
        }
        myBoard[s.row()][s.col()] = p;
        if (!p.equals(EMPTY)) {
            pieceSquareMap.get(p).add(s);
        }
    }

    /**
     * Set square S to P and record for undoing.
     */
    final void revPut(Piece p, Square s) {
        myBoard[s.row()][s.col()] = p;
    }

    /**
     * Set square COL ROW to P.
     */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /**
     * Return true iff FROM - TO is an unblocked rook move on the current
     * board.  For this to be true, FROM-TO must be a rook move and the
     * squares along it, other than FROM, must be empty.
     */

    boolean isUnblockedMove(Square from, Square to) {

        if (!from.isRookMove(to) || to.equals(from)) {
            return false;
        } else {
            if (from.row() == to.row()) {
                for (int i = Math.min(from.col(), to.col()) + 1;
                     i < Math.max(from.col(), to.col()); i++) {
                    if (!get(i, from.row()).equals(EMPTY)) {
                        return false;
                    }
                }
                if (get(to).equals(EMPTY)) {
                    return true;
                }
            } else if (from.col() == to.col()) {
                for (int j = Math.min(from.row(), to.row()) + 1;
                     j < Math.max(from.row(), to.row()); j++) {
                    if (!get(from.col(), j).equals(EMPTY)) {
                        return false;
                    }
                }
                if (get(to).equals(EMPTY)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    boolean isLegal(Square from) {
        return get(from).side() == _turn;
    }

    /**
     * Return true iff FROM-TO is a valid move.
     */
    boolean isLegal(Square from, Square to) {
        if (_winner != null) {
            return false;
        }
        if (isLegal(from)) {
            if (get(from) != EMPTY) {
                if (isUnblockedMove(from, to)) {
                    if (to.equals(THRONE)) {
                        if (get(from).equals(KING)) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Return true iff FROM-TO is a valid move.
     */
    boolean weakIsLegal(Square from, Square to) {
        if (!get(from).equals(EMPTY)) {
            if (isUnblockedMove(from, to)) {
                if (to.equals(THRONE)) {
                    if (get(from).equals(KING)) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /**
     * Move FROM-TO, assuming this is a legal move.
     */
    void makeMove(Square from, Square to) {
        if (!hasMove(_turn)) {
            _winner = _turn.opponent();
        } else if (isLegal(from, to)) {
            _moveCount++;
            if (2 * _moveLimit == moveCount()) {
                _winner = _turn;
            }
            numCaptured = 0;

            Piece temp = get(from);
            Board currDuplicate = new Board();
            currDuplicate.copy(this);

            myHistory.add(currDuplicate);
            myHashes.add(currDuplicate.encodedBoard());

            put(get(from), to);
            pieceSquareMap.get(temp).remove(from);

            put(EMPTY, from);
            pieceSquareMap.get(temp).add(to);

            if (kingPosition() != null) {
                if (kingPosition().isEdge()) {
                    _winner = WHITE;
                }
            }

            doCapture(to);

            _turn = _turn.opponent();

            if (myHashes.contains(this.encodedBoard())) {
                _repeated = true;
                _winner = _turn;
            }

        }
    }

    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /**
     * Check if a piece is hostile.
     * @param sq sq
     * @param other other
     * @return Hostile
     */
    boolean isHostile(Square sq, Square other) {
        if (sq == THRONE && get(sq) == KING
            && get(other).side() == WHITE
            && ((get(NTHRONE).side().equals(BLACK)
            && get(ETHRONE).side().equals(BLACK)
            && get(STHRONE).side().equals(BLACK))
            || (get(ETHRONE).side().equals(BLACK)
            && get(STHRONE).side().equals(BLACK)
            && get(WTHRONE).side().equals(BLACK))
            || (get(STHRONE).side().equals(BLACK)
            && get(WTHRONE).side().equals(BLACK)
            && get(NTHRONE).side().equals(BLACK))
            || (get(WTHRONE).side().equals(BLACK)
            && get(NTHRONE).side().equals(BLACK)
            && get(ETHRONE).side().equals(BLACK)))) {
            return true;
        }

        if ((get(sq).side().equals(_turn)
            && get(other).side().equals(_turn.opponent()))
            || (sq == THRONE && get(sq) == EMPTY)
            || (sq == THRONE && get(sq) == KING
            && get(other).side() == WHITE
            && ((get(NTHRONE).side().equals(BLACK)
            && get(ETHRONE).side().equals(BLACK)
            && get(STHRONE).side().equals(BLACK))
            || (get(ETHRONE).side().equals(BLACK)
            && get(STHRONE).side().equals(BLACK)
            && get(WTHRONE).side().equals(BLACK))
            || (get(STHRONE).side().equals(BLACK)
            && get(WTHRONE).side().equals(BLACK)
            && get(NTHRONE).side().equals(BLACK))
            || (get(WTHRONE).side().equals(BLACK)
            && get(NTHRONE).side().equals(BLACK)
            && get(ETHRONE).side().equals(BLACK))))) {
            return true;
        }
        return false;
    }

    /**
     * Do the capture work.
     * @param to to sq.
     */
    private void doCapture(Square to) {
        if (to.col() + 2 < SIZE && numCaptured < 3) {
            capture(to, sq(to.col() + 2, to.row()));
        }
        if (to.col() > 1 && numCaptured < 3) {
            capture(to, sq(to.col() - 2, to.row()));
        }
        if (to.row() + 2 < SIZE && numCaptured < 3) {
            capture(to, sq(to.col(), to.row() + 2));
        }
        if (to.row() > 1 && numCaptured < 3) {
            capture(to, sq(to.col(), to.row() - 2));
        }
    }

    /**
     * Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     * SQ0 and the necessary conditions are satisfied.
     */
    private void capture(Square sq0, Square sq2) {
        assert isLegal(sq0);
        if (sq0.row() == sq2.row()
            && Math.abs(sq0.col() - sq2.col()) == 2) {
            if (get((sq0.col() + sq2.col()) / 2,
                sq0.row()).equals(KING)
                && (sq((sq0.col() + sq2.col()) / 2,
                sq0.row()).equals(THRONE)
                || sq((sq0.col() + sq2.col()) / 2,
                sq0.row()).equals(NTHRONE)
                || sq((sq0.col() + sq2.col()) / 2,
                sq0.row()).equals(ETHRONE)
                || sq((sq0.col() + sq2.col()) / 2,
                sq0.row()).equals(WTHRONE)
                || sq((sq0.col() + sq2.col()) / 2,
                sq0.row()).equals(STHRONE)
                )) {
                if (isHostile(sq(1 + (sq0.col() + sq2.col()) / 2,
                    sq0.row()), sq((sq0.col() + sq2.col()) / 2, sq0.row()))
                    && isHostile(sq((sq0.col() + sq2.col()) / 2,
                    sq0.row() - 1), sq((sq0.col() + sq2.col()) / 2,
                    sq0.row()))
                    && isHostile(sq(((sq0.col() + sq2.col()) / 2) - 1,
                    sq0.row()),
                    sq((sq0.col() + sq2.col()) / 2, sq0.row()))
                    && isHostile(sq((sq0.col() + sq2.col()) / 2,
                    sq0.row() + 1),
                    sq((sq0.col() + sq2.col()) / 2, sq0.row()))) {
                    put(EMPTY, sq((sq0.col() + sq2.col()) / 2, sq0.row()));

                    _winner = BLACK;
                    numCaptured++;
                }
            } else if (isHostile(sq0,
                sq((sq0.col() + sq2.col()) / 2, sq0.row()))
                && isHostile(sq2, sq((sq0.col() + sq2.col()) / 2,
                sq0.row()))) {
                if (get((sq0.col() + sq2.col()) / 2,
                    sq0.row()).equals(KING)) {
                    put(EMPTY, sq((sq0.col() + sq2.col()) / 2, sq0.row()));
                    _winner = BLACK;
                } else {
                    put(EMPTY, sq((sq0.col() + sq2.col()) / 2, sq0.row()));
                }
                numCaptured++;
            }
        } else if (sq0.col() == sq2.col()
            && Math.abs(sq0.row() - sq2.row()) == 2) {
            captureSec(sq0, sq2);
        }
    }

    /**
     * Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     * SQ0 and the necessary conditions are satisfied.
     */
    private void captureSec(Square sq0, Square sq2) {
        if (get(sq0.col(), (sq0.row() + sq2.row()) / 2).equals(KING)
            && (sq(sq0.col(), (sq0.row() + sq2.row()) / 2).equals(THRONE)
            || sq(sq0.col(), (sq0.row() + sq2.row()) / 2).equals(NTHRONE)
            || sq(sq0.col(), (sq0.row() + sq2.row()) / 2).equals(ETHRONE)
            || sq(sq0.col(), (sq0.row() + sq2.row()) / 2).equals(WTHRONE)
            || sq(sq0.col(), (sq0.row() + sq2.row()) / 2).equals(STHRONE)
            )) {
            if (isHostile(sq(1 + sq0.col(), (sq0.row() + sq2.row()) / 2),
                sq(sq0.col(), (sq0.row() + sq2.row()) / 2))
                && isHostile(sq(sq0.col(), (sq0.row() + sq2.row()) / 2 - 1),
                sq(sq0.col(), (sq0.row() + sq2.row()) / 2))
                && isHostile(sq(sq0.col() - 1, (sq0.row() + sq2.row()) / 2),
                sq(sq0.col(), (sq0.row() + sq2.row()) / 2))
                && isHostile(sq(sq0.col(), 1 + (sq0.row() + sq2.row()) / 2),
                sq(sq0.col(), (sq0.row() + sq2.row()) / 2))) {
                put(EMPTY, sq(sq0.col(), (sq0.row() + sq2.row()) / 2));
                _winner = BLACK;
                numCaptured++;
            }
        } else if (isHostile(sq0, sq(sq0.col(), (sq0.row() + sq2.row()) / 2))
            && isHostile(sq2, sq(sq0.col(), (sq0.row() + sq2.row()) / 2))) {
            if (get(sq0.col(), (sq0.row() + sq2.row()) / 2).equals(KING)) {
                put(EMPTY, sq(sq0.col(), (sq0.row() + sq2.row()) / 2));
                _winner = BLACK;
            } else {
                put(EMPTY, sq(sq0.col(), (sq0.row() + sq2.row()) / 2));
            }
            numCaptured++;
        }
    }

    /**
     * Undo one move.  Has no effect on the initial board.
     */
    void undo() {
        if (_moveCount > 0 && (!myHistory.isEmpty())) {
            Board prev = myHistory.get(myHistory.size() - 1);
            this.copy(prev);
            if (myHistory.size() != 0) {
                myHistory.remove(myHistory.size() - 1);
                _moveCount--;
            }
            _repeated = false;
            myHashes.remove(prev.encodedBoard());
        }
    }

    /**
     * Clear the undo stack and board-position counts. Does not modify the
     * current position or win status.
     */
    void clearUndo() {
        myHistory.clear();
        _moveCount = 0;
    }

    /**
     * Return a new mutable list of all legal moves on the current board for
     * SIDE (ignoring whose turn it is at the moment).
     */
    List<Move> legalMoves(Piece side) {
        List<Move> res = new ArrayList<Move>();

        if (side.equals(WHITE)) {
            Square kingPos = kingPosition();
            if (kingPos != null) {
                for (int i = 0; i < DIR.length; i++) {
                    int[] curr = DIR[i];
                    int col = kingPos.col() + curr[0];
                    int row = kingPos.row() + curr[1];
                    while (Square.exists(col, row)) {
                        Square sqq = sq(col, row);
                        if (weakIsLegal(kingPos, sqq)) {
                            res.add(mv(kingPos, sqq));
                        }
                        col += curr[0];
                        row += curr[1];
                    }
                }
            }
        }

        HashSet<Square> allMySquares = pieceLocations(side);
        if (allMySquares != null) {
            for (Square sq : allMySquares) {
                for (int i = 0; i < DIR.length; i++) {
                    int[] curr = DIR[i];
                    int col = sq.col() + curr[0];
                    int row = sq.row() + curr[1];
                    while (Square.exists(col, row)) {
                        Square sqq = sq(col, row);
                        if (weakIsLegal(sq, sqq)) {
                            res.add(mv(sq, sqq));
                        }
                        col += curr[0];
                        row += curr[1];
                    }
                }
            }
        }
        return res;
    }

    /**
     * Return true iff SIDE has a legal move.
     */
    boolean hasMove(Piece side) {
        return legalMoves(side) != null;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /**
     * Return a text representation of this Board.  If COORDINATES, then row
     * and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /**
     * Return the locations of all pieces on SIDE.
     */
    HashSet<Square> pieceLocations(Piece side) {
        assert side != null;
        return pieceSquareMap.get(side);
    }

    /**
     * Return the contents of _board in the order of SQUARE_LIST as a sequence
     * of characters: the toString values of the current turn and Pieces.
     */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }
}










