package academy.maze.dto;

/** Тип ячейки в лабиринте. WALL - стена, ROUTE - свободная ячейка. */
public enum CellType {
    WALL('#'),
    PASSAGE(' '),
    START('O'),
    END('X'),
    ROUTE('.');

    private final char symbol;

    CellType(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public static CellType fromSymbol(char symbol) {
        return switch (symbol) {
            case '#' -> WALL;
            case ' ' -> PASSAGE;
            case 'O' -> START;
            case 'X' -> END;
            case '.' -> ROUTE;
            default -> throw new IllegalArgumentException("Неизвестный символ ячейки: " + symbol);
        };
    }
}
