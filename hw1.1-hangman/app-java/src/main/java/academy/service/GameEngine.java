package academy.service;

import academy.model.GameStatus;

public interface GameEngine {

    /**
     * @param secretWord Загаданное слово.
     * @param attempts Начальное количество попыток.
     * @param dictionary Словарь для получения подсказок.
     * @param placeHolder Символ-заполнитель для скрытых букв.
     * @return Объект GameStatus, представляющий начальное состояние игры.
     */
    GameStatus initializeGame(String secretWord, Integer attempts, academy.model.WordDictionary dictionary, char placeHolder);

    /**
     * @param status Текущее состояние игры.
     * @param user Угаданная буква.
     * @return true, если буква найдена; false, если промах; null, если буква уже была угадана.
     */
    Boolean guessLetter(GameStatus status, char user);

    /**
     * @param status Текущее состояние игры.
     */
    void updateGameState(GameStatus status);

    /**
     * @param status Текущее состояние игры.
     * @param dictionary Словарь для получения текста подсказки.
     * @return Текст подсказки или сообщение об ошибке.
     */
    String useHint(GameStatus status, academy.model.WordDictionary dictionary);
}
