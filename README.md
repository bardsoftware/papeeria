papeeria
========

Papeeria is an IDE for your computer science papers 

Article search system for Papeeria

Crawling documents:
-------------------

Class Crawler creates index database for all articles' abstracts from the ACM Computing Surveys (CSUR) journal.
Create search index database: 
./crawl_acm

Searching:
----------

Class Searcher searchs top n documents by the text. Start searching:
./start_searching

This script calls method cos_search(...), which takes 2 arguments: text for searching and number of top words in text, using for searching. You can modify both of them.

Method cos_search() implements searcher using tf-idf for counting top words in the text and cosine similarity to find appropriate documents.
