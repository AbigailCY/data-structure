import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

/* A class representing a simplified grid board, where
 * each player controls one piece and attempts to trap
 * the other player.
 */
public class Lockdown implements Iterable<String[]> {

    /* Array to keep track of the state of the board. */
    private char[][] board;

    /* Coordinates of the X player. */
    private int[] xPos;

    /* Coordinates of the O player. */
    private int[] oPos;

    /* Whether it is X's turn or not. */
    private boolean xTurn;

    Lockdown(int size) {
        board = new char[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                board[r][c] = '-';
            }
        }
        board[0][0] = 'X';
        board[size - 1][size - 1] = 'O';
        xPos = new int[]{0, 0};
        oPos = new int[]{size - 1, size - 1};
        xTurn = true;
    }

    /* Returns the current state of the board. */
    public char[][] getBoard() {
        return board;
    }

    /* Returns a char representing the current player. */
    public char turn() {
        if (xTurn) {
            return 'X';
        }
        return 'O';
    }

    /* Converts a String POS into coordinates which can be fed into the board.
     */
    public static int[] getCoords(String pos) {
        return new int[]{pos.charAt(0) - 'A', pos.charAt(1) - '0'};
    }

    public static String getPosition(int[] coords) {
        return "" + (char) ('A' + coords[0]) + coords[1];
    }

    /* Sets the board at position POS to char C
     */
    public void setBoard(int[] pos, char c) {
        board[pos[0]][pos[1]] = c;
    }

    /* Retrieves the value of the board at position POS.
     */
    public char getBoard(int[] pos) {
        return board[pos[0]][pos[1]];
    }

    /* Performs the move from the current player to MOVE, followed by
     * performing the block at BLOCK.
     */
    public void makeMove(String move, String block) {
        if (xTurn) {
            if (moveAvailable(xPos, getCoords(move), xPos)
                    && moveAvailable(getCoords(move), getCoords(block), xPos)) {
                setBoard(getCoords(move), getBoard(xPos));
                setBoard(xPos, '-');
                xPos = getCoords(move);
                setBoard(getCoords(block), '#');
                xTurn = false;
                return;
            }
        } else {
            if (moveAvailable(oPos, getCoords(move), oPos)
                    && moveAvailable(getCoords(move), getCoords(block), oPos)) {
                setBoard(getCoords(move), getBoard(oPos));
                setBoard(oPos, '-');
                oPos = getCoords(move);
                setBoard(getCoords(block), '#');
                xTurn = true;
                return;
            }
        }
        System.out.println("Bad input move.");
    }

    /* Checks that a move is available from FROM to TO, counting IGNORE
     * as an empty space in the board..
     */
    public boolean moveAvailable(int[] from, int[] to, int[] ignore) {
        return to[0] >= 0 && to[0] < board.length
                && to[1] >= 0 && to[1] < board.length
                && (from[0] - to[0]) * (from[0] - to[0]) <= 1
                && (from[1] - to[1]) * (from[1] - to[1]) <= 1
                && !(from[0] == to[0] && from[1] == to[1])
                && (board[to[0]][to[1]] == '-'
                || (to[0] == ignore[0] && to[1] == ignore[1]));
    }

    /* Returns the position that results from traveling in direction D
     * from position LOC. Values of D represent adjacent positions to LOC,
     * starting from the upper left and going clockwise.
     */
    private int[] getDirection(int[] loc, int d) {
        int[][] dirs = new int[][]{{-1, -1}, {-1, 0}, {-1, 1},
                {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};
        return new int[]{loc[0] + dirs[d][0], loc[1] + dirs[d][1]};
    }

     @Override
    public Iterator<String[]> iterator() {
        if (xTurn) {
            return new MoveIterator(xPos);
        }
        return new MoveIterator(oPos);
    }

    /* A private Iterator class which iterates through the moves
     * available to the current player.
     */
    private class MoveIterator implements Iterator<String[]> {

        /* Coordinates of the current player's position. */
        int[] from;

        /* Direction in which I am attempting to move. */
        int moveDir = 0;

        /* Direction in which I am attempting to block. */
        int blockDir = 0;

        /* Constructor which takes in FROM, a position to move from.
         */
        MoveIterator(int[] from) {
            this.from = from;
        }

        @Override
        public boolean hasNext() {
            int pointer = -1;
            for (int i = 0; i < 8; i++) {
                if (moveAvailable(from, getDirection(from, i), from)) {
                    pointer = i;
                    break;
                }
            }
            if (pointer == -1) {
                return false;
            }
            return blockDir >= 0 && blockDir <= 7 && (!(Arrays.equals(new int[]{moveDir, blockDir}, nextHelper())));
        }

        /* A helper method to calculate the next valid position. */
        private void nextPos() {
            while (true) {
                if (blockDir >= 0 && blockDir <= 7) {
                    break;
                } else if (moveDir >= 0 && moveDir <= 7){
                    moveDir ++;
                    blockDir = 0;
                } else {
                    break;
                }
            }
        }

        private int[] nextHelper() {
            int[] a = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
            int[] b = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
            int[] result = null;
            for (int i : a) {
                for (int j : b) {
                    if (moveAvailable(from, getDirection(from, i), from) && moveAvailable(getDirection(from, i), getDirection(getDirection(from, i), j), from)) {
                        result = new int[]{i, j};
                    }
                }
            }
            result[1] ++;
            while (true) {
                if (result[1] >= 0 && result[1] <= 7) {
                    break;
                } else if (result[0] >= 0 && result[0] <= 7){
                    result[0] ++;
                    result[1] = 0;
                } else {
                    break;
                }
            }
            return result;
        }

        @Override
        public String[] next() {
            String[] result = new String[]{getPosition(getDirection(from, moveDir)), getPosition(getDirection(getDirection(from, moveDir), blockDir))};
            blockDir ++;
            nextPos();
            if (moveDir != 8) {
                while (!(moveAvailable(from, getCoords(result[0]), from)) || !(moveAvailable(getCoords(result[0]), getCoords(result[1]), from))) {
                    result = new String[]{getPosition(getDirection(from, moveDir)), getPosition(getDirection(getDirection(from, moveDir), blockDir))};
                    blockDir ++;
                    nextPos();
                }
            }
            return result;
        }
    }
    
    /* Prints our contents of the board with coordinates
     * on left and top of board.
     */
    public void printBoard() {
        StringBuilder toPrint = new StringBuilder();
        for (int r = 0; r < board.length; r++) {
            if (r == 0) {
                for (int i = 0; i < board.length; i++) {
                    if (i == 0) {
                        toPrint.append("    ");
                    }
                    toPrint.append(i);
                    toPrint.append(" ");
                }
                toPrint.append("\n\n");
            }
            for (int c = 0; c < board.length; c++) {
                if (c == 0) {
                    toPrint.append((char) ('A' + r));
                    toPrint.append("   ");
                }
                toPrint.append(board[r][c]);
                toPrint.append(" ");
            }
            toPrint.append("\n");
        }
        System.out.println(toPrint);
    }

    public static void main(String[] args) {
        // You can modify this line in order to change the size of the board
        Lockdown game = new Lockdown(5);
        Scanner reader = new Scanner(System.in);
        while (game.iterator().hasNext()) {
            game.printBoard();
            System.out.println(game.turn() + " to move.\nEnter move as \"__ __\" or type \"help\"");
            String inp = reader.next();
            if (inp.toLowerCase().equals("help")) {
                System.out.println("Here are your possible moves:");
                for (String[] m : game) {
                    System.out.println(Arrays.toString(m));
                }
            } else {
                game.makeMove(inp, reader.next());
            }
        }
        System.out.println(game.turn() + " loses!");
    }
}
