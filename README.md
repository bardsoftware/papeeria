# CSC articles classification

Прототип, определяющий тему статьи с помощью категорий википедии и векторного поиска.

### Инструкция по запуску
1. [Собрать](https://docs.docker.com/reference/builder/) docker образ из [Dockerfile](https://raw.githubusercontent.com/bardsoftware/papeeria/named_entity_recognition/build/Dockerfile)'a. Затем запустить его в интерактивном режиме (`docker run -it image_id`, также обратите внимание на shared folders в последнем пункте).
2. Скачать корпус с помощью команды `crawl`. Учтите, что название категории должно быть заключено в кавычки. Пример: `crawl 'classification algorithms' -ru`. Дополнительные пояснения к этой и следующим командам можете получить, запустив их с аргументами -h или --help.
3. Построить индекс с помощью команды `classifier`.
4. Можете пользоваться поиском! С помощью того же `classifier`. Наверняка вам захочется использовать pdf-статьи со своего локального диска – в docker для этого предусмотрены [shared folders](https://docs.docker.com/userguide/dockervolumes/).
