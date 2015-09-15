package com.anorange;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class App {

    static int strLength = 16;
    final static int MAX_VOWELS = 2;

    final static int WIDTH = 5;
    final static int HEIGHT = 4;

    final static char[][] FIELD = {{'A', 'B', 'C', 'D', 'E'},
                                   {'F', 'G', 'H', 'I', 'J'},
                                   {'K', 'L', 'M', 'N', 'O'},
                                   {' ', '1', '2', '3', ' '}};

    final static int[][] VOWELS = {{1, 0, 0, 0, 1},
                                   {0, 0, 0, 1, 0},
                                   {0, 0, 0, 0, 1},
                                   {0, 0, 0, 0, 0}};

    final static int[][] ALL_POSSIBLE_KNIGHT_MOVES = {{-2,-1}, {-2,1}, {-1,-2}, {1,-2}, {2,-1}, {2,1}, {-1,2}, {1,2}};

    // lists of all allowed moves from each cell
    final static ArrayList<Pair<Integer,Integer>>[][] allPossibleMoves = new ArrayList[HEIGHT][WIDTH];

    public static boolean isValidPos(final int x, final int y) {
        return (x >= 0 && x < WIDTH) && (y >= 0 && y < HEIGHT) && (FIELD[y][x] != ' ');
    }

    public static void main(String[] args) {

        System.out.print("Enter the string length: ");
        strLength = new Scanner(System.in).nextInt();

        // generate all possible moves for each cell
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if ( !isValidPos(x,y) ) {
                    continue;
                }
                final ArrayList<Pair<Integer,Integer>> possibleMovesForCell = new ArrayList<Pair<Integer,Integer>>(6);
                for (int k = 0; k < ALL_POSSIBLE_KNIGHT_MOVES.length; k++) {
                    final int new_x = x + ALL_POSSIBLE_KNIGHT_MOVES[k][0];
                    final int new_y = y + ALL_POSSIBLE_KNIGHT_MOVES[k][1];
                    if (isValidPos(new_x, new_y)) {
                        possibleMovesForCell.add(Pair.create(new_x, new_y));
                    }
                }
                allPossibleMoves[y][x] = possibleMovesForCell;
//                System.out.println("For cell (" + x + "," + y + ") allowed moves: " + possibleMovesForCell.size() + " -> " + possibleMovesForCell);
            }
        }

        long totalCount = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if ( !isValidPos(x,y) ) {
                    continue;
                }
                totalCount += getCount(x, y, 1, 0);
            }
        }
        System.out.println("Number of all possible combinations: " + totalCount);
    }

    // (Step,VowelsSoFar) -> Count for each cell
    // we can even use 4-dimensional array [HEIGHT][WIDTH][MAX_STRING_LENGTH][MAX_VOWELS] for that to make it even faster
    final static Map<Pair<Integer, Integer>, Long>[][] cache = new HashMap[HEIGHT][WIDTH];

    static {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                cache[y][x] = new HashMap<Pair<Integer, Integer>, Long>(32 * MAX_VOWELS);
            }
        }
    }

    static long getCount(final int x, final int y, final int step, int vowels) {
        vowels += VOWELS[y][x];
        if (vowels > MAX_VOWELS) {
            return 0;
        }
        if (step == strLength) {
            return 1;
        }

        final ArrayList<Pair<Integer,Integer>> moves = allPossibleMoves[y][x];
        long totalCount = 0;
        for (Pair<Integer, Integer> move : moves) {
            final Pair<Integer, Integer> key = Pair.create(step + 1, vowels);
            final Long cachedCount = cache[move.second][move.first].get(key);
            if (cachedCount != null) {
                totalCount += cachedCount;
            } else {
                final long curCount = getCount(move.first, move.second, step + 1, vowels);
                cache[move.second][move.first].put(key, curCount);
                totalCount += curCount;
            }
        }
        return totalCount;
    }

    // it should be extracted into the separate class 
    // Just put it hear to keep everything in one place
    static public final class Pair<X, Y> {
        public final X first;
        public final Y second;

        private Pair(X first, Y second) {
            this.first = first;
            this.second = second;
        }

        public static <F,S> Pair<F,S> create(F first, S second) {
            return new Pair<F,S>(first,second);
        }

        @Override
        public int hashCode() {
            return (first != null ? first.hashCode() : 0) + 31 * (second != null ? second.hashCode() : 0);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != this.getClass()) { return false; }
            Pair that = (Pair) o;
            return (first == null ? that.first == null : first.equals(that.first))
                    && (second == null ? that.second == null : second.equals(that.second));
        }

        @Override
        public String toString() {
            return "[" + first + ", " + second + "]";
        }
    }
}