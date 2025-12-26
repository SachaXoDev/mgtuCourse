package academy.maze.utils;

import academy.maze.dto.CellType;
import academy.maze.dto.Point;
import java.util.ArrayList;
import java.util.List;

public class MazeUtils {

    public static CellType[][] initializeGrid(int width, int height) {
        if (width < 3 || height < 3) {
            throw new IllegalArgumentException("Размер лабиринта должен быть не менее 3x3.");
        }

        int actualWidth = (width % 2 == 0) ? width + 1 : width;
        int actualHeight = (height % 2 == 0) ? height + 1 : height;

        CellType[][] grid = new CellType[actualHeight][actualWidth];
        for (int i = 0; i < actualHeight; i++) {
            for (int j = 0; j < actualWidth; j++) {
                grid[i][j] = CellType.WALL;
            }
        }
        return grid;
    }

    public static List<Point> getNeighbors(Point point, CellType[][] cells) {
        List<Point> neighbors = new ArrayList<>();
        int[] dx = {0, 1, 0, -1};
        int[] dy = {-1, 0, 1, 0};

        for (int i = 0; i < 4; i++) {
            int newX = point.x() + dx[i];
            int newY = point.y() + dy[i];

            if (newX >= 0
                    && newX < cells[0].length
                    && newY >= 0
                    && newY < cells.length
                    && cells[newY][newX] != CellType.WALL) {
                neighbors.add(new Point(newX, newY));
            }
        }
        return neighbors;
    }
}
