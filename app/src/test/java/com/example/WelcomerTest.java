package com.example;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Модульные тесты для класса Welcomer.
 * 
 * Запускаются: 
 * - локально: mvn test
 * - в TeamCity: автоматически при push в репозиторий
 * 
 * Результаты парсятся TeamCity через junit-report plugin.
 */
public class WelcomerTest {
    
    @Test
    public void testSayHello() {
        Welcomer w = new Welcomer();
        assertEquals("Hello, World!", w.sayHello());
    }
    
    @Test
    public void testSayHelloTo_withName() {
        Welcomer w = new Welcomer();
        assertEquals("Hello, Alice!", w.sayHelloTo("Alice"));
    }
    
    @Test
    public void testSayHelloTo_withEmptyName() {
        Welcomer w = new Welcomer();
        assertEquals("Hello, World!", w.sayHelloTo(null));
        assertEquals("Hello, World!", w.sayHelloTo(""));
        assertEquals("Hello, World!", w.sayHelloTo("   "));
    }
    
    // ========================================================================
    // ✅ НОВЫЙ ТЕСТ: для метода getHunterReply()
    // Добавлен в ветке feature/add_reply согласно заданию
    // ========================================================================
    
    /**
     * Проверяет, что getHunterReply() возвращает строку со словом "hunter".
     * 
     * Требования задания:
     * 1. Метод возвращает произвольную реплику
     * 2. Реплика должна содержать слово "hunter"
     * 
     * Реализация теста:
     * - Проверка на null/пустоту (защита от ошибок)
     * - Проверка наличия подстроки "hunter" в любом регистре
     * - Информативное сообщение об ошибке при падении для быстрой отладки
     */
    @Test
    public void testHunterReplyContainsHunterWord() {
        // Arrange: подготовка тестовых данных
        Welcomer welcomer = new Welcomer();
        
        // Act: выполнение тестируемого кода
        String reply = welcomer.getHunterReply();
        
        // Assert 1: базовая валидация результата
        assertNotNull("Reply must not be null", reply);
        assertFalse("Reply must not be empty", reply.isEmpty());
        
        // Assert 2: 🔑 ключевое требование — наличие слова "hunter"
        // Используем toLowerCase() для регистронезависимой проверки
        String lower = reply.toLowerCase();
        assertTrue(
            "Reply should contain 'hunter' (case-insensitive), but was: '" + reply + "'",
            lower.contains("hunter")
        );
    }
    
    /**
     * Дополнительный тест: проверяет "осмысленность" реплики.
     * Опциональный, но демонстрирует лучшие практики тестирования.
     */
    @Test
    public void testHunterReplyIsMeaningful() {
        Welcomer w = new Welcomer();
        String reply = w.getHunterReply();
        
        // Минимальная длина (защита от "hunter" как единственного слова)
        assertTrue("Reply too short", reply.length() >= 20);
        
        // Заканчивается знаком препинания (признак законченной фразы)
        char last = reply.charAt(reply.length() - 1);
        assertTrue("Should end with punctuation", 
            last == '.' || last == '!' || last == '?' || last == ',');
    }
}