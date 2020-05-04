package tablut;

import ucb.gui2.Pad;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

import static tablut.Move.mv;
import static tablut.Piece.*;
import static tablut.Square.sq;

/**
 * A widget that displays a Tablut game.
 *
 * @author Laiming Huang
 */
class BoardWidget extends Pad {

    /* Parameters controlling sizes, speeds, colors, and fonts. */

    /**
     * Squares on each side of the board.
     */
    static final int SIZE = Board.SIZE;
    /**
     * Colors of empty squares, pieces, grid lines, and boundaries.
     */
    static final Color
        SQUARE_COLOR = new Color(238, 207, 161),
        THRONE_COLOR = new Color(180, 255, 180),
        ADJACENT_THRONE_COLOR = new Color(200, 220, 200),
        CLICKED_SQUARE_COLOR = new Color(255, 255, 100),
        GRID_LINE_COLOR = Color.black,
        WHITE_COLOR = Color.white,
        BLACK_COLOR = Color.black;
    /**
     * Margins.
     */
    static final int
        OFFSET = 2,
        MARGIN = 16;
    /**
     * Side of single square and of board (in pixels).
     */
    static final int
        SQUARE_SIDE = 30,
        BOARD_SIDE = SQUARE_SIDE * SIZE + 2 * OFFSET + MARGIN;
    /**
     * The font in which to render the "K" in the king.
     */
    static final Font KING_FONT = new Font("Serif", Font.BOLD, 18);
    /**
     * The font for labeling rows and columns.
     */
    static final Font ROW_COL_FONT = new Font("SanSerif", Font.PLAIN, 10);
    /**
     * Squares adjacent to the throne.
     */
    static final Square[] ADJACENT_THRONE = {
        Board.NTHRONE, Board.ETHRONE, Board.STHRONE, Board.WTHRONE
    };
    /**
     * Board being displayed.
     */
    private final Board _board = new Board();
    /**
     * Sigh.
     */
    private LinkedList<Square> _moveSquares;
    /**
     * Queue on which to post move commands (from mouse clicks).
     */
    private ArrayBlockingQueue<String> _commands;
    /**
     * True iff accepting moves from user.
     */
    private boolean _acceptingMoves;
    /**
     * Current clicked square.
     */
    private Square clicked;
    /**
     * Destination square.
     */
    private Square destination;

    /**
     * A graphical representation of a Tablut board that sends commands
     * derived from mouse clicks to COMMANDS.
     */
    BoardWidget(ArrayBlockingQueue<String> commands) {
        _commands = commands;
        setMouseHandler("click", this::mouseClicked);
        setPreferredSize(BOARD_SIDE, BOARD_SIDE);
        _acceptingMoves = false;
    }

    /**
     * Draw the bare board G.
     */
    private void drawGrid(Graphics2D g) {
        g.setColor(SQUARE_COLOR);
        g.fillRect(0, 0, BOARD_SIDE, BOARD_SIDE);
        g.setColor(THRONE_COLOR);
        g.fillRect(cx(Board.THRONE), cy(Board.THRONE),
            SQUARE_SIDE, SQUARE_SIDE);
        g.setColor(ADJACENT_THRONE_COLOR);
        g.fillRect(cx(Board.NTHRONE), cy(Board.NTHRONE),
            SQUARE_SIDE, SQUARE_SIDE);
        g.fillRect(cx(Board.ETHRONE), cy(Board.ETHRONE),
            SQUARE_SIDE, SQUARE_SIDE);
        g.fillRect(cx(Board.STHRONE), cy(Board.STHRONE),
            SQUARE_SIDE, SQUARE_SIDE);
        g.fillRect(cx(Board.WTHRONE), cy(Board.WTHRONE),
            SQUARE_SIDE, SQUARE_SIDE);
        if (clicked != null) {
            g.setColor(CLICKED_SQUARE_COLOR);
            g.fillRect(cx(clicked), cy(clicked), SQUARE_SIDE, SQUARE_SIDE);
        }

        g.setColor(GRID_LINE_COLOR);
        g.setFont(ROW_COL_FONT);

        int[] nums = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};
        for (int i = 0; i < _board.SIZE; i++) {
            g.drawString(String.valueOf(nums[i]), cx(0) - TWELVE,
                cy(i) + TWENTY);
            g.drawString(Character.toString(chars[i]), cx(i) + TWELVE,
                cy(0) + FOURTYONE);
        }
        for (int k = 0; k <= SIZE; k += 1) {
            g.drawLine(cx(0), cy(k - 1), cx(SIZE), cy(k - 1));
            g.drawLine(cx(k), cy(-1), cx(k), cy(SIZE - 1));
        }


    }

    /**
     * Draw.
     * @param sq sq
     * @return color
     */
    private Color pieceColor(Square sq) {
        if (_board.get(sq.col(), sq.row()).equals(BLACK)) {
            return BLACK_COLOR;
        } else if (_board.get(sq.col(), sq.row()).equals(WHITE)
            || _board.get(sq.col(), sq.row()).equals(KING)) {
            return WHITE_COLOR;
        }
        return new Color(R, G, B, A);
    }

    /**
     * R.
     */
    private static final int R = 238;
    /**
     * G.
     */
    private static final int G = 207;
    /**
     * B.
     */
    private static final int B = 161;
    /**
     * A.
     */
    private static final int A = 0;
    /**
     * 3.
     */
    private static final int THREE = 3;
    /**
     * 24.
     */
    private static final int TWENTYFOUR = 24;
    /**
     * 8.
     */
    private static final int EIGHT = 8;
    /**
     * TWENTYONE.
     */
    private static final int TWENTYONE = 21;
    /**
     * TEN.
     */
    private static final int TEN = 10;
    /**
     * TWENTY.
     */
    private static final int TWENTY = 20;
    /**
     * TWELVE.
     */
    private static final int TWELVE = 12;
    /**
     * FOURTYONE.
     */
    private static final int FOURTYONE = 41;

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        drawGrid(g);
        Square.SQUARE_LIST.iterator().forEachRemaining(s -> drawPiece(g, s));
    }

    /**
     * Draw the contents of S on G.
     */
    private void drawPiece(Graphics2D g, Square s) {
        g.setColor(pieceColor(s));
        int px = cx(s.row()), py = cy(s.col());
        if (_board.get(s.col(), s.row()).equals(KING)) {
            g.fillOval(px + THREE, py + THREE, TWENTYFOUR, TWENTYFOUR);
            g.setColor(Color.RED);
            g.setFont(KING_FONT);
            g.drawString("K", px + EIGHT, py + TWENTYONE);
        } else {
            g.fillOval(px + THREE, py + THREE, TWENTYFOUR, TWENTYFOUR);
        }
    }

    /**
     * Handle a click on S.
     */
    private void click(Square s) {
        if (clicked != null) {
            if (_board.isLegal(sq(clicked.row(), clicked.col()),
                sq(s.row(), s.col()))) {
                _commands.offer(mv(sq(clicked.row(), clicked.col()),
                    sq(s.row(), s.col())).toString());
            }
            clicked.clickMe();
            clicked = null;
            repaint();
        }
        if (_board.get(s.row(), s.col()) != EMPTY) {
            s.clickMe();
            if (s.isSelected()) {
                clicked = s;
            } else {
                clicked = null;
            }
        }
        repaint();
    }

    /**
     * Handle mouse click event E.
     */
    private synchronized void mouseClicked(String unused, MouseEvent e) {
        int xpos = e.getX(), ypos = e.getY();
        int x = (xpos - OFFSET - MARGIN) / SQUARE_SIDE,
            y = (OFFSET - ypos) / SQUARE_SIDE + SIZE - 1;
        if (_acceptingMoves
            && x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
            click(sq(x, y));
        }
    }

    /**
     * Revise the displayed board according to BOARD.
     */
    synchronized void update(Board board) {
        _board.copy(board);
        repaint();
    }

    /**
     * Turn on move collection iff COLLECTING, and clear any current
     * partial selection.  When move collection is off, ignore clicks on
     * the board.
     */
    void setMoveCollection(boolean collecting) {
        _acceptingMoves = collecting;
        repaint();
    }

    /**
     * Return x-pixel coordinate of the left corners of column X
     * relative to the upper-left corner of the board.
     */
    private int cx(int x) {
        return x * SQUARE_SIDE + OFFSET + MARGIN;
    }

    /**
     * Return y-pixel coordinate of the upper corners of row Y
     * relative to the upper-left corner of the board.
     */
    private int cy(int y) {
        return (SIZE - y - 1) * SQUARE_SIDE + OFFSET;
    }

    /**
     * Return x-pixel coordinate of the left corner of S
     * relative to the upper-left corner of the board.
     */
    private int cx(Square s) {
        return cx(s.col());
    }

    /**
     * Return y-pixel coordinate of the upper corner of S
     * relative to the upper-left corner of the board.
     */
    private int cy(Square s) {
        return cy(s.row());
    }

}
