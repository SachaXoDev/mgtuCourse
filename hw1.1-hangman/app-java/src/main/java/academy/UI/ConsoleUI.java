package academy.UI;

import academy.model.GameState;
import academy.model.GameStatus;
import academy.model.WordDictionary;
import academy.service.GameEngine;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class ConsoleUI {

    private static final Map<String, Integer> DIFFICULTY_LEVELS = Map.of(
        "1", 10,
        "2", 6,
        "3", 4
    );
    private final Random random = new Random();
    private final GameEngine gameEngine;
    private final Scanner scanner;

    private static final String HANGMAN = """
 +---+



|
|
========,
 +---+


|
|
|
========,
 +---+

|
|
|
|
========,
 +---+
|
|
|
|
|
========,
 +---+
|   |
|
|
|
|
========,
 +---+
|   |
|   O
|
|
|
========,
 +---+
|   |
|   O
|   |
|
|
========,
 +---+
|   |
|   O
|   |\\
|
|
========,
 +---+
|   |
|   O
|  /|\\
|
|
========,
 +---+
|   |
|   O
|  /|\\
|    \\
|
========,
 +---+
|   |
|   O
|  /|\\
|  / \\
|
========
    """;

    public ConsoleUI(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        this.scanner = new Scanner(System.in);
    }

    public void runInteractiveMode(WordDictionary dictionary) {
        System.out.println("Как вас зовут?");
        String playerName = scanner.nextLine();
        System.out.println("Привет, " + playerName + "! Давай сыграем в Виселицу!");

        String chosenCategory = chooseCategory(dictionary);
        int attempts = chooseDifficulty();
        String secretWord = chosenCategory != null
            ? dictionary.getRandomWord(chosenCategory)
            : dictionary.getRandomWord();

        GameStatus gameStatus = gameEngine.initializeGame(secretWord, attempts, dictionary, '_');

        if (chosenCategory != null) {
            System.out.println("Выбрана категория: " + chosenCategory);
        } else {
            System.out.println("Категория не выбрана, выбрано случайное слово");
        }

        while (gameStatus.getState() == GameState.IN_GAME) {
            displayGameStatus(gameStatus);
            handleUserInput(gameStatus, dictionary);
        }
        displayFinalResult(gameStatus);
    }

    private String chooseCategory(WordDictionary dictionary) {
        List<String> categories = dictionary.getAllCategories();
        System.out.println("\nВыбери категорию (введи номер): ");

        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        System.out.println("Если хочешь случайную категорию, нажми Enter ");

        String categoryInput = scanner.nextLine().trim();

        if (categoryInput.isEmpty()) return null;

        try {
            int categoryIndex = Integer.parseInt(categoryInput) - 1;
            return dictionary.getCategoriesByIndex(categoryIndex);
        } catch (NumberFormatException e) {
            if (dictionary.isValidCategory(categoryInput)) {
                return categoryInput;
            }
        }
        System.out.println("Неверный выбор категории. Будет выбрано случайное слово.");
        return null;
    }

    private int chooseDifficulty() {
        System.out.println("\nВыберите уровень сложности (введи номер): ");
        System.out.println("1.Лёгкий(10 попыток)");
        System.out.println("2.Средний(6 попыток)");
        System.out.println("3.Сложный(4 попытки)");
        System.out.print("Выберите номер(1, 2 или 3): ");

        String levelInput = scanner.nextLine().trim();
        Integer attempts = DIFFICULTY_LEVELS.get(levelInput);

        if (attempts != null) return attempts;

        List<Integer> values = new java.util.ArrayList<>(DIFFICULTY_LEVELS.values());
        int randomAttempts = values.get(random.nextInt(values.size()));

        System.out.printf("Уровень не выбран. Случайно выбрано %d попыток.\n", randomAttempts);
        return randomAttempts;
    }

    private void handleUserInput(GameStatus status, WordDictionary dictionary) {
        System.out.print("Введи букву или 'подсказка' для помощи: ");
        String userInput = scanner.next();
        scanner.nextLine();

        if (userInput.equalsIgnoreCase("подсказка") || userInput.equalsIgnoreCase("hint")) {
            String hintMessage = gameEngine.useHint(status, dictionary);
            System.out.println(hintMessage);
            return;
        }

        if (userInput.length() != 1) {
            System.out.println("Ошибка ввода! Введите, пожалуйста, одну букву.");
            return;
        }

        char userChar = userInput.charAt(0);
        if (!Character.isLetter(userChar)) {
            System.out.println("Ошибка ввода! Введите, пожалуйста, только буквы.");
            return;
        }

        Boolean letterFound = gameEngine.guessLetter(status, userChar);
        if (letterFound == null) {
            System.out.println("Вы уже угадывали эту букву '" + userChar + "'!");
        } else if (letterFound) {
            System.out.println("Угадал, продолжай в том же духе!");
        } else {
            if (status.getRemainingAttempts() > 0) {
                System.out.println("Не угадал, попробуй еще раз");
            } else {
                System.out.println("Не угадал, эта была последняя попытка");
            }
        }
    }

    private void displayGameStatus(GameStatus status) {
        System.out.println("\nОсталось попыток: " + status.getRemainingAttempts());
        printHangman(status.getRemainingAttempts());
        System.out.println("Угаданное слово: " + status.getDisplayWord());
        System.out.println("Использованные буквы: " + status.getGuessedLetters());
    }

    private void displayFinalResult(GameStatus status) {
        System.out.println("\nИгра закончена!");
        if (status.getState() == GameState.WON) {
            System.out.println("Угаданное слово: " + status.getDisplayWord());
            System.out.println("\nПоздравляю <3, вы угадали слово: " + status.getSecretWord());
        } else {
            printHangman(status.getRemainingAttempts());
            System.out.println("\nВы проиграли:( Загаданное слово было: " + status.getSecretWord());
        }
    }

    private static void printHangman(int attempts) {
        String[] frames = HANGMAN.split(",\\s*");
        final int MAX_INDEX = frames.length - 1;
        int index = MAX_INDEX - attempts;
        if (index >= 0 && index < frames.length) {
            System.out.println(frames[index].trim());
        }
    }
}
