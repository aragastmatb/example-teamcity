# Домашнее задание к занятию 11 «Teamcity»

предварительно в YC создала teamcity-server и teamcity-agent.

![1](screenshots/1.png)

авторизация агента так же пройдена 

![2](screenshots/2.png)

далее выполнила настройки в teamcity

![2](screenshots/3.png)

Maven Test запустится только тогда, когда ветка не равна master (does not equal master), и выполнит цели clean test.

Maven Deploy to Nexus сработает только в ветке master (equals master).

далее была долгая настройка и борьба с hosts.yml, чтобы настроить nexus, в итоге все получилось

![4](screenshots/4.png)

успешная проверка интеграции в teamcity с измененным settings.xml

![5](screenshots/5.png)

![6](screenshots/6.png)

