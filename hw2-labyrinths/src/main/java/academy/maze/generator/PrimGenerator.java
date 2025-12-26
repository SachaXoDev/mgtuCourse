package academy.maze.generator;

import academy.maze.Generator;
import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import academy.maze.utils.MazeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrimGenerator implements Generator {
    private final Random random = new Random();
    private int width;
    private int height;
    private List<Point> frontierWalls = new ArrayList<>();
    private CellType[][] grid;

    @Override
    public Maze generate(int width, int height) {
        this.grid = MazeUtils.initializeGrid(width, height);
        this.width = grid[0].length;
        this.height = grid.length;

        Point startCell = new Point(1, 1);
        grid[startCell.y()][startCell.x()] = CellType.PASSAGE;

        addNeighborWalls(grid, 1, 1);

        while (!frontierWalls.isEmpty()) {
            int wallIndex = random.nextInt(frontierWalls.size());
            Point wall = frontierWalls.remove(wallIndex);

            int wx = wall.x();
            int wy = wall.y();

            if (wx % 2 != 0 && wy % 2 == 0) {
                processWall(grid, wx, wy, wx, wy - 1, wx, wy + 1);
            } else if (wx % 2 == 0 && wy % 2 != 0) {
                processWall(grid, wx, wy, wx - 1, wy, wx + 1, wy);
            }
        }
        grid[1][1] = CellType.START;
        grid[this.height - 2][this.width - 2] = CellType.END;
        return new Maze(grid);
    }

    private boolean isValid(int x, int y) {
        return x > 0 && x < width - 1 && y > 0 && y < height - 1;
    }

    private void addNeighborWalls(CellType[][] grid, int x, int y) {
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        for (int i = 0; i < 4; i++) {
            int wx = x + dx[i];
            int wy = y + dy[i];

            if (isValid(wx, wy) && grid[wy][wx] == CellType.WALL) {
                Point wallPoint = new Point(wx, wy);
                if (!this.frontierWalls.contains(wallPoint)) {
                    this.frontierWalls.add(wallPoint);
                }
            }
        }
    }

    private void processWall(CellType[][] grid, int wallX, int wallY, int c1x, int c1y, int c2x, int c2y) {
        if (!isValid(c1x, c1y) || !isValid(c2x, c2y)) {
            return;
        }
        CellType type1 = grid[c1y][c1x];
        CellType type2 = grid[c2y][c2x];

        if (type1 == CellType.PASSAGE && type2 == CellType.WALL) {
            grid[wallY][wallX] = CellType.PASSAGE;
            grid[c2y][c2x] = CellType.PASSAGE;
            addNeighborWalls(grid, c2x, c2y);
        } else if (type1 == CellType.WALL && type2 == CellType.PASSAGE) {
            grid[wallY][wallX] = CellType.PASSAGE;
            grid[c1y][c1x] = CellType.PASSAGE;
            addNeighborWalls(grid, c1x, c1y);
        }
    }
}
