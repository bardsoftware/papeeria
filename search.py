import urllib2
from BeautifulSoup import *
from urlparse import urljoin
from pysqlite2 import dbapi2 as sqlite

ignorewords = set(['the', 'of', 'to', 'and', 'a', 'in', 'is', 'it'])




class crawler:

  def __init__(self, dbname):
    self.con = sqlite.connect(dbname)

  def __del__(self):
    self.con.close()

  def dbcommit(self):
    self.con.commit()
  
  def isindexed(self, url):
    u = self.con.execute(
    "select rowid from urllist where url = '%s'" % url).fetchone()
    if u != None:
      v = self.con.execute(
      "select * from wordlocation where urlid = '%d'" % u[0]).fetchone()
      if v != None: return True
    return False


  def addlinkref(self, urlForm, urlTo, linkText):
    pass


  def getentryid(self, table, field, value, createnew = True):
    cur = self.con.execute(
      "select rowid from %s where %s = '%s'" % (table, field, value))
    res = cur.fetchone()
    if res == None:
      cur = self.con.execute(
      "insert into %s (%s) values ('%s')" % (table, field, value))
      return cur.lastrowid
    else:
      return res[0]


  def get_text_only(self, soup):
    v = soup.string
    if v == None:
      c = soup.contents
      resulttext = ''
      for t in c:
        sudtext = self.get_text_only(t)
        resulttext += sudtext + '\n'
      return resulttext
    else:
      return v.strip()
      

  def separatewords(self, text):
    splitter = re.compile('\\W*')
    return [s.lower() for s in splitter.split(text) if s != '']
 

  def addtoindex(self, url, text):
    if self.isindexed(url): return
    print 'Indexing %s' % url
    
    words = self.separatewords(text)
    urlid = self.getentryid('urllist', 'url', url)
    
    for i in range(len(words)):
      word = words[i]
      if word in ignorewords: continue
      wordid = self.getentryid('wordlist', 'word', word)
      self.con.execute("insert into wordlocation(urlid, wordid, location) values (%d, %d, %d)" % (urlid, wordid, i))     


  def open_tab(self, url, tab_name):
    #tab_name = "tab_about"
    req = urllib2.Request(url, headers={'User-Agent' : "Magic Browser"})
    try: 
      con = urllib2.urlopen(req)
    except:
      print "I can't open %s" % url
      return
    soup = BeautifulSoup(con.read())
    scripts = soup.findAll("script")
    m = re.compile("'bindExpr\\\\':\[\\\\'[^']*\\\\'\]")
    for tag in scripts:
      if (len(tag.contents) > 0): 
        script_content = str(tag.contents)
        #print script_content
        #print "======another string========\n"
        matches = m.finditer(script_content)
        for expr in matches:
          expr_str = expr.group(0)
          #print expr_str
          if tab_name in expr_str:
            begin_ind = expr_str.find(tab_name)
            #print expr_str
            link = expr_str[begin_ind:(len(expr_str) - 3)]
            link = link.replace(";", "")
            #print link
            return link


  def get_list_of_links(self, url):
    req = urllib2.Request(url, headers={'User-Agent' : "Magic Browser"}) 
    try: 
      con = urllib2.urlopen(req)
    except:
      print "I can't open %s" % url
      return
    soup = BeautifulSoup(con.read())
    links = soup('a')
    return links


  def get_abstract_text(self, url):
    req = urllib2.Request(url, headers={'User-Agent' : "Magic Browser"}) 
    try: 
      con = urllib2.urlopen(req)
    except:
      print "I can't open %s" % url
      return
    soup = BeautifulSoup(con.read())
    text = self.get_text_only(soup)
    #print text
    return text
    

  def crawl(self, journal_url, depth = 2):
    base = "http://dl.acm.org/"
    link = self.open_tab(journal_url, "pub_series")
    if link == None:
      return
    #print link
    archive_url = base + link
    #print archive_url
    links = self.get_list_of_links(archive_url)
    if links == None:
      return

    for link in links:
      print "Link of Journal: " + base + link['href']
      list_vol = self.open_tab(base + link['href'], "tab_about")
      #print "Getting list: " + base + list_vol
      list_of_papers = self.get_list_of_links(base + list_vol)
      for paper in list_of_papers:
        if (len(dict(paper.attrs)) == 1):
          #print "Paper: " + base + paper['href']
          paper_abstract = self.open_tab(base + paper['href'], "tab_abstract")
          text = self.get_abstract_text(base + paper_abstract)
          self.addtoindex(base + paper['href'], text)
          #print "====== TEXT ======"
          #print text
    


  def createindextables(self):
    self.con.execute('create table urllist(url)')
    self.con.execute('create table wordlist(word)')
    self.con.execute('create table wordlocation(urlid, wordid, location)')
    self.con.execute('create table link(fromid integer, toid integer)')
    self.con.execute('create table linkwords(wordid, linkid)')
    self.con.execute('create index wordidx on wordlist(word)')
    self.con.execute('create index urlidx on urllist(url)')
    self.con.execute('create index wordurlidx on wordlocation(wordid)')
    self.con.execute('create index urltoidx on link(toid)')
    self.con.execute('create index urlfromidx on link(fromid)')
    self.dbcommit()


