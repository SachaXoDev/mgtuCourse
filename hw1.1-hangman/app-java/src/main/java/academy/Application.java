package academy;

import academy.model.AppConfig;
import academy.model.DictionaryConfig;
import academy.model.WordDictionary;
import academy.service.GameEngine;
import academy.service.HangmanGameService;
import academy.service.StaticDictionaryLoader;
import academy.UI.ConsoleUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.File;
import java.util.function.Predicate;
import static java.util.Objects.nonNull;

@Command(name = "Application Example", version = "Example 1.0", mixinStandardHelpOptions = true)
public class Application implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final Predicate<String[]> IS_TESTING_MODE = words -> nonNull(words) && words.length == 2;

    private final GameEngine gameEngine = new HangmanGameService();

    @Option(names = {"-s", "--font-size"}, description = "Font size")
    int fontSize;

    @Parameters(paramLabel = "<word>", description = "Words pair for testing mode")
    private String[] words;

    @Option(names = {"-c", "--config"}, description = "Path to YAML config file. This option is currently ignored for interactive mode.")
    private File configPath;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        AppConfig config = loadConfig();

        if (IS_TESTING_MODE.test(config.words())) {
            runTestMode(config);
        } else {
            WordDictionary dictionary = new WordDictionary(
                config.dictionary().getWords(),
                config.dictionary().getHints()
            );
            ConsoleUI ui = new ConsoleUI(gameEngine);
            ui.runInteractiveMode(dictionary);
        }
    }

    private void runTestMode(AppConfig config) {
        String word = config.words()[0];
        String userInput = config.words()[1];

        academy.model.GameStatus gameStatus = gameEngine.initializeGame(word, 6, null, '*');

        for (char guess : userInput.toCharArray()) {
            gameEngine.guessLetter(gameStatus, guess);
        }

        String result = gameStatus.getUserWord() + ";";
        result += gameStatus.getSecretWord().equals(gameStatus.getUserWord()) ? "POS" : "NEG";
        System.out.println(result);
    }

    private AppConfig loadConfig() {
        if (IS_TESTING_MODE.test(words)) {
            return new AppConfig(fontSize, words, null);
        }

        if (configPath != null) {
            LOGGER.warn("Аргумент -c/--config проигнорирован. В интерактивном режиме используется встроенный словарь.");
        }

        DictionaryConfig dictionary = StaticDictionaryLoader.loadDefaultDictionary();
        return new AppConfig(fontSize, words, dictionary);
    }
}
