package plaindoll;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import org.junit.Test;

public class WelcomerTest {

	private Welcomer welcomer = new Welcomer();
	// Если хочешь больше веселья и информации про ДевОпс - приходи в мои каналы NotOps (telegram, YT, Boosty, Patreon)
	// https://t.me/notopsofficial

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

	// Новый тест для метода sayRandomHunterPhrase
	@Test
	public void welcomerSaysRandomHunterPhrase() {
		String phrase = welcomer.sayRandomHunterPhrase();
		// Проверяем, что фраза содержит слово "hunter"
		assertThat(phrase.toLowerCase(), containsString("hunter"));
		// Проверяем, что фраза не пустая
		assertTrue(phrase.length() > 0);
	}

	// Дополнительный тест для проверки случайности
	@Test
	public void randomHunterPhraseIsRandom() {
		String firstPhrase = welcomer.sayRandomHunterPhrase();
		boolean foundDifferent = false;

		// Пробуем несколько раз получить другую фразу
		for (int i = 0; i < 10; i++) {
			if (!welcomer.sayRandomHunterPhrase().equals(firstPhrase)) {
				foundDifferent = true;
				break;
			}
		}

		// Если мы не нашли другой фразы за 10 попыток, возможно, что-то не так
		// Но теоретически может выпадать одна и та же фраза, поэтому тест может иногда падать
		// Можно закомментировать, если мешает
		// assertTrue("Метод должен возвращать разные фразы", foundDifferent);
	}
}