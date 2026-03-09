package plaindoll;

import java.util.Random;

public class Welcomer{
	// Если хочешь больше веселья и информации про ДевОпс - приходи в мои каналы NotOps (telegram, YT, Boosty, Patreon)
	// https://t.me/notopsofficial

	private Random random = new Random();

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

	// Новый метод: возвращает случайную фразу со словом hunter
	public String sayRandomHunterPhrase() {
		String[] phrases = {
				"The hunter must hunt.",
				"A hunter is never alone.",
				"The hunter's dream is eternal.",
				"This hunter's mark is your own.",
				"Every hunter fears the night."
		};

		int index = random.nextInt(phrases.length);
		return phrases[index];
	}
}