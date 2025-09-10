package plaindoll;
import java.util.Random;
public class Welcomer{

	public String sayWelcome() {
		return "Welcome home, good hunter. What is it your desire?";
	}
	public String sayFarewell() {
		return "Farewell, good hunter. May you find your worth in waking world.";
	}
	public String sayNeedGold(){
		return "Not enough gold";
	}
	public String saySome(){
		return "something in the way";
	}
	public String hunterPhrase() {
		Random random = new Random();
        String[] hunterPhrases = {
            "The hunter becomes the hunted.",
            "A skilled hunter always respects their prey.",
            "In the forest, every hunter has a story to tell.",
            "The hunter's moon guides our way tonight.",
            "Only the most patient hunter catches the prize.",
            "A true hunter knows when to wait and when to strike.",
            "The hunter's instinct never fails in the wild.",
            "Every hunter carries the wisdom of generations.",
            "The hunter's path is solitary but rewarding.",
            "A hunter must understand nature to succeed."
        };
        
        return hunterPhrases[random.nextInt(hunterPhrases.length)];
    }
}
