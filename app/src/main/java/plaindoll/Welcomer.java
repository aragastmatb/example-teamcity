package plaindoll;

/**
 * Учебный класс для демонстрации CI/CD в TeamCity.
 * 
 * Задание: добавить метод, возвращающий строку со словом "hunter".
 */
public class Welcomer {
    
    /**
     * Приветствие охотника.
     */
    public String sayWelcome() {
        return "Welcome home, good hunter. What is it your desire?";
    }
    
    /**
     * Прощание с охотником.
     */
    public String sayFarewell() {
        return "Farewell, good hunter. May you find your worth in waking world.";
    }
    
    /**
     * Сообщение о нехватке золота.
     */
    public String sayNeedGold() {
        return "Not enough gold";
    }
    
    /**
     * Произвольная реплика.
     */
    public String saySome() {
        return "something in the way";
    }
    
    /**
     * ✅ НОВЫЙ МЕТОД: добавлен в ветке feature/add_reply
     * Требование: вернуть строку, содержащую слово "hunter"
     */
    public String sayHunterReply() {
        return "Greetings, brave hunter! Your quest awaits in the northern woods.";
    }
}