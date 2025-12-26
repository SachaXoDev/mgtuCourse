package academy.maze;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MazeFileUtils {

    public static Maze loadMazeFromFile(String filename) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filename));

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Файл пуст");
        }

        int height = lines.size();
        int width = lines.get(0).length();
        CellType[][] cells = new CellType[height][width];

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            if (line.length() != width) {
                throw new IllegalArgumentException("Все строки лабиринта должны иметь одинаковую длину");
            }

            for (int x = 0; x < width; x++) {
                cells[y][x] = CellType.fromSymbol(line.charAt(x));
            }
        }

        return new Maze(cells);
    }
}
