papeeria
========

Papeeria is an IDE for your computer science papers 

Article search system for Papeeria


Very simple example from Toby Segaran's book.  
Class crawler creates search index for pages from http://segaran.com/wiki/

Create search index database:

\>>> import search  
\>>> c = search.crawler('searchindex.db')  
\>>> c.createindextables()  
\>>> list = ['http://segaran.com/wiki/Categorical_list_of_programming_languages.html']  
\>>> c.crawl(list)  
  
The result database is available: http://segaran.com/db/searchindex.db
