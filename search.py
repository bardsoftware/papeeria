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
import math



class Crawler:
    ABSTRACT_TAB_NAME = "tab_abstract"
    TABLE_OF_CONTENTS_TAB_NAME = "tab_about"
    ARCHIVE_TAB_NAME = "pub_series"
    BASE = "http://dl.acm.org/"
    IS_PAPER_LINK = "citation"
    ABS_NOT_AVAILABLE = "An abstract is not available."

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
            "select * from word_location join url_list on "
            "url_list.rowid = word_location.url_id where url = '%s'" % url).fetchone()
        if u is not None:
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

    def add_to_index(self, url, text, title, count):
        """ Add all words from text (from url) to database.
        This url becomes indexed """
        if self.is_indexed(url):
            return
        print '%4d Indexing %s' % (count, url)

        if (title is None) and (text is None):
            print "Neither text nor title are available"
            return

        words = []
        if title is not None:
            #print title
            words = self.separate_words(title)

        if (len(text) < 50) and (self.ABS_NOT_AVAILABLE in text):
            print self.ABS_NOT_AVAILABLE
        else:
            words_from_abstract = self.separate_words(text)
            for word in words_from_abstract:
                words.append(word)

        url_id = self.get_entry_id('url_list', 'url', url)

        for i in range(len(words)):
            word = words[i]
            word_id = self.get_entry_id('word_list', 'word', word)
            #print word_id
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

    def get_title(self, url):
        req = urllib2.Request(url, headers={'User-Agent': "Magic Browser"})
        try:
            con = urllib2.urlopen(req)
        except:
            print "I can't get title of: %s" % url
            return
        soup = BeautifulSoup(con.read())
        title = soup.title.string
        return title

    def get_abstract_text(self, url):
        """ Return text of article's abstract"""
        req = urllib2.Request(url, headers={'User-Agent': "Magic Browser"})
        try:
            con = urllib2.urlopen(req)
        except:
            print "I can't open abstract: %s" % url
            return
        soup = BeautifulSoup(con.read())
        text = self.get_text_only(soup)
        return text

    def delete_user_info(self, url):
        """Delete user info from url"""
        ind = url.find('&')
        new_url = url[0: ind]
        return new_url

    def crawl(self, journal_url, depth=2):
        """ Begin crawling journal in ACM Library """

        link = self.open_tab(journal_url, self.ARCHIVE_TAB_NAME)
        if link is None:
            return
        archive_url = self.BASE + link
        links = self.get_list_of_links(archive_url)
        if links is None:
            return

        count = 1
        for link in links:
            #DEBUG
            if count > 5:
                break
            ref = self.delete_user_info(link['href'])
            print "=============="
            print " Journal link: " + self.BASE + ref
            print "=============="
            list_vol = self.open_tab(self.BASE + ref, self.TABLE_OF_CONTENTS_TAB_NAME)
            list_of_papers = self.get_list_of_links(self.BASE + list_vol)

            for paper in list_of_papers:
                #DEBUG
                if count > 5:
                    break
                paper_ref = self.delete_user_info(paper['href'])
                if (len(dict(paper.attrs)) == 1) and (paper_ref.startswith(self.IS_PAPER_LINK)):
                    paper_abstract = self.open_tab(self.BASE + paper_ref, self.ABSTRACT_TAB_NAME)
                    if paper_abstract is None:
                        continue
                    text = self.get_abstract_text(self.BASE + paper_abstract)
                    title = self.get_title(self.BASE + paper_ref)

                    self.add_to_index(self.BASE + paper_ref, text, title, count)
                    count += 1
                    self.db_commit()

        print "%4d papers were indexed" % (count - 1)


    def count_idf(self):
        '''Count idf for each word
        Set this value to the table word_list'''
        print "Counting idf..."
        url_count = self.con.execute("select count(rowid) from url_list").fetchone()[0]
        words_urls = self.con.execute("select word_id, count(distinct url_id) from word_location "
                         "group by word_id").fetchall()

        for pair in words_urls:
            word_id = pair[0]
            num = pair[1]
            idf = math.log10(url_count / num)
            self.con.execute("update word_list set idf = %f where rowid = %d" % (idf, word_id))
        self.db_commit()


    def count_vectors_length(self):
        '''Count vector's length for each url (Euclidean norm of tf * idf for each word in url)
        Set this value to the table url_list'''
        print "Counting lengths..."
        url_ids = self.con.execute("select rowid from url_list").fetchall()
        url_ids = (url_id[0] for url_id in url_ids)

        for url_id in url_ids:
            words_count = self.con.execute("select word_id, count(word_id), idf from word_location join "
                                           " word_list on word_location.word_id = word_list.rowid where "
                                           " url_id = %d group by word_id" % url_id).fetchall()
            words_dict = {record[0]: record[1] for record in words_count}
            words_idf = {record[0]: record[2] for record in words_count}
            sum_of_words = sum(words_dict[word] for word in words_dict)

            length = 0
            for word in words_dict:
                length += pow(words_dict[word] * words_idf[word], 2)
            length = math.sqrt(length)
            length = length / sum_of_words
            self.con.execute("update url_list set length = %f where rowid = %d" % (length, url_id))

        self.db_commit()


    def create_index_tables(self):
        """ Create database tables """
        res = self.con.execute('select name from sqlite_master where type="table" and name="url_list"').fetchone()
        if res is not None:
            self.con.execute('delete from url_list')
            self.con.execute('delete from word_list')
            self.con.execute('delete from word_location')
            self.con.execute('delete from link')
            self.con.execute('delete from link_words')
            self.db_commit()
            return

        self.con.execute('create table url_list(url, length)')
        self.con.execute('create table word_list(word, idf)')
        self.con.execute('create table word_location(url_id, word_id, location)')
        self.con.execute('create table link(from_id integer, to_id integer)')
        self.con.execute('create table link_words(word_id, link_id)')
        self.con.execute('create index word_idx on word_list(word)')
        self.con.execute('create index url_idx on url_list(url)')
        self.con.execute('create index word_url_idx on word_location(word_id)')
        self.con.execute('create index url_to_idx on link(to_id)')
        self.con.execute('create index url_from_idx on link(from_id)')
        self.db_commit()


