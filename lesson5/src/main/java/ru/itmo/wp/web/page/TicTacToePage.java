package ru.itmo.wp.web.page;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class TicTacToePage {
    public static class State {
        public enum Phase {
            RUNNING, WON_X, WON_O, DRAW
        }

        int size = 3, filled = 0;
        Character[][] cells = new Character[size][size];
        boolean crossesMove = true;
        Phase phase = Phase.RUNNING;

        public int getSize() {
            return size;
        }

        public Character[][] getCells() {
            return cells;
        }

        public boolean getCrossesMove() {
            return crossesMove;
        }

        public Phase getPhase() {
            return phase;
        }

        boolean checkLimits(int x, int y) {
            return 0 <= x && x < size && 0 <= y && y < size;
        }

        void move(int x, int y) {
            if (phase == Phase.RUNNING && checkLimits(x, y)) {
                cells[x][y] = crossesMove ? 'X' : 'O';
                crossesMove = !crossesMove;
                filled++;
                checkPhase(x, y);
            }
        }

        void checkPhase(int x, int y) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if ((dx != 0 || dy != 0) && goDeep(x, y, dx, dy) + goDeep(x, y, -dx, -dy) > size) {
                        phase = cells[x][y] == 'X' ? Phase.WON_X : Phase.WON_O;
                        break;
                    }
                }
            }
            if (filled == size * size) {
                phase = Phase.DRAW;
            }
        }

        int goDeep(int x, int y, int dx, int dy) {
            int result = 1;
            Character cell = cells[x][y];
            while (checkLimits(x += dx, y += dy) && cells[x][y] == cell) {
                result++;
            }
            return result;
        }
    }

    private void action(Map<String, Object> view) {
        if (!view.containsKey("state")) {
            newGame(view);
        }
    }

    private void onMove(HttpServletRequest request, Map<String, Object> view) {
        State state = (State) view.get("state");
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("cell_")) {
                state.move(key.charAt(5) - '0', key.charAt(6) - '0');
            }
        }
    }

    private void newGame(Map<String, Object> view) {
        view.put("state", new State());
    }
}
