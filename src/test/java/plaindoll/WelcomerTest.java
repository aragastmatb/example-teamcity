package plaindoll;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import org.junit.Test;

public class WelcomerTest {
	
	private Welcomer welcomer = new Welcomer();

	@Test
	public void welcomerSaysWelcome() {
		assertThat(welcomer.sayWelcome(), containsString("Welcome"));
	}
	@Test
	public void welcomerSaysFarewell() {
		assertThat(welcomer.sayFarewell(), containsString("Farewell"));
	}
	@Test
	public void welcomerSaysHunter() {
		assertThat(welcomer.sayWelcome(), containsString("hunter"));
		assertThat(welcomer.sayFarewell(), containsString("hunter"));
	}
	@Test
	public void welcomerSaysSilver(){
		assertThat(welcomer.sayNeedGold(), containsString("gold"));
	}
	@Test
	public void welcomerSaysSomething(){
		assertThat(welcomer.saySome(), containsString("something"));
	}
	
	// Новый тест для метода getHunterReply - поиск слова hunter в новой реплике
	@Test
	public void welcomerHunterReplyContainsHunter() {
		// Получаем реплику из нового метода
		String hunterReply = welcomer.getHunterReply();
		
		// Проверяем, что реплика содержит слово "hunter" (регистронезависимо)
		assertThat(
			"Новая реплика должна содержать слово 'hunter'", 
			hunterReply.toLowerCase(), 
			containsString("hunter")
		);
	}
}
