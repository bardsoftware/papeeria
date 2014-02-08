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
import math
import heapq

class Searcher:
    SHOW_ANSWER = 10
    MAGIC_NUMBER = 20
    IDF_BOUND = 1
    COS_BOUND = 0.1

    def __init__(self, db_name):
        self.con = sqlite.connect(db_name)

    def __del__(self):
        self.con.close()

    def db_commit(self):
        self.con.commit()

    @staticmethod
    def separate_words(text):
        splitter = re.compile('\\W*')
        return [s.lower() for s in splitter.split(text) if s != '']


    def get_top_words(self, words, n):
        '''Return top n tf * idf words in text
        Return list of words '''
        words_top = {word: 0 for word in words}
        for word in words:
            words_top[word] += 1

        for word in words_top:
            word_idf = self.con.execute(
                "select idf from word_list where word = '%s'" % word).fetchone()
            if word_idf is None:
                words_top[word] = 0
            else:
                word_idf = word_idf[0]
                if word_idf > self.IDF_BOUND:
                    words_top[word] = words_top[word] * word_idf
                else:
                    words_top[word] = 0

        words_top = {word: words_top[word] for word in words_top if words_top[word] > 0}

        sorted_top = sorted(words_top.iteritems(), key=operator.itemgetter(1), reverse=True)
        clear_list = [pair[0] for pair in sorted_top]
        if len(clear_list) <= n:
            return clear_list
        else:
            return clear_list[0: n]


    def find_rows(self, words):
        '''Find documents which contain one of words'''
        if len(words) == 0:
            return []

        word_id_list = []
        table_num = 0
        clause_list = ''

        for word in words:
            word_row = self.con.execute(
                "select rowid from word_list where word = '%s'" % word).fetchone()
            if word_row is not None:
                word_id = word_row[0]
                #print "word_id: %d" % word_id
                word_id_list.append(word_id)
                if table_num > 0:
                    clause_list += ' or '
                clause_list += 'word_id = %d' % word_id
                table_num += 1

        query = 'select distinct url_id from word_location where %s ' % clause_list
        result = self.con.execute(query)
        rows = [row for row in result]
        return rows


    def tf(self, words):
        '''Return tf of words'''
        words_top = {word: 0 for word in words}
        for word in words:
            words_top[word] += 1
        words_freq = {word: words_top[word] / float(len(words)) for word in words_top}

        return words_freq

    def idf(self, word):
        '''Return idf of word'''
        idf = self.con.execute("select idf from word_list where word = '%s'" % word).fetchone()
        if idf is None:
            return 0
        else:
            return idf[0]

    def get_top_tf_idf(self, words, n):
        '''Get top n words from list words using tf * idf'''
        tf_dict = self.tf(words)
        heap = []

        for word in tf_dict:
            idf = self.idf(word)
            tf_idf = tf_dict[word] * idf
            if len(heap) < n:
                heapq.heappush(heap, (tf_idf, word))
            else:
                heapq.heappushpop(heap, (tf_idf, word))

        heap.sort(reverse=True)
        inverted = [(pair[1], pair[0]) for pair in heap]
        return inverted


    def get_url_by_id(self, url_id):
        '''Return url by its id'''
        url = self.con.execute("select url_list.url, title, authors, journal.name, issue.name from url_list "
                               "join issue on url_list.issue_id = issue.rowid "
                               "join journal on issue.jour_id = journal.rowid "
                               "where url_list.rowid = '%s'" % url_id).fetchall()[0]
        return url

    @staticmethod
    def count_length(l):
        '''Return Euclidean norm of the vector, saved in list of pairs'''
        words_dict = {pair[0]: pair[1] for pair in l}
        length = 0
        for word in words_dict:
            length = length + words_dict[word] * words_dict[word]

        length = math.sqrt(length)
        return length

    @staticmethod
    def cos_distance(url_words_tf_idf, url_words_length, words_tf_idf, words_length):
        ''' Count cos distance between two vectors of words
        url_words_tf_idf - list of pairs (word, tf * idf), where words are words from url
        url_words_length - norm of vector url_words_tf_idf
        words_tf_idf     - list of pairs (word, tf * idf), where words are words from text
        words_length     - norm of vector words_tf_idf '''

        if url_words_length == 0 or words_length == 0:
            return 0

        words_dict = {pair[0]: pair[1] for pair in words_tf_idf}
        url_words_dict = {pair[0]: pair[1] for pair in url_words_tf_idf}

        sc_product = 0
        for word in words_dict:
            if word in url_words_dict:
                sc_product += words_dict[word] * url_words_dict[word]

        return sc_product / (url_words_length * words_length)


    def cos_search(self, text, n):
        ''' Start search by cos distance between text and documents(urls)
        n means taking n top words from the text
        top is counted by (tf * idf) '''

        text_words = Searcher.separate_words(text)
        text_words_tf_idf = self.get_top_tf_idf(text_words, n)
        text_length = Searcher.count_length(text_words_tf_idf)

        top_text_words = self.get_top_words(text_words, self.MAGIC_NUMBER)
        url_ids = self.find_rows(top_text_words)

        url_ids = [url_id[0] for url_id in url_ids]
        url_count = len(url_ids)

        url_full_count = self.con.execute("select count(rowid) from url_list").fetchone()[0]
        print "Number of documents: %d " % url_full_count

        print "Number of documents after cutting: %d " % url_count
        print "Searching..."

        heap = []
        for url_id in url_ids:
            #print url_id
            url_words = self.con.execute("select word from word_list join word_location on "
                                         " word_list.rowid = word_location.word_id where "
                                         " word_location.url_id = %s" % url_id).fetchall()

            url_words = [pair[0] for pair in url_words]
            url_words_tf_idf = self.get_top_tf_idf(url_words, len(url_words))
            url_length = self.con.execute("select length from url_list where rowid = %d" % url_id).fetchone()[0]
            url_cos = Searcher.cos_distance(url_words_tf_idf, url_length, text_words_tf_idf, text_length)

            if url_cos < self.COS_BOUND:
                continue

            if len(heap) < self.SHOW_ANSWER:
                heapq.heappush(heap, (url_cos, url_id))
            else:
                heapq.heappushpop(heap, (url_cos, url_id))

        heap.sort(reverse=True)
        top_n = [(pair[1], pair[0]) for pair in heap]

        print "Answer: "
        number = 1
        for url_id in top_n:
            if url_id[1] > 0:
                print "%2d. id = %4d  cos = %f" % (number, url_id[0], url_id[1])
                number += 1
                print "    " + self.get_url_by_id(url_id[0])[0]
                print "    " + self.get_url_by_id(url_id[0])[1]
                print "    " + self.get_url_by_id(url_id[0])[2]
                print "    " + self.get_url_by_id(url_id[0])[3]
                print "    " + self.get_url_by_id(url_id[0])[4]

