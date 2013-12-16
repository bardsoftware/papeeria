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
import math

class Searcher:
    SHOW_ANSWER = 10

    def __init__(self, db_name):
        self.con = sqlite.connect(db_name)

    def __del__(self):
        self.con.close()

    def db_commit(self):
        self.con.commit()

    def separate_words(self, text):
        splitter = re.compile('\\W*')
        return [s.lower() for s in splitter.split(text) if s != '']

    def get_top_words(self, words, n):
        '''Return top n words in text'''
        words_top = {word: 0 for word in words}
        for word in words:
            words_top[word] += 1

        sorted_top = sorted(words_top.iteritems(), key=operator.itemgetter(1), reverse=True)
        #print sorted_top

        if len(sorted_top) <= n:
            return sorted_top
        else:
            return sorted_top[0: n]

    def count_idf(self):
        print "Counting idf..."
        url_count = self.con.execute("select count(rowid) from url_list").fetchone()[0]
        words_count = self.con.execute("select count(rowid) from word_list").fetchone()[0]
        max = 0
        for word_id in range(1, words_count + 1):
            urls = self.con.execute("select distinct url_id from word_location "
                                        "where word_id = %s" % word_id).fetchall()
            num = len(urls)
            if num > max:
                   max = num
            idf = math.log10(url_count / num)
            self.con.execute("update word_list set idf = %f where rowid = %d" % (idf, word_id))
        self.db_commit()


    def tf(self, words):
        '''Return top n words in text'''
        words_top = {word: 0 for word in words}
        for word in words:
            words_top[word] += 1
        words_top = {word: words_top[word] / float(len(words)) for word in words_top}

        sorted_top = sorted(words_top.iteritems(), key=operator.itemgetter(1), reverse=True)
        return sorted_top


    def get_top_tf_idf(self, words, n):
        '''Get top n words from list words using tf * idf'''
        tf_list = self.tf(words)
        top_dict = {pair[0]: pair[1] for pair in tf_list}
        for word in top_dict:
            idf = self.con.execute("select idf from word_list where word = '%s'" % word).fetchone()[0]
            top_dict[word] = top_dict[word] * idf

        sorted_top = sorted(top_dict.iteritems(), key=operator.itemgetter(1), reverse=True)

        if len(sorted_top) <= n:
            return sorted_top
        else:
            return sorted_top[0: n]


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
        '''Start search by n top words in text
        top is counted by number of repeats'''
        words_pairs = self.get_top_words(self.separate_words(text), n)
        words = [pair[0] for pair in words_pairs]
        url_id_list = self.find_rows(words)
        for url_id in url_id_list:
            print self.get_url_by_id(url_id)

        if len(url_id_list) == 0:
            print "I can't find anything. Sorry... :("


    def cos_distance(self, url_id, words):
        '''Count cos distance between list of words and article with id url = url_id'''
        sum = 0
        url_words = self.con.execute("select word from word_list join word_location on "
                                     "word_list.rowid = word_location.word_id where "
                                     "word_location.url_id = %s" % url_id).fetchall()

        url_words = [pair[0] for pair in url_words]
        url_words_counted = self.get_top_words(url_words, len(url_words))
        #list of pairs

        words_dict = {pair[0]: pair[1] for pair in words}
        url_words_dict = {pair[0]: pair[1] for pair in url_words_counted}

        for word in words_dict:
            if word in url_words_dict:
                sum = sum + words_dict[word] * url_words_dict[word]

        length1 = 0
        for word in words_dict:
            length1 = length1 + words_dict[word] * words_dict[word]
        length1 = math.sqrt(length1)

        length2 = 0
        for word in url_words_dict:
            length2 = length2 + url_words_dict[word] * url_words_dict[word]
        length2 = math.sqrt(length2)

        cos = sum / (length1 * length2)
        return cos


    def cos_search(self, text, n):
        ''' Start search by cos distance between text and documents
        n means taking n top words from the text
        top is counted by (number of repeats) * (idf) '''

        all_words = self.separate_words(text)
        words = self.get_top_tf_idf(all_words, n)
        #list of pairs

        url_count = self.con.execute("select count(rowid) from url_list").fetchone()[0]
        print "Number of documents is %s" % url_count
        print "Searching..."

        url_cos = {url_id: self.cos_distance(url_id, words) for url_id in range(1, url_count + 1)}
        top_n = sorted(url_cos.iteritems(), key=operator.itemgetter(1), reverse=True)
        top_n = top_n[0: self.SHOW_ANSWER]

        print "Answer: "
        for url_id in top_n:
            print "id = %4s  cos = %s" % (url_id[0], url_id[1])
            print self.get_url_by_id(url_id[0])







