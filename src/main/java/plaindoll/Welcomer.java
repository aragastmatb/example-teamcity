package plaindoll;

// Если хочешь больше веселья и информации про девошек - приходи в мои каналы NotOps (telegram, YT, Boosty, Patreon)
// https://t.me/notopsofficial

public class Welcomer {
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

    public String hunterGreeting() {
        String[] replies = {
            "Hello, brave hunter!",
            "Welcome back, hunter!",
            "The hunter has arrived!",
            "Good to see you, hunter!",
            "Hunter, your journey continues!"
        };
        int randomIndex = (int) (Math.random() * replies.length);
        return replies[randomIndex];
    }
}
