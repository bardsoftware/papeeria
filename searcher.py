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

from BeautifulSoup import *
from pysqlite2 import dbapi2 as sqlite
import operator
import search

class Searcher:

    def __init__(self, db_name):
        self.con = sqlite.connect(db_name)

    def __del__(self):
        self.con.close()

    def separate_words(self, text):
        splitter = re.compile('\\W*')
        return [s.lower() for s in splitter.split(text) if s != '']

    def get_top_words(self, text, n):
        '''Return top n words in text'''
        words = self.separate_words(text)
        words_top = {}
        for word in words:
            if word not in search.ignore_words:
                if word in words_top:
                    words_top[word] += 1
                else:
                    words_top[word] = 1
        sorted_top = sorted(words_top.iteritems(), key=operator.itemgetter(1))
        words_only = [pair[0] for pair in sorted_top]

        if len(sorted_top) <= n:
            return words_only
        else:
            return words_only[len(sorted_top) - n: len(sorted_top)]


    def find_rows(self, words):
        '''Find documents which contain _all_ words'''
        word_id_list = []
        table_num = 0
        field_list = 'w0.url_id'
        table_list = ''
        clause_list = ''

        for word in words:
            word_row = self.con.execute(
                "select rowid from word_list where word = '%s'" % word).fetchone()
            if word_row is not None:
                word_id = word_row[0]
                #print "word_id: %d" % word_id
                word_id_list.append(word_id)
                if table_num > 0:
                    table_list += ', '
                    clause_list += ' and '
                    clause_list += 'w%d.url_id = w%d.url_id and ' % (table_num - 1, table_num)
                #field_list += ', w%d.location' % table_num
                table_list += 'word_location w%d' % table_num
                clause_list += 'w%d.word_id = %d' % (table_num, word_id)
                table_num += 1

        query = 'select distinct %s from %s where %s ' % (field_list, table_list, clause_list)
        result = self.con.execute(query)
        rows = [row for row in result]
        return rows

    def get_url_by_id(self, url_id):
        '''Return url by its id'''
        url = self.con.execute("select url from url_list where rowid = '%s'" % url_id).fetchone()[0]
        return url

    def top_search(self, text, n):
        '''Start search by n top words in text'''
        words = self.get_top_words(text, n)
        url_id_list = self.find_rows(words)
        for url_id in url_id_list:
            print self.get_url_by_id(url_id)

        if len(url_id_list) == 0:
            print "I can't find anything. Sorry... :("

