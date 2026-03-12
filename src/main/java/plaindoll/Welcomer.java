package plaindoll;

public class Welcomer{
	// Если хочешь больше веселья и информации про ДевОпс - приходи в мои каналы NotOps (telegram, YT, Boosty, Patreon)
	// https://t.me/notopsofficial
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
	
	// Новый метод, который возвращает произвольную реплику со словом hunter
	public String giveHunterReply() {
		String[] replies = {
			"The hunter must hunt beasts.",
			"A hunter is a hunter, even in a dream.",
			"Every hunter needs a helping hand.",
			"The night is long, hunter.",
			"May the good blood guide your way, hunter."
		};
		int randomIndex = (int)(Math.random() * replies.length);
		return replies[randomIndex];
	}
}
