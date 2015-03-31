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

3. Проиндексировать корпус:

    Внимание - используйте аргумент [-ru], когда индексируете русскоязычный корпус!

    `java org.apache.lucene.demo.IndexFiles [-index INDEX_PATH] [-docs DOCS_PATH] [-update] [-ru]`

     Пример:

     `java org.apache.lucene.demo.IndexFiles -index index -docs corpus -ru`

4. Поиск:

    Аргумент -pdf включает поиск по pdf-статьям, лежащим в директории queries
    Внимание - используйте аргумент [-ru], когда ищете по русскоязычному индексу!

    `java org.ner.SearchFiles [-index dir] [-queries queries] [-ru] [-pdf]`

    Пример:

    `java org.ner.SearchFiles -index pdf -queries pdf -pdf`

    Команда вернет список пар "категория - вес", отсортированный по весам.

    Например, при запросе в виде кусочка статьи про Apache Cassandra и при корпусе вида `{big data, databases, image_processing, statistics}` результатом будет:

        377 total matching documents
        databases : 3.017130
        big_data : 0.624801
        image_processing : 0.478265
        statistics : 0.074634
