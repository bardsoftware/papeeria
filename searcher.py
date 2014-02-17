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
import sys

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


    def find_rows(self, words, words_tf_idf_idf):
        '''Find documents which contain one of words and count cos distance'''
        if len(words) == 0:
            return []

        word_id_list = []
        table_num = 0
        #clause_list = ''
        query = 'SELECT url_id, denorm_rank / url_list.length AS rank FROM (' \
                ' SELECT url_id, sum/count(*) AS denorm_rank FROM ('
        fat_query = 'SELECT url_id as urlid, sum(weight) AS sum, count(*) AS match_words_num FROM ('

        for word in words:
            word_row = self.con.execute(
                "SELECT rowid FROM word_list WHERE word = '%s'" % word).fetchone()
            if word_row is not None:
                word_id = word_row[0]
                word_id_list.append(word_id)
                if table_num > 0:
                    fat_query += ' UNION '
                fat_query += 'SELECT url_id, count(*) * %f AS weight ' \
                         'FROM word_location WHERE word_id = %d ' \
                         'GROUP BY url_id' % (words_tf_idf_idf[word], word_id)
                table_num += 1

        fat_query += ') GROUP BY urlid '
        query += fat_query
        query += ') as FatQuery JOIN word_location ON (FatQuery.urlid = word_location.url_id)' \
                 'GROUP BY urlid, sum, match_words_num)' \
                 'JOIN url_list ON url_list.rowid = url_id'
#        print query
#        print fat_query
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

    def get_top_tf_idf(self, words, top_words):
        '''Return values of tf * idf * idf for words in top_words'''
        tf_dict = self.tf(words)
        idf_dict = {word: 0 for word in top_words}

        for word in tf_dict:
            if word in top_words:
                idf = self.idf(word)
                idf_dict[word] = idf
                tf_dict[word] = tf_dict[word] * idf * idf
            else:
                tf_dict[word] = 0

        tf_dict = {word: tf_dict[word] for word in tf_dict if tf_dict[word] > 0}
        inverted = [(word, tf_dict[word]) for word in tf_dict]
        return inverted, idf_dict


    def get_url_by_id(self, url_id):
        '''Return url by its id'''
        url = self.con.execute("select url_list.url, title, authors, journal.name, issue.name from url_list "
                               "join issue on url_list.issue_id = issue.rowid "
                               "join journal on issue.jour_id = journal.rowid "
                               "where url_list.rowid = '%s'" % url_id).fetchall()[0]
        return url

    @staticmethod
    def count_length(l, idfs):
        '''Return Euclidean norm of the vector, saved in list of pairs'''
        words_dict = {pair[0]: (pair[1] / idfs[pair[0]]) for pair in l}
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
        top_text_words = self.get_top_words(text_words, n)

        answer = self.get_top_tf_idf(text_words, top_text_words)
        text_words_tf_idf_idf = answer[0]
        top_idfs = answer[1]

        text_length = Searcher.count_length(text_words_tf_idf_idf, top_idfs)

#        print top_text_words
#        print text_words_tf_idf_idf

        url_ids_cos = self.find_rows(top_text_words, {word: tf_idf_idf for (word, tf_idf_idf) in text_words_tf_idf_idf})

        #url_ids = [url_id[0] for url_id in url_ids]
        url_count = len(url_ids_cos)

        url_full_count = self.con.execute("select count(rowid) from url_list").fetchone()[0]
        print >> sys.stderr, "Number of documents: %d " % url_full_count

        print >> sys.stderr, "Number of documents after cutting: %d " % url_count
        print >> sys.stderr, "Searching..."

        '''
        heap = []
        url_ids = []
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
        '''
        heap = []
        for url_pair in url_ids_cos:
            url_cos = url_pair[1] / text_length
            url_id = url_pair[0]
            if len(heap) < self.SHOW_ANSWER:
                heapq.heappush(heap, (url_cos, url_id))
            else:
                heapq.heappushpop(heap, (url_cos, url_id))


        heap.sort(reverse=True)
        top_n = [(pair[1], pair[0]) for pair in heap]

        print '{"articles": ['
        number = 1
        for url_id in top_n:
            if url_id[1] > 0:
                if number > 1:
                    print ","
                article_data = self.get_url_by_id(url_id[0])
                print "{"
                print '"docid": %4d,' % url_id[0]
                print '"rank": %f, ' % url_id[1]
                print '"url": "%s", ' % article_data[0]
                print '"title": "%s", ' % article_data[1]
                print '"authors": "%s", ' % article_data[2].encode('utf-8')
                print '"journal": "%s", ' % article_data[3]
                print '"issue": "%s" ' % article_data[4]
                print "}"
                number += 1
        print "]}"