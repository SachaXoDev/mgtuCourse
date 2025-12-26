package academy;

import academy.model.GameStatus;
import academy.service.GameEngine;
import academy.service.HangmanGameService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HangmanGameEngineTest {

    private final GameEngine gameEngine = new HangmanGameService();
    private static final int DEFAULT_ATTEMPTS = 6;
    private static final char TEST_PLACEHOLDER = '*';

    private GameStatus createGame(String word) {
        // Использование нового GameEngine для инициализации статуса игры
        return gameEngine.initializeGame(word, DEFAULT_ATTEMPTS, null, TEST_PLACEHOLDER);
    }

    @Test
    void guessLetter_currentGuessUpdateState() {
        GameStatus status = createGame("дом");
        assertEquals("* * *", status.getDisplayWord(), "Изначально слово должно быть скрыто");

        gameEngine.guessLetter(status, 'д');
        assertEquals("д * *", status.getDisplayWord(), "Должна открыться первая буква");
        assertTrue(status.getGuessedLetters().contains('д'), "Буква должна быть в списке угаданных");
    }

    @Test
    void guessLetter_correctlyUpdatesStateOnHit() {
        GameStatus status = createGame("молоко");

        gameEngine.guessLetter(status, 'м');
        gameEngine.guessLetter(status, 'л');

        gameEngine.guessLetter(status, 'о');
        assertEquals("м о л о * о", status.getDisplayWord(), "Должны открыться все вхождения буквы 'о' с учетом предыдущих угадываний");
        assertEquals(DEFAULT_ATTEMPTS, status.getRemainingAttempts(), "Попытки не должны уменьшиться при верном угадывании");
    }

    @Test
    void guessLetter_correctlyUpdatesStateOnMiss() {
        GameStatus status = createGame("самолет");
        gameEngine.guessLetter(status, 'я');

        assertEquals(DEFAULT_ATTEMPTS - 1, status.getRemainingAttempts(), "Попытки должны уменьшиться при неверном угадывании");
        assertEquals("* * * * * * *", status.getDisplayWord(), "Слово не должно измениться");
    }

    @Test
    void guessLetter_caseInsensitivityCheck() {
        // Угадывание заглавной буквы
        GameStatus status = createGame("привет");
        gameEngine.guessLetter(status, 'П');
        assertEquals("П * * * * *", status.getDisplayWord(), "Буква 'П' должна открыться в своем регистре");

        // Угадывание строчной буквы
        status = createGame("Привет");
        gameEngine.guessLetter(status, 'п');
        assertEquals("п * * * * *", status.getDisplayWord(), "Буква 'п' должна открыться в своем регистре");
    }

    @Test
    void guessLetter_completesWordCorrectly() {
        GameStatus status = createGame("сок");
        gameEngine.guessLetter(status, 'с');
        gameEngine.guessLetter(status, 'о');
        gameEngine.guessLetter(status, 'к');

        gameEngine.updateGameState(status); // Обновление состояния
        assertEquals(academy.model.GameState.WON, status.getState(), "Состояние должно быть WON");
        assertEquals("с о к", status.getDisplayWord());
    }

    @Test
    void guessLetter_gameOverOnAttemptsDepleted() {
        GameStatus status = gameEngine.initializeGame("стол", 1, null, TEST_PLACEHOLDER);
        gameEngine.guessLetter(status, 'а');

        gameEngine.updateGameState(status); // Обновление состояния
        assertEquals(academy.model.GameState.LOST, status.getState(), "Состояние должно быть LOST");
        assertEquals(0, status.getRemainingAttempts());
    }

    @Test
    void guessLetter_repeatedGuessReturnsNull() {
        GameStatus status = createGame("слон");
        gameEngine.guessLetter(status, 'с');

        assertEquals("с * * *", status.getDisplayWord(), "Повторное угадывание не должно менять отображение");

        assertNull(gameEngine.guessLetter(status, 'с'), "Повторное угадывание должно вернуть null");
        assertEquals(DEFAULT_ATTEMPTS, status.getRemainingAttempts(), "Попытки не должны меняться");
    }
}
