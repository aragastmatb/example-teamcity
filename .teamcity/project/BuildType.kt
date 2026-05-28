import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.*
import jetbrains.buildServer.configs.kotlin.triggers.*
import jetbrains.buildServer.configs.kotlin.failureConditions.*

/*
 * Полная конфигурация сборки с:
 * - Условным выполнением шагов в зависимости от ветки
 * - Интеграцией с Nexus для деплоя
 * - Настройкой артефактов
 * - Триггерами и условиями неуспеха
 */

object Build : BuildType({
    
    // ==================== БАЗОВЫЕ НАСТРОЙКИ ====================
    
    // Уникальный технический идентификатор
    id = "Build"
    
    // Отображаемое имя в интерфейсе
    name = "Build and Deploy"
    
    // Описание, которое видно в интерфейсе
    description = "Основная сборка проекта: тесты для всех веток, деплой только для master"
    
    // ==================== VCS НАСТРОЙКИ ====================
    
    vcs {
        // Используем VCS root, определённый в проекте
        root(DslContext.settingsRoot)
        
        // Правила проверки: берём код из текущей ветки
        checkoutMode = CheckoutMode.ON_AGENT
        
        // Показывать изменения из всех веток в истории
        showDependenciesChanges = true
    }
    
    // ==================== ПАРАМЕТРЫ СБОРКИ ====================
    
    // Параметры, доступные в шагах сборки через %param_name%
    params {
        // Версия Maven (можно переопределить в UI)
        param("env.MAVEN_VERSION", "3.9.4")
        // Версия Java для сборки
        param("env.JAVA_VERSION", "17")
        // Флаг для условной логики (опционально)
        param("deploy.enabled", "true")
    }
    
    // ==================== ШАГИ СБОРКИ ====================
    
    steps {
        
        // --------------------------------------------------------------------
        // ШАГ 1: Запуск тестов (для ВСЕХ веток, КРОМЕ master)
        // --------------------------------------------------------------------
        step(MavenBuildStep {
            name = "Run Tests (non-master branches)"
            
            // Цели Maven: очистить и запустить тесты
            goals = "clean test"
            
            // Путь к pom.xml относительно корня репозитория
            pomLocation = "pom.xml"
            
            // Использовать Maven, установленный на агенте
            mavenVersion = "default"
            
            // Передаём settings.xml из репозитория для доступа к Nexus
            userSettingsSelection = "teamcity/settings.xml"
            
            // JVM-параметры для Maven-процесса
            jvmArgs = "-Xmx512m"
            
            // ==================== УСЛОВИЕ ВЫПОЛНЕНИЯ ====================
            // Этот шаг выполняется ТОЛЬКО если ветка НЕ равна "master"
            conditions = listOf(
                EqualsCondition(
                    parameter = "teamcity.build.branch",  // Системная переменная TeamCity
                    value = "master",                      // С чем сравниваем
                    invert = true                          // Инвертируем: выполняем если НЕ равно
                )
            )
        })
        
        // --------------------------------------------------------------------
        // ШАГ 2: Деплой в Nexus (ТОЛЬКО для ветки master)
        // --------------------------------------------------------------------
        step(MavenBuildStep {
            name = "Deploy to Nexus (master branch only)"
            
            // Цели Maven: очистить, протестировать и задеплоить артефакт
            goals = "clean deploy"
            
            pomLocation = "pom.xml"
            mavenVersion = "default"
            
            // Критически важно: используем settings.xml с креденшиалами Nexus
            userSettingsSelection = "teamcity/settings.xml"
            
            jvmArgs = "-Xmx512m"
            
            // ==================== УСЛОВИЕ ВЫПОЛНЕНИЯ ====================
            // Этот шаг выполняется ТОЛЬКО если ветка РАВНА "master"
            conditions = listOf(
                EqualsCondition(
                    parameter = "teamcity.build.branch",
                    value = "master",
                    invert = false  // Не инвертируем: выполняем только если равно
                )
            )
        })
    }
    
    // ==================== АРТЕФАКТЫ ====================
    
    /*
     * Правила публикации артефактов:
     * Формат: +:<источник> => <путь в артефактах>
     * 
     * +: — включить файл/папку
     * -: — исключить (можно использовать для фильтрации)
     * ** — рекурсивный поиск
     */
    artifactRules = """
        +:target/*.jar => artifacts/          # Все JAR-файлы из target/
        +:target/*-sources.jar => artifacts/  # Исходники (если собраны)
        +:target/*-javadoc.jar => artifacts/  # JavaDoc (если собраны)
        -:target/*.jar.original               # Исключаем временные файлы
    """.trimIndent()
    
    // ==================== ТРИГГЕРЫ ====================
    
    triggers {
        // Запускать сборку при каждом пуше в репозиторий
        vcs {
            // Минимальная задержка между проверками (в секундах)
            quietPeriodMode = QuietPeriodMode.USE_DEFAULT
            // Не запускать сборку для служебных изменений (опционально)
            branchFilter = "+:*"
        }
    }
    
    // ==================== УСЛОВИЯ НЕУСПЕХА ====================
    
    failureConditions {
        // Сборка считается неудачной, если:
        
        // 1. Любой шаг сборки вернул ошибку
        executionTimeoutMin = 0  // 0 = без лимита
        
        // 2. Тесты упали (по умолчанию включено)
        testFailure = true
        
        // 3. Появились новые ошибки в коде (если подключён анализатор)
        errorMessage = true
        
        // 4. Сборка длилась слишком долго (опционально, в минутах)
        // executionTimeoutMin = 30
    }
    
    // ==================== ДОПОЛНИТЕЛЬНЫЕ НАСТРОЙКИ ====================
    
    // Включить сборку по расписанию (опционально, ночная сборка)
    // schedules {
    //     cronSchedule {
    //         schedulingPolicy = cron {
    //             hours = "2"  // Запускать в 2:00 ночи
    //             timezone = "UTC"
    //         }
    //         branchFilter = "+:refs/heads/master"
    //         withPendingChangesOnly = false
    //     }
    // }
    
    // Метки для успешных сборок (для автоматического деплоя дальше)
    features {
        // Автоматически присваивать метку успешным сборкам master
        feature {
            type = "perfmon"
        }
    }
    
    // Требования к агенту сборки
    requirements {
        // Требуется минимум 2 ГБ памяти на агенте
        equals("teamcity.agent.jvm.maxmemory", "2048", pattern = false)
        // Требуется установленный Maven 3.x
        exists("env.MAVEN_HOME")
    }
})