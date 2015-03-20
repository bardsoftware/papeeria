# CSC_NER
Named entity recognition based on wikipedia.com categories

### Инструкция по запуску
1. Установить необходимые библиотеки:
    *   Python: mwclient, mwparserfromhell
    *   Java: lucene-5.0.0

2. Скачать корпус:

Качается скриптом `wiki_crawler.py`. Скрипт запускается командой `python wiki_crawler.py 'category name' [-e]`.
Аргумент -e полностью стирает предыдущий корпус. Без аргумента категория добавляется к уже существующим. Используется python2.x
Имя категории можно писать с пробелами вместо нижних подчёркиваний. Пример запуска: `python wiki_crawler.py 'image processing'`

3. Проиндексировать корпус:

`java org.apache.lucene.demo.IndexFiles [-index INDEX_PATH] [-docs DOCS_PATH] [-update]`

 Пример:

 `java org.apache.lucene.demo.IndexFiles -index index -docs corpus`

4. Поиск:

`java org.ner.SearchFiles [-index dir] [-queries file]`

Пример:

`java org.ner.SearchFiles -index index -queries query`

Команда вернет список пар "категория - вес", отсортированный по весам.

Например, при запросе в виде кусочка статьи про Apache Cassandra и при корпусе вида `{big data, databases, image_processing, statistics}` результатом будет:

    377 total matching documents
    databases : 3.017130
    big_data : 0.624801
    image_processing : 0.478265
    statistics : 0.074634