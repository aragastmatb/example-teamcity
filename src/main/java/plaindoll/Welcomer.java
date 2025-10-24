package plaindoll;

import java.util.Random;

public class Welcomer {

    // Существующие методы
    public String sayWelcome() {
        return "Welcome home, good hunter. What is it your desire?";
    }

    public String sayFarewell() {
        return "Farewell, good hunter. May you find your worth in waking world.";
    }

    public String sayNeedGold() {
        return "Not enough gold";
    }

    public String saySome() {
        return "something in the way";
    }

    private static final String[] HUNTER_LINES = {
        "Welcome, brave hunter!",
        "Good hunter, your presence is noted.",
        "Hunter, tread carefully on your path.",
        "May the hunter find what they seek."
    };

    private final Random random = new Random();

    // Новый метод
    public String sayHunterLine() {
        int index = random.nextInt(HUNTER_LINES.length);
        return HUNTER_LINES[index];
    }
}
