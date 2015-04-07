# CSC_NER

### Инструкция по запуску
1. [Собрать](https://docs.docker.com/reference/builder/) docker образ из [Dockerfile](https://raw.githubusercontent.com/bardsoftware/papeeria/named_entity_recognition/build/Dockerfile)'a.
2. Скачать корпус с помощью команды `crawl`. Дополнительные пояснения к этой и следующим командам можете получить, запустив их с аргументами -h или --help.
3. Построить индекс с помощью команды `classificator`.
4. Можете пользоваться поиском! С помощью того же `classificator`. Наверняка вам захочется использовать pdf-статьи со своего локального диска – в docker для этого предусмотрены [shared folders](https://docs.docker.com/userguide/dockervolumes/).
