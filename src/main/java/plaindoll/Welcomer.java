package plaindoll;

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
	
	// Новый метод: возвращает произвольную реплику со словом "hunter"
	public String getHunterReply() {
		// Несколько вариантов реплик на выбор
		String[] hunterReplies = {
			"A hunter must hunt, even in a dream",
			"The night is dark and full of hunters",
			"Every hunter needs a trusted weapon",
			"A skilled hunter knows when to be patient",
			"Hunter's mark reveals the hidden path",
			"The old hunter shared his wisdom with the new",
			"In this forest, every shadow could be a hunter",
			"The hunter tracked his prey through moonlit woods",
			"A true hunter respects the balance of nature",
			"The hunter's journey is never truly complete"
		};
		
		// Выбираем случайную реплику
		int randomIndex = (int) (Math.random() * hunterReplies.length);
		return hunterReplies[randomIndex];
	}
}
