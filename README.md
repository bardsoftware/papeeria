# CSC simple topic modeling

Прототип, определяющий тему статьи с помощью категорий википедии и векторного поиска.

### Инструкция по запуску
1. [Собрать](https://docs.docker.com/reference/builder/) docker образ из [Dockerfile](https://raw.githubusercontent.com/bardsoftware/papeeria/named_entity_recognition/build/Dockerfile)'a. Затем запустить его в интерактивном режиме (`docker run -it image_id`, также обратите внимание на shared folders в последнем пункте).
2. Собрать корпус и индекс командой `build`
3. Запускаем сервер! `startserver`
4. Отправляем ему pdf-файл: `curl --data-binary @path_to_pdf ip:port`

