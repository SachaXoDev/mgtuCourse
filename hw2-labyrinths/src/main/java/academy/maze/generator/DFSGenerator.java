package academy.maze.generator;

import academy.maze.Generator;
import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.utils.MazeUtils;
import java.util.*;

public class DFSGenerator implements Generator {
    private final Random random = new Random();
    private int width;
    private int height;
    private CellType[][] grid;

    @Override
    public Maze generate(int width, int height) {
        this.grid = MazeUtils.initializeGrid(width, height);
        this.width = grid[0].length;
        this.height = grid.length;

        generateDFS(1, 1);
        grid[1][1] = CellType.START;
        grid[this.height - 2][this.width - 2] = CellType.END;
        return new Maze(grid);
    }

    private void generateDFS(int x, int y) {
        grid[y][x] = CellType.PASSAGE;
        List<Integer> directions = new ArrayList<>(List.of(0, 1, 2, 3));
        Collections.shuffle(directions, random);

        for (int dir : directions) {
            int dx = 0, dy = 0;
            int WallDx = 0, WallDy = 0;

            switch (dir) {
                case 0:
                    dx = 2;
                    WallDx = 1;
                    break;
                case 1:
                    dx = -2;
                    WallDx = -1;
                    break;
                case 2:
                    dy = -2;
                    WallDy = -1;
                    break;
                case 3:
                    dy = 2;
                    WallDy = 1;
                    break;
                default:
                    throw new IllegalStateException("Unexpected direction: " + dir);
            }

            int nextX = x + dx;
            int nextY = y + dy;
            int wallX = x + WallDx;
            int wallY = y + WallDy;

            if (nextX > 0
                    && nextX < width - 1
                    && nextY > 0
                    && nextY < height - 1
                    && grid[nextY][nextX] == CellType.WALL) {
                grid[wallY][wallX] = CellType.PASSAGE;
                generateDFS(nextX, nextY);
            }
        }
    }
}
