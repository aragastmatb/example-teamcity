package com.example;

/**
 * Простой класс для демонстрации CI/CD пайплайна.
 * 
 * Используется в учебном задании по настройке TeamCity с:
 * - условными шагами сборки (тесты / деплой)
 * - интеграцией с Nexus для хранения артефактов
 * - версионированием конфигурации через Kotlin DSL
 */
public class Welcomer {
    
    /**
     * Возвращает стандартное приветствие.
     * @return "Hello, World!"
     */
    public String sayHello() {
        return "Hello, World!";
    }
    
    /**
     * Персонализированное приветствие.
     * @param name имя пользователя
     * @return "Hello, {name}!" или стандартное, если имя пустое
     */
    public String sayHelloTo(String name) {
        if (name == null || name.trim().isEmpty()) {
            return sayHello();
        }
        return "Hello, " + name.trim() + "!";
    }
    
    // ========================================================================
    // ✅ НОВЫЙ МЕТОД: добавлен в ветке feature/add_reply
    // Требование задания: вернуть строку, содержащую слово "hunter"
    // ========================================================================
    
    /**
     * Возвращает реплику для "охотника".
     * 
     * Этот метод был добавлен в рамках домашнего задания:
     * 1. Создана ветка feature/add_reply
     * 2. Реализован метод с требуемой логикой
     * 3. Написан тест на проверку наличия слова "hunter"
     * 4. Изменения слиты в master через Merge Request
     * 
     * @return строка, гарантированно содержащая "hunter" (регистронезависимо)
     */
    public String getHunterReply() {
        /*
         * Возвращаем детерминированную строку для стабильного тестирования.
         * В реальном проекте здесь могла бы быть:
         * - случайная выборка из списка реплик
         * - локализация под язык пользователя
         * - контекстная генерация на основе состояния игры
         */
        return "Greetings, brave hunter! Your quest awaits in the northern woods.";
    }
    
    /**
     * Точка входа для демонстрации работы класса.
     * Запуск: java -jar target/example-teamcity-*.jar
     */
    public static void main(String[] args) {
        Welcomer w = new Welcomer();
        System.out.println(w.sayHello());
        System.out.println(w.sayHelloTo("TeamCity"));
        System.out.println(w.getHunterReply());  // Новый метод
    }
}