package academy.maze.solver;

import academy.maze.Solver;
import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;
import academy.maze.utils.MazeUtils;
import java.util.*;
import java.util.Objects;

public class AStarSolver implements Solver {

    private static class Node implements Comparable<Node> {
        final Point point;
        final Node parent;
        final double gCost; // стоимость от старта
        final double hCost; // эвристическая стоимость до цели
        final double fCost; // общая стоимость

        Node(Point point, Node parent, double gCost, double hCost) {
            this.point = point;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.fCost, other.fCost);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return point.equals(node.point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(point);
        }
    }

    @Override
    public Path solve(Maze maze, Point start, Point end) {
        CellType[][] cells = maze.cells();

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Point> closedSet = new HashSet<>();
        Map<Point, Double> gCosts = new HashMap<>();

        double startHCost = heuristic(start, end);
        openSet.offer(new Node(start, null, 0, startHCost));
        gCosts.put(start, 0.0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.point.equals(end)) {
                return reconstructPath(current);
            }

            closedSet.add(current.point);

            for (Point neighbor : getNeighbors(current.point, cells)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                double tentativeGCost = gCosts.get(current.point) + 1;

                if (!gCosts.containsKey(neighbor) || tentativeGCost < gCosts.get(neighbor)) {
                    gCosts.put(neighbor, tentativeGCost);
                    double hCost = heuristic(neighbor, end);
                    openSet.offer(new Node(neighbor, current, tentativeGCost, hCost));
                }
            }
        }

        return new Path(new Point[0]);
    }

    private double heuristic(Point a, Point b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    private List<Point> getNeighbors(Point point, CellType[][] cells) {
        return MazeUtils.getNeighbors(point, cells);
    }

    private Path reconstructPath(Node endNode) {
        List<Point> path = new ArrayList<>();
        Node current = endNode;

        while (current != null) {
            path.add(current.point);
            current = current.parent;
        }

        Collections.reverse(path);
        return new Path(path.toArray(new Point[0]));
    }
}
