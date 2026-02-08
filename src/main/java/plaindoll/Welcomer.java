package plaindoll;

import java.util.Random;

public class Welcomer {
    private static final String[] HUNTER_REPLICAS = {
        "The hunter became the hunted in this thrilling chase.",
        "Every good hunter knows patience is the key to success.",
        "In the forest, the hunter must respect nature's balance.",
        "The hunter's moon shone brightly over the silent woods.",
        "A skilled hunter tracks more than just footprints.",
        "The hunter prepared his gear for the morning expedition.",
        "Legend speaks of a hunter who could talk to animals.",
        "The hunter's instinct told him danger was nearby.",
        "As a bounty hunter, she always got her target.",
        "The hunter's code forbids killing for sport alone."
    };
    
    private final Random random = new Random();
    
    /**
     * Новый метод для задания 10
     * Возвращает произвольную реплику, содержащую слово "hunter"
     * @return строка с репликой про охотника
     */
    public String getHunterReplica() {
        int index = random.nextInt(HUNTER_REPLICAS.length);
        return HUNTER_REPLICAS[index];
    }
    
    // Существующие методы остаются ниже
    public String welcome(String name) {
        return "Welcome, " + name + "!";
    }
    
    public String farewell() {
        return "Farewell, dear hunter.";
    }
    
    public String needGold() {
        return "I need more gold.";
    }
    
    public String sayFarewell() {
        return "Farewell, good hunter.";
    }
    
    public String sayWelcome() {
        return "Welcome, good hunter.";
    }
    
    public String sayHunter() {
        return "hunter!";
    }
    
    public String no() {
        return "No";
    }
    
    public String yes() {
        return "Yes";
    }
}
