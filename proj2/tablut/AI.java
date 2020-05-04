package tablut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static tablut.Piece.BLACK;
import static tablut.Piece.WHITE;

/**
 * A Player that automatically generates moves.
 *
 * @author Laiming Huang
 */
class AI extends Player {

    /**
     * A position-score magnitude indicating a win (for white if positive,
     * black if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /**
     * A position-score magnitude indicating a forced win in a subsequent
     * move.  This differs from WINNING_VALUE to avoid putting off wins.
     */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /**
     * A position-score magnitude indicating a forced win in a subsequent
     * move.  This differs from WINNING_VALUE to avoid putting off wins.
     */
    private static final int MIN = 50;
    /**
     * A position-score magnitude indicating a forced win in a subsequent
     * move.  This differs from WINNING_VALUE to avoid putting off wins.
     */
    private static final int FIRSTGRADE = 40;
    /**
     * A position-score magnitude indicating a forced win in a subsequent
     * move.  This differs from WINNING_VALUE to avoid putting off wins.
     */
    private static final int SECONDGRADE = 70;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;
    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;
    /** 2 . */
    private static final int TWO = 2;
    /** 3 .*/
    private static final int THREE = 3;
    /** 1.5  .*/
    private static final double ONEPOINTFIVE = 1.5;
    /**  6 .*/
    private static final int SIX = 6;
    /** 12 .*/
    private static final int TWELVE = 12;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private static int maxDepth(Board board) {
        return 4;
    }

    /**
     * Return the number of corner pieces.
     * @param board The board.
     */

    public static double numCornerPieces(Board board) {
        double value = 0.0;

        if (board.get(0, 8) == Piece.BLACK) {
            value += 0.25;
        }

        if (board.get(8, 8) == Piece.BLACK) {
            value += 0.25;
        }

        if (board.get(0, 0) == Piece.BLACK) {
            value += 0.25;
        }

        if (board.get(8, 0) == Piece.BLACK) {
            value += 0.25;
        }

        return value;
    }

    /**
     * The number of king moves to all the corners.
     * @param board The board
     * @return Score
     */
    public static double kingEndeavors(Board board) {
        Square kingPosition = board.kingPosition();

        List<Move> kingMoves = board.legalMoves(board.get(kingPosition));

        double moveDistance = 0.0;
        if (!kingMoves.isEmpty()) {

            int[] distances = new int[4];

            int numCorners = 0;
            for (Square corner : Square.allCorners()) {
                distances[numCorners] = minimumCornerMove
                    (board, corner, 1, kingPosition);
                numCorners++;
            }

            for (int i = 0; i < distances.length; i++) {
                switch (distances[i]) {
                case 1:
                    moveDistance += 15;
                    break;
                case 2:
                    moveDistance += 1;
                    break;
                default:
                    moveDistance += 0;
                    break;
                }
            }
        }
        return moveDistance;
    }

    /**
     * The min number of moves for the king to reach a given corner.
     * @param boardState The board.
     * @param corner The corner.
     * @param  numMove The move number.
     * @param kingPosition The postion of King.
     * @return minimumCornerMove.
     */
    public static int minimumCornerMove(Board boardState, Square corner,
                                        int numMove, Square kingPosition) {
        if (numMove == 3 || Square.isCorner(kingPosition)) {
            return numMove;
        }

        List<Move> kingMoves = (kingPosition == null) ? new ArrayList<Move>()
            : boardState.legalMoves(boardState.get(kingPosition));

        int[] moveCounts = new int[kingMoves.size()];

        int moveScore = 0;
        for (Move move : kingMoves) {
            if (move.from().distance(move.from(), corner)
                > move.to().distance(move.to(), corner)) {
                moveCounts[moveScore] =
                    minimumCornerMove(boardState, corner,
                        numMove + 1, move.to());
                moveScore++;
            }
        }

        int min = MIN;
        for (int i = 0; i < moveCounts.length; i++) {
            int current = moveCounts[i];
            if (current != 0 && current < min) {
                min = current;
            }
        }
        return min;
    }


    @Override
    Player create(Piece piece, Controller controller) {
        return new tablut.AI(piece, controller);
    }

    @Override
    String myMove() {
        Move mv = findMove();
        _controller.reportMove(mv);
        return mv.toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     * _lastFoundMove = null;
     */
    private Move findMove() {
        Board board = new Board(board());

        Board searchBoard = new Board();
        searchBoard.copy(board);

        int sense;
        if (searchBoard.turn().equals(Piece.WHITE)) {
            sense = 1;
        } else {
            sense = -1;
        }

        findMove(searchBoard, maxDepth(searchBoard),
            true, sense, -INFTY, INFTY);

        return _lastFoundMove;
    }

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private double findMove(Board board, int depth, boolean saveMove,
                            int sense, double alpha, double beta) {
        double currBest = Integer.MIN_VALUE;
        if (sense == -1) {
            currBest = Integer.MAX_VALUE;
        }
        if (board.winner() != null || depth == 0) {
            return staticScore(board);
        }
        List<Move> moves = board.legalMoves(board.turn());
        Iterator<Move> itr = moves.iterator();
        Move bestSoFar = null;
        Move nextMove;
        while (itr.hasNext()) {
            nextMove = itr.next();
            board.makeMove(nextMove);
            double score = findMove(board, depth - 1,
                false, -sense, alpha, beta);
            board.undo();
            if (sense < 0 && score < currBest) {
                bestSoFar = nextMove;
                currBest = score;
                beta = min(beta, currBest);
                if (beta <= alpha) {
                    break;
                }
            } else if (sense > 0 && score > currBest) {
                bestSoFar = nextMove;
                currBest = score;
                alpha = max(alpha, currBest);
                if (beta <= alpha) {
                    break;
                }
            }

        }
        if (sense == 1) {
            if (currBest == -WINNING_VALUE) {
                currBest = -WILL_WIN_VALUE;
            }
        }
        if (sense == -1) {
            if (currBest == WINNING_VALUE) {
                currBest = WILL_WIN_VALUE;
            }
        }
        if (saveMove) {
            _lastFoundMove = bestSoFar;
        }
        return currBest;
    }

    /**
     * Return a heuristic value for BOARD.
     */
    private double staticScore(Board board) {
        int numWhitePiece = board.pieceLocations(WHITE.side()).size();
        int numBlackPiece = board.pieceLocations(BLACK.side()).size();
        int moveCount = board.moveCount();
        double resScore = 0.0;
        if (board.winner() == WHITE.side()) {
            return WINNING_VALUE;
        } else if (board.winner() == BLACK.side()) {
            return -WINNING_VALUE;
        }
        List<Boolean> numOfClearPathToEdge = new ArrayList<Boolean>();
        if (board.weakIsLegal(board.kingPosition(),
            Square.sq(board.kingPosition().col(), 0))) {
            numOfClearPathToEdge.add(Boolean.TRUE);
        }
        if (board.weakIsLegal(board.kingPosition(),
            Square.sq(board.kingPosition().col(), 8))) {
            numOfClearPathToEdge.add(Boolean.TRUE);
        }
        if (board.weakIsLegal(board.kingPosition(),
            Square.sq(0, board.kingPosition().row()))) {
            numOfClearPathToEdge.add(Boolean.TRUE);
        }
        if (board.weakIsLegal(board.kingPosition(),
            Square.sq(8, board.kingPosition().row()))) {
            numOfClearPathToEdge.add(Boolean.TRUE);
        }
        if (numOfClearPathToEdge.size() >= 2
            || (numOfClearPathToEdge.size() == 1 && board.turn() == WHITE)) {
            return WILL_WIN_VALUE;
        }
        if (myPiece().equals(WHITE)) {
            if (moveCount < FIRSTGRADE) {
                resScore = numWhitePiece - numBlackPiece
                    + kingEndeavors(board)
                    - enemyAroundKing(board);
            } else if (moveCount < SECONDGRADE) {
                resScore = (TWO * (numWhitePiece - numBlackPiece)
                    + SIX * kingEndeavors(board)
                    - ONEPOINTFIVE * enemyAroundKing(board));
            } else {
                resScore = (THREE * (numWhitePiece - numBlackPiece)
                    + TWELVE * kingEndeavors(board)
                    - THREE * enemyAroundKing(board));
            }
        } else {
            if (moveCount < FIRSTGRADE) {
                resScore = numWhitePiece - numBlackPiece
                    - numCornerPieces(board)
                    + kingEndeavors(board)
                    - enemyAroundKing(board);
            } else if (moveCount < SECONDGRADE) {
                resScore = (TWO * (numWhitePiece - numBlackPiece)
                    - numCornerPieces(board)
                    + ONEPOINTFIVE * kingEndeavors(board)
                    - SIX * enemyAroundKing(board));
            } else {
                resScore = (THREE * (numWhitePiece - numBlackPiece)
                    - numCornerPieces(board)
                    + ONEPOINTFIVE * kingEndeavors(board)
                    - TWELVE * enemyAroundKing(board));
            }
        }
        return resScore;
    }

    /**
     * Return my neighbors.
     * @param sq The square.
     */
    private List<Square> getNeighbors(Square sq) {
        List<Square> res = new ArrayList<Square>();
        if (sq.rookMove(0, 1) != null) {
            res.add(sq.rookMove(0, 1));
        }
        if (sq.rookMove(1, 1) != null) {
            res.add(sq.rookMove(1, 1));
        }
        if (sq.rookMove(2, 1) != null) {
            res.add(sq.rookMove(2, 1));
        }
        if (sq.rookMove(3, 1) != null) {
            res.add(sq.rookMove(3, 1));
        }
        return res;
    }

    /**
     * @param board The board.
     * @return A number.
     */
    public double enemyAroundKing(Board board) {
        double numPieces = 0.0;

        List<Square> kingNeighbors = getNeighbors(board.kingPosition());

        for (Square sq : kingNeighbors) {
            if (board.get(sq) == Piece.BLACK) {
                numPieces += 0.25;
            }
        }
        return numPieces;
    }
}
