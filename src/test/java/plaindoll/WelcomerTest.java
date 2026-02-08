package plaindoll;

import org.junit.Test;
import static org.junit.Assert.*;

public class WelcomerTest {
    private static final int REPLICA_TEST_COUNT = 100;

    @Test
    public void welcomerTest() {
        Welcomer welcomer = new Welcomer();
        assertEquals("Unexpected welcome", "Welcome, good hunter.", welcomer.sayWelcome());
        assertEquals("Unexpected farewell", "Farewell, good hunter.", welcomer.sayFarewell());
        assertEquals("Unexpected hunter call", "hunter!", welcomer.sayHunter());
        assertEquals("Unexpected need gold", "I need more gold.", welcomer.needGold());
        assertEquals("Unexpected farewell", "Farewell, dear hunter.", welcomer.farewell());
        assertEquals("Unexpected yes", "Yes", welcomer.yes());
        assertEquals("Unexpected no", "No", welcomer.no());
    }

    // Новый тест для метода getHunterReplica
    @Test
    public void testGetHunterReplicaContainsHunter() {
        Welcomer welcomer = new Welcomer();
        String replica = welcomer.getHunterReplica();
        
        assertNotNull("Replica should not be null", replica);
        assertTrue("Replica should contain word 'hunter'. Got: " + replica, 
                  replica.toLowerCase().contains("hunter"));
    }
    
    @Test
    public void testGetHunterReplicaMultipleCalls() {
        Welcomer welcomer = new Welcomer();
        
        // Проверяем несколько вызовов
        for (int i = 0; i < 10; i++) {
            String replica = welcomer.getHunterReplica();
            assertNotNull(replica);
            assertTrue(replica.toLowerCase().contains("hunter"));
        }
    }
    
    @Test
    public void testGetHunterReplicaRandomness() {
        Welcomer welcomer = new Welcomer();
        String firstReplica = welcomer.getHunterReplica();
        boolean foundDifferent = false;
        
        // Проверяем несколько вызовов для проверки случайности
        for (int i = 0; i < 100; i++) {
            String currentReplica = welcomer.getHunterReplica();
            if (!currentReplica.equals(firstReplica)) {
                foundDifferent = true;
                break;
            }
        }
        
        assertTrue("Method should return different replicas (randomness check)", foundDifferent);
    }
}
