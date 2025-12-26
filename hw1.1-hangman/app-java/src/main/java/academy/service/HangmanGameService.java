package academy.service;

import academy.model.GameState;
import academy.model.GameStatus;
import academy.model.WordDictionary;

public class HangmanGameService implements GameEngine {

    @Override
    public GameStatus initializeGame(String secretWord, Integer attempts, WordDictionary dictionary, char placeHolder) {
        if (secretWord == null || secretWord.trim().length() < 2) {
            throw new IllegalArgumentException("Загадываемое слово должно быть корректным.");
        }
        int initialAttempts = (attempts != null && attempts > 0) ? attempts : 6;

        GameStatus status = new GameStatus(secretWord, initialAttempts, placeHolder);
        status.setState(GameState.IN_GAME);

        return status;
    }

    @Override
    public Boolean guessLetter(GameStatus status, char user) {
        char letter = Character.toLowerCase(user);

        if (status.getGuessedLetters().contains(letter)) {
            return null;
        }
        status.getGuessedLetters().add(letter);

        boolean letterFound = false;
        String secretWord = status.getSecretWord();
        StringBuilder displayWord = status.getDisplayWordBuilder();

        for (int i = 0; i < secretWord.length(); i++) {
            if (Character.toLowerCase(secretWord.charAt(i)) == letter) {
                displayWord.setCharAt(i * 2, user);
                letterFound = true;
            }
        }

        if (!letterFound) {
            status.decrementAttempts();
        }

        updateGameState(status);
        return letterFound;
    }

    @Override
    public void updateGameState(GameStatus status) {
        if (status.getUserWord().equals(status.getSecretWord())) {
            status.setState(GameState.WON);
        } else if (status.getRemainingAttempts() <= 0) {
            status.setState(GameState.LOST);
        } else {
            status.setState(GameState.IN_GAME);
        }
    }

    @Override
    public String useHint(GameStatus status, WordDictionary dictionary) {
        if (dictionary == null) {
            return "Подсказки недоступны.";
        }
        if (status.isHintUsed()) {
            return "Подсказка уже была использована!";
        }
        String hint = dictionary.getHint(status.getSecretWord());

        if (hint != null) {
            status.setHintUsed(true);
            return "Подсказка: " + hint;
        } else {
            return "К сожалению, для этого слова нет подсказки";
        }
    }
}
