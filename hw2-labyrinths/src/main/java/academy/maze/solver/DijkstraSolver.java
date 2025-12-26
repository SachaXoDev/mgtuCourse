package academy.maze.solver;

import academy.maze.Solver;
import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;
import academy.maze.utils.MazeUtils;
import java.util.*;

public class DijkstraSolver implements Solver {

    @Override
    public Path solve(Maze maze, Point start, Point end) {
        CellType[][] cells = maze.cells();
        int height = cells.length;
        int width = cells[0].length;

        Map<Point, Double> distances = new HashMap<>();
        Map<Point, Point> previous = new HashMap<>();
        PriorityQueue<Point> queue =
                new PriorityQueue<>(Comparator.comparingDouble(p -> distances.getOrDefault(p, Double.MAX_VALUE)));

        // Инициализация
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (cells[y][x] != CellType.WALL) {
                    Point point = new Point(x, y);
                    distances.put(point, Double.MAX_VALUE);
                }
            }
        }

        distances.put(start, 0.0);
        queue.offer(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(end)) {
                break;
            }

            for (Point neighbor : getNeighbors(current, cells)) {
                double newDist = distances.get(current) + 1;

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }

        return reconstructPath(previous, start, end);
    }

    private List<Point> getNeighbors(Point point, CellType[][] cells) {
        return MazeUtils.getNeighbors(point, cells);
    }

    private Path reconstructPath(Map<Point, Point> previous, Point start, Point end) {
        List<Point> path = new ArrayList<>();
        Point current = end;

        // Проверяем, достижима ли конечная точка
        if (!previous.containsKey(current) && !current.equals(start)) {
            return new Path(new Point[0]); // Путь не найден
        }

        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }

        Collections.reverse(path);
        return new Path(path.toArray(new Point[0]));
    }
}
