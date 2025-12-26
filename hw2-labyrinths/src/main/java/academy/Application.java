package academy;

import academy.maze.Generator;
import academy.maze.MazeFileUtils;
import academy.maze.Solver;
import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;
import academy.maze.generator.DFSGenerator;
import academy.maze.generator.PrimGenerator;
import academy.maze.solver.AStarSolver;
import academy.maze.solver.DijkstraSolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "maze-cli",
        version = "Maze Generator & Solver 1.0",
        mixinStandardHelpOptions = true,
        subcommands = {Application.GenerateCommand.class, Application.SolveCommand.class})
public class Application implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final ObjectReader YAML_READER =
            new ObjectMapper(new YAMLFactory()).findAndRegisterModules().reader();

    @Option(
            names = {"-s", "--font-size"},
            description = "Font size")
    int fontSize;

    @Parameters(
            paramLabel = "<word>",
            defaultValue = "Hello, picocli",
            description = "Words to be translated into ASCII art.")
    private String[] words;

    @Option(
            names = {"-c", "--config"},
            description = "Path to JSON config file")
    private File configPath;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    private static void saveToFile(String content, String filename) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(java.nio.file.Path.of(filename))) {
            writer.write(content);
        }
    }

    @Override
    public void run() {}

    @Command(name = "generate", description = "Генерирует лабиринт.")
    static class GenerateCommand implements Runnable {
        @ParentCommand
        private Application parent;

        @Option(
                names = {"--algorithm"},
                required = true,
                description = "Алгоритм генерации: dfs, prim")
        String algorithm;

        @Option(
                names = {"--width"},
                required = true,
                description = "Ширина лабиринта.")
        int width;

        @Option(
                names = {"--height"},
                required = true,
                description = "Высота лабиринта.")
        int height;

        @Option(
                names = {"--output"},
                description = "Имя файла для сохранения.")
        String output;

        @Override
        public void run() {
            var config = parent.loadConfig();
            LOGGER.atInfo().addKeyValue("config", config).log("Config content");

            Generator generator = null;
            if ("dfs".equalsIgnoreCase(algorithm)) {
                generator = new DFSGenerator();
            } else if ("prim".equalsIgnoreCase(algorithm)) {
                generator = new PrimGenerator();
            } else {
                System.err.println("Неизвестный алгоритм: " + algorithm);
                return;
            }

            Maze maze = generator.generate(width, height);

            try {
                if (output != null) {
                    saveToFile(maze.toString(), output);
                    System.out.println("Лабиринт сохранен в файл: " + output);
                } else {
                    System.out.println("Сгенерированный лабиринт (Алгоритм: " + algorithm + ")");
                    System.out.print(maze.toString());
                }
            } catch (IOException e) {
                System.err.println("Ошибка при сохранении файла: " + e.getMessage());
            }
        }
    }

    @Command(name = "solve", description = "Находит путь в лабиринте.")
    static class SolveCommand implements Runnable {

        @ParentCommand
        private Application parent;

        @Option(
                names = {"--algorithm"},
                required = true,
                description = "Алгоритм решения: astar, dijkstra")
        String algorithm;

        @Option(
                names = {"--file"},
                required = true,
                description = "Файл с описанием лабиринта.")
        String file;

        @Option(
                names = {"--start"},
                required = true,
                description = "Начальная точка в формате x,y.")
        String start;

        @Option(
                names = {"--end"},
                required = true,
                description = "Конечная точка в формате x,y.")
        String end;

        @Option(
                names = {"--output"},
                description = "Имя файла для сохранения решения.")
        String output;

        @Override
        public void run() {
            try {
                Point startPoint = Point.fromString(start);
                Point endPoint = Point.fromString(end);

                Maze maze = loadMazeFromFile(file);

                Solver solver = null;
                if ("astar".equalsIgnoreCase(algorithm)) {
                    solver = new AStarSolver();
                } else if ("dijkstra".equalsIgnoreCase(algorithm)) {
                    solver = new DijkstraSolver();
                } else {
                    System.err.println("Неизвестный алгоритм: " + algorithm);
                    return;
                }

                assert solver != null;
                Path path = solver.solve(maze, startPoint, endPoint);

                Maze solvedMaze = createMazeWithPath(maze, path, startPoint, endPoint);

                if (output != null) {
                    saveToFile(solvedMaze.toString(), output);
                    System.out.println("Решение сохранено в файл: " + output);
                } else {
                    System.out.println("Лабиринт с решением:");
                    System.out.print(solvedMaze.toString());
                }

            } catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }

        private Maze loadMazeFromFile(String filename) throws IOException {
            return MazeFileUtils.loadMazeFromFile(filename);
        }

        private Maze createMazeWithPath(Maze original, Path path, Point start, Point end) {
            CellType[][] cells = new CellType[original.cells().length][];
            for (int i = 0; i < original.cells().length; i++) {
                cells[i] = original.cells()[i].clone();
            }

            for (Point p : path.points()) {
                if (!p.equals(start) && !p.equals(end)) {
                    cells[p.y()][p.x()] = CellType.ROUTE;
                }
            }

            return new Maze(cells);
        }
    }

    private AppConfig loadConfig() {
        // fill with cli options
        if (configPath == null) return new AppConfig(fontSize, words);

        // use config file if provided
        try {
            return YAML_READER.readValue(configPath, AppConfig.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
