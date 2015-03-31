# CSC_NER
Named entity recognition based on wikipedia.com categories

### Инструкция по запуску
1. Установить необходимые библиотеки:
    *   Python: requests, wikipedia (автоматически тянет за собой beautifulsoup)
    *   Java: lucene-5.0.0. Учтите - используется Java 1.8!

2. Скачать корпус:

Качается скриптом `wiki_crawler.py`. Скрипт запускается командой `python3 wiki_crawler.py 'category name' [-e] [-ru]`.
Аргумент -e полностью стирает предыдущий корпус. Без аргумента категория добавляется к уже существующим. Используется python3.x
Аргумент -ru включает загрузку только русских версий (если таковые имеются).
Имя категории можно писать с пробелами вместо нижних подчёркиваний. Пример запуска: `python3 wiki_crawler.py 'image processing'`

3. Проиндексировать корпус: (тут и далее пока только для английского языка. Скоро появится версия для русского)

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
