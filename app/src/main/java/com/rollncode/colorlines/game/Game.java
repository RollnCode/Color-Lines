package com.rollncode.colorlines.game;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.support.annotation.NonNull;

import com.rollncode.colorlines.game.interfaces.State;
import com.rollncode.colorlines.game.model.Position;
import com.rollncode.colorlines.util.IntWrapper;
import com.rollncode.colorlines.util.Utils;
import com.rollncode.colorlines.view.CellView;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 15.06.16
 */
public class Game {

    public static ArrayList<Position> path(final CellView[][] cells, Position end, Position start) {
        final int[][] field = wave(Utils.makeArray(cells), end, start);
        return findPath(field, start);
    }

    public static ArrayList<Position> generate(final CellView[][] cells, int[] colors, IntWrapper score) {
        final ArrayList<Position> empties = getEmptyCells(cells);
        final ArrayList<Position> generated = new ArrayList<>();
        final Random random = new Random();
        if (empties.size() >= 3) {
            for (int i = 0; i < 3; i++) {
                Position p = empties.get(random.nextInt(empties.size()));
                cells[p.y][p.x].setState(colors[i]);
                score.value += check(cells, p, score);
                generated.add(p);
                empties.remove(p);
            }
            return generated;
        }
        return generated;
    }

    /**
     * Generates colors for next 3 balls.
     *
     * @return array of {@link State} with colors
     */
    public static int[] nextColors() {
        final Random random = new Random();
        int[] colors = new int[3];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = random.nextInt((State.BLUE - State.ORANGE) + 1) + State.ORANGE;
        }
        return colors;
    }

    /**
     * Generates a wave (Lee algorithm).
     *
     * @param source game field
     * @return int[][] generated wave
     */
    private static int[][] wave(final int[][] source, Position end, Position start) {
        // maximum iteration counter (first impassable color)
        int nk = State.ORANGE;
        // iteration counter
        int ni = 0;
        int[][] field = Utils.copy(source);
        {
            field[end.y][end.x] = State.FINAL;
            field[start.y][start.x] = State.START;
        }

        while (ni < nk) {
            final FieldIterator y = new FieldIterator(end.y, start.y, field.length);
            for (; y.hasNext(); y.next()) {
                final FieldIterator x = new FieldIterator(end.x, start.x, field[y.value()].length);
                for (; x.hasNext(); x.next()) {
                    if (field[y.value()][x.value()] == ni) {
                        // need to check all neighbor cells
                        if (y.value() + 1 < field.length) {
                            if (field[y.value() + 1][x.value()] == State.START) {
                                return field;
                            } else if (field[y.value() + 1][x.value()] == State.PASSABLE) {
                                field[y.value() + 1][x.value()] = ni + 1;
                            }
                        }

                        if (y.value() - 1 >= 0) {
                            if (field[y.value() - 1][x.value()] == State.START) {
                                return field;
                            } else if (field[y.value() - 1][x.value()] == State.PASSABLE) {
                                field[y.value() - 1][x.value()] = ni + 1;
                            }
                        }

                        if (x.value() + 1 < field[y.value()].length) {
                            if (field[y.value()][x.value() + 1] == State.START) {
                                return field;
                            } else if (field[y.value()][x.value() + 1] == State.PASSABLE) {
                                field[y.value()][x.value() + 1] = ni + 1;
                            }
                        }

                        if (x.value() - 1 >= 0) {
                            if (field[y.value()][x.value() - 1] == State.START) {
                                return field;
                            } else if (field[y.value()][x.value() - 1] == State.PASSABLE) {
                                field[y.value()][x.value() - 1] = ni + 1;
                            }
                        }
                    }
                }
            }
            ni++;
        }
        return null;
    }

    /**
     * Finds path to cell marked as {@link State#FINAL} from {@link State#START}
     *
     * @param field generated wave {@link Game#wave(int[][], Position, Position)}
     * @param c start
     * @return ArrayList of steps
     */
    private static ArrayList<Position> findPath(int[][] field, Position c) {
        if (field == null) {
            return null; // can't pass to the point
        }

        final ArrayList<Position> path = new ArrayList<>();
        path.add(c);
        int min = State.START;
        do {
            Position temp = c;
            if (c.y + 1 < field.length) {
                if (field[c.y + 1][c.x] < min) {
                    min = field[c.y + 1][c.x];
                    temp = new Position(c.x, c.y + 1);
                }
            }

            if (c.y - 1 >= 0) {
                if (field[c.y - 1][c.x] < min) {
                    min = field[c.y - 1][c.x];
                    temp = new Position(c.x, c.y - 1);
                }
            }

            if (c.x + 1 < field[c.y].length) {
                if (field[c.y][c.x + 1] < min) {
                    min = field[c.y][c.x + 1];
                    temp = new Position(c.x + 1, c.y);
                }
            }

            if (c.x - 1 >= 0) {
                if (field[c.y][c.x - 1] < min) {
                    min = field[c.y][c.x - 1];
                    temp = new Position(c.x - 1, c.y);
                }
            }
            c = temp;
            if (!path.contains(c)) {
                path.add(c);
            }
        } while (min != 0 && min != State.START);
        return path;
    }

    public static ArrayList<Position> getEmptyCells(final CellView[][] cells) {
        final ArrayList<Position> empties = new ArrayList<>();
        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells[y].length; x++) {
                if (cells[y][x].getState() == State.PASSABLE) {
                    empties.add(new Position(x, y));
                }
            }
        }
        return empties;
    }

    private static int remove(final CellView[][] cells, ArrayList<Position> toRemove) {
        if (toRemove != null && toRemove.size() > 0) {
            for (Position p : toRemove) {
                cells[p.y][p.x].setState(State.PASSABLE);
            }
            return toRemove.size();
        }
        return 0;
    }

    private static boolean isEmptyField(final CellView[][] cells) {
        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells[y].length; x++) {
                if (cells[y][x].getState() != State.PASSABLE) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if player makes line and adds animation to remove
     */
    public static int check(final CellView[][] source, Position p, final IntWrapper score) {
        final int[][] cells = Utils.makeArray(source);
        final ArrayList<Position> toRemove = findLine(cells, p);
        AnimatorSet set = GameAnimator.disappear(source, toRemove);
        set.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                remove(source, toRemove);
                if (isEmptyField(source)) {
                    GameAnimator.appear(source, Game.generate(source, nextColors(), score)).start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.start();
        return toRemove.size();
    }

    /**
     * Finds if player makes lines in all possible directions.
     *
     * @param p position to start finding
     * @return ArrayList of {@link Position} to remove
     */
    private static ArrayList<Position> findLine(@NonNull final int[][] cells, @NonNull Position p) {
        final int quantity = quantityToBurn(cells.length);
        final ArrayList<Position> toRemove = new ArrayList<>();
        toRemove.add(p);

        // available directions
        boolean horizontal = true;
        boolean horizontal2 = true;

        boolean vertical = true;
        boolean vertical2 = true;

        boolean diagonal = true;
        boolean diagonal2 = true;

        boolean opposite = true;
        boolean opposite2 = true;

        int i = 1;

        final ArrayList<Position> horizontals = new ArrayList<>();
        final ArrayList<Position> verticals = new ArrayList<>();
        final ArrayList<Position> diagonals = new ArrayList<>();
        final ArrayList<Position> opposites = new ArrayList<>();

        final int state = cells[p.y][p.x];

        while (horizontal || vertical || diagonal || opposite ||
                horizontal2 || vertical2 || diagonal2 || opposite2) {

            int x, y;
            if (horizontal) {
                x = p.x + i;
                y = p.y;
                if (x < cells[y].length) {
                    if (cells[y][x] == state) {
                        horizontals.add(new Position(x, y));
                    } else {
                        horizontal = false;
                    }

                } else {
                    horizontal = false;
                }
            }

            if (horizontal2) {
                x = p.x - i;
                y = p.y;
                if (x >= 0) {
                    if (cells[y][x] == state) {
                        horizontals.add(new Position(x, y));
                    } else {
                        horizontal2 = false;
                    }

                } else {
                    horizontal2 = false;
                }
            }

            if (vertical) {
                x = p.x;
                y = p.y + i;
                if (y < cells.length) {
                    if (cells[y][x] == state) {
                        verticals.add(new Position(x, y));
                    } else {
                        vertical = false;
                    }

                } else {
                    vertical = false;
                }
            }

            if (vertical2) {
                x = p.x;
                y = p.y - i;
                if (y >= 0) {
                    if (cells[y][x] == state) {
                        verticals.add(new Position(x, y));
                    } else {
                        vertical2 = false;
                    }

                } else {
                    vertical2 = false;
                }
            }

            if (diagonal) {
                x = p.x + i;
                y = p.y + i;
                if (y < cells.length && x < cells[y].length) {
                    if (cells[y][x] == state) {
                        diagonals.add(new Position(x, y));
                    } else {
                        diagonal = false;
                    }

                } else {
                    diagonal = false;
                }
            }

            if (diagonal2) {
                x = p.x - i;
                y = p.y - i;
                if (y >= 0 && x >= 0) {
                    if (cells[y][x] == state) {
                        diagonals.add(new Position(x, y));
                    } else {
                        diagonal2 = false;
                    }

                } else {
                    diagonal2 = false;
                }
            }

            if (opposite) {
                x = p.x - i;
                y = p.y + i;
                if (y < cells.length && x >= 0) {
                    if (cells[y][x] == state) {
                        opposites.add(new Position(x, y));
                    } else {
                        opposite = false;
                    }

                } else {
                    opposite = false;
                }
            }

            if (opposite2) {
                x = p.x + i;
                y = p.y - i;
                if (y >= 0 && x < cells[y].length) {
                    if (cells[y][x] == state) {
                        opposites.add(new Position(x, y));
                    } else {
                        opposite2 = false;
                    }

                } else {
                    opposite2 = false;
                }
            }

            i++;
            if (i >= cells.length) {
                break;
            }

        }

        final int count = quantity - 1;
        add(toRemove, horizontals, count);
        add(toRemove, verticals, count);
        add(toRemove, diagonals, count);
        add(toRemove, opposites, count);

        if (toRemove.size() < quantity) {
            toRemove.clear();
        }

        return toRemove;
    }

    private static void add(ArrayList<Position> source, ArrayList<Position> collection, int count) {
        if (collection.size() >= count) {
            source.addAll(collection);
        }
    }

    private static int quantityToBurn(final int size) {
        switch (size) {
            case 5:
                return 3;

            case 7:
                return 4;

            case 9:
            default:
                return 5;
        }
    }

}