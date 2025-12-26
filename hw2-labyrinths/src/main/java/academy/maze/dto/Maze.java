package academy.maze.dto;

/**
 * Лабиринт.
 *
 * @param cells Массив ячеек лабиринта.
 */
public record Maze(CellType[][] cells) {
    @Override
    public String toString() {
        if (cells == null || cells.length == 0) {
            return "Лабиринт пустой.";
        }
        StringBuilder sb = new StringBuilder();
        for (CellType[] row : cells) {
            for (CellType cell : row) {
                sb.append(cell.getSymbol());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
