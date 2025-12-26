package academy.maze.dto;

/**
 * Координаты точки
 *
 * @param x
 * @param y
 */
public record Point(int x, int y) {
    public static Point fromString(String s) {
        try {
            String[] parts = s.split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Неправильный символ точки. Используйте 'x, y'.");
            }
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());
            return new Point(x, y);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
