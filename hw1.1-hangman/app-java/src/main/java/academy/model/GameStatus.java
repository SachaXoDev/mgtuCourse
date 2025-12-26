package academy.model;

import java.util.Set;

public class GameStatus {
    private final String secretWord;
    private StringBuilder displayWord;
    private int remainingAttempts;
    private Set<Character> guessedLetters;
    private boolean hintUsed;
    private GameState state;
    private final char placeHolder;

    public GameStatus(String secretWord, int initialAttempts, char placeHolder) {
        this.secretWord = secretWord.toLowerCase();
        this.remainingAttempts = initialAttempts;
        this.guessedLetters = new java.util.HashSet<>();
        this.placeHolder = placeHolder;
        this.hintUsed = false;
        this.state = GameState.INITIALIZING;
        initializeDisplayWord();
    }

    private void initializeDisplayWord() {
        this.displayWord = new StringBuilder();
        for (int i = 0; i < secretWord.length(); i++) {
            displayWord.append(placeHolder);
            if (i < secretWord.length() - 1) {
                displayWord.append(" ");
            }
        }
    }

    public String getSecretWord() {
        return secretWord;
    }

    public StringBuilder getDisplayWordBuilder() {
        return displayWord;
    }

    public String getDisplayWord() {
        return displayWord.toString();
    }

    public String getUserWord() {
        return displayWord.toString().replace(" ", "");
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public void decrementAttempts() {
        remainingAttempts--;
    }

    public Set<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public boolean isHintUsed() {
        return hintUsed;
    }

    public void setHintUsed(boolean hintUsed) {
        this.hintUsed = hintUsed;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }
}
