papeeria
========

Papeeria is an IDE for your computer science papers 

Article search system for Papeeria

Class crawler creates index database for all articles' abstracts from the ACM Computing Surveys (CSUR) journal.
Create search index database:

\>>> import search  
\>>> c = search.crawler('searchindex.db')  
\>>> c.createindextables()    
\>>> c.crawl("http://dl.acm.org/pub.cfm?id=J204&CFID=376498762&CFTOKEN=48845412")  
  
