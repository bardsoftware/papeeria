# Copyright 2013 Elizabeth Shashkova
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import urllib2
from BeautifulSoup import *
from pysqlite2 import dbapi2 as sqlite

ignore_words = set(['the', 'of', 'to', 'and', 'a', 'in', 'is', 'it'])


class Crawler:
    def __init__(self, db_name):
        self.con = sqlite.connect(db_name)

    def __del__(self):
        self.con.close()

    def db_commit(self):
        self.con.commit()

    def is_indexed(self, url):
        """ Check if url is already exist in database.
        That means this page is already indexed"""
        u = self.con.execute(
            "select rowid from url_list where url = '%s'" % url).fetchone()
        if u is not None:
            v = self.con.execute(
                "select * from word_location where url_id = '%d'" % u[0]).fetchone()
            if v is not None:
                return True
        return False

    def add_link_ref(self, url_from, url_to, link_text):
        pass

    def get_entry_id(self, table, field, value, create_new=True):
        """ Return id of row in table if this row exists
        Else create this row and return id"""
        cur = self.con.execute(
            "select rowid from %s where %s = '%s'" % (table, field, value))
        res = cur.fetchone()
        if res is None:
            cur = self.con.execute(
                "insert into %s (%s) values ('%s')" % (table, field, value))
            return cur.lastrowid
        else:
            return res[0]

    def get_text_only(self, soup):
        """ Return text from Soup of page"""
        v = soup.string
        if v is None:
            c = soup.contents
            result_text = ''
            for t in c:
                sud_text = self.get_text_only(t)
                result_text += sud_text + '\n'
            return result_text
        else:
            return v.strip()

    def separate_words(self, text):
        """ Separate words in text"""
        splitter = re.compile('\\W*')
        return [s.lower() for s in splitter.split(text) if s != '']

    def add_to_index(self, url, text):
        """ Add all words from text (from url) to database.
        This url becomes indexed """
        if self.is_indexed(url):
            return
        print 'Indexing %s' % url

        words = self.separate_words(text)
        url_id = self.get_entry_id('url_list', 'url', url)

        for i in range(len(words)):
            word = words[i]
            if word in ignore_words: continue
            word_id = self.get_entry_id('word_list', 'word', word)
            self.con.execute(
                "insert into word_location(url_id, word_id, location) values (%d, %d, %d)" % (url_id, word_id, i))

    def open_tab(self, url, tab_name):
        """ Return link to get content from tab in ACM Library menu.
        tab_name is a name of tab"""
        req = urllib2.Request(url, headers={'User-Agent': "Magic Browser"})
        try:
            con = urllib2.urlopen(req)
        except:
            print "I can't open %s" % url
            return
        soup = BeautifulSoup(con.read())
        scripts = soup.findAll("script")
        m = re.compile("'bindExpr\\\\':\[\\\\'[^']*\\\\'\]")
        for tag in scripts:
            if len(tag.contents) > 0:
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
        """ Return all links from url """
        req = urllib2.Request(url, headers={'User-Agent': "Magic Browser"})
        try:
            con = urllib2.urlopen(req)
        except:
            print "I can't open %s" % url
            return
        soup = BeautifulSoup(con.read())
        links = soup('a')
        return links

    def get_abstract_text(self, url):
        """ Return text of article's abstract"""
        req = urllib2.Request(url, headers={'User-Agent': "Magic Browser"})
        try:
            con = urllib2.urlopen(req)
        except:
            print "I can't open %s" % url
            return
        soup = BeautifulSoup(con.read())
        text = self.get_text_only(soup)
        #print text
        return text

    def crawl(self, journal_url, depth=2):
        """ Begin crawling journal in ACM Library """
        base = "http://dl.acm.org/"
        link = self.open_tab(journal_url, "pub_series")
        if link is None:
            return
        archive_url = base + link
        links = self.get_list_of_links(archive_url)
        if links is None:
            return

        for link in links:
            print "Journal link: " + base + link['href']
            list_vol = self.open_tab(base + link['href'], "tab_about")
            list_of_papers = self.get_list_of_links(base + list_vol)
            for paper in list_of_papers:
                if len(dict(paper.attrs)) == 1:
                    paper_abstract = self.open_tab(base + paper['href'], "tab_abstract")
                    text = self.get_abstract_text(base + paper_abstract)
                    self.add_to_index(base + paper['href'], text)

    def create_index_tables(self):
        """ Create database tables """
        self.con.execute('create table url_list(url)')
        self.con.execute('create table word_list(word)')
        self.con.execute('create table word_location(url_id, word_id, location)')
        self.con.execute('create table link(from_id integer, to_id integer)')
        self.con.execute('create table link_words(word_id, link_id)')
        self.con.execute('create index word_idx on word_list(word)')
        self.con.execute('create index url_idx on url_list(url)')
        self.con.execute('create index word_url_idx on word_location(word_id)')
        self.con.execute('create index url_to_idx on link(to_id)')
        self.con.execute('create index url_from_idx on link(from_id)')
        self.db_commit()


