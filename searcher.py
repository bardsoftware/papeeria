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
        if len(sorted_top) <= n:
            return sorted_top
        else:
            return sorted_top[0: n]

    def get_top_n_from_dict(self, dic, n):
        '''Return list of pairs (key, value)
        sorted top n values from dictionary dic '''
        heap = []
        for word in dic:
            if len(heap) < n:
                heapq.heappush(heap, (dic[word], word))
            else:
                heapq.heappushpop(heap, (dic[word], word))
        heap.sort(reverse=True)
        inverted = [(pair[1], pair[0]) for pair in heap]
        return inverted


    def tf(self, words):
        '''Return words sorted by their tf'''
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

        sorted_top = self.get_top_n_from_dict(top_dict, n)
        return sorted_top


    def get_url_by_id(self, url_id):
        '''Return url by its id'''
        url = self.con.execute("select url from url_list where rowid = '%s'" % url_id).fetchone()[0]
        return url

    def count_length(self, l):
        '''Return length of the vector saved in list of pairs'''
        words_dict = {pair[0]: pair[1] for pair in l}
        length = 0
        for word in words_dict:
            length = length + words_dict[word] * words_dict[word]

        length = math.sqrt(length)
        return length

    def cos_distance(self, url_id, words_tf_idf, length):
        '''Count cos distance between list of pairs (word, tf * idf)
        and article with id url = url_id
        length is a length of vector, it doesn't affect the order of documents, but it makes
        cos values between 0 and 1'''

        url_words = self.con.execute("select word from word_list join word_location on "
                                     "word_list.rowid = word_location.word_id where "
                                     "word_location.url_id = %s" % url_id).fetchall()

        url_words = [pair[0] for pair in url_words]
        url_words_counted = self.get_top_words(url_words, len(url_words))

        words_dict = {pair[0]: pair[1] for pair in words_tf_idf}
        url_words_dict = {pair[0]: pair[1] for pair in url_words_counted}

        sum = 0
        for word in words_dict:
            if word in url_words_dict:
                sum = sum + words_dict[word] * url_words_dict[word]

        url_length = self.con.execute("select length from url_list where rowid = %d" % url_id).fetchone()[0]
        cos = sum / (url_length * length)
        return cos


    def cos_search(self, text, n):
        ''' Start search by cos distance between text and documents
        n means taking n top words from the text
        top is counted by (number of repeats) * (idf) '''

        all_words = self.separate_words(text)
        words_top_tf_idf = self.get_top_tf_idf(all_words, n)
        print words_top_tf_idf
        length = self.count_length(words_top_tf_idf)
        #list of pairs

        url_ids = self.con.execute("select rowid from url_list").fetchall()
        url_ids = [url_id[0] for url_id in url_ids]
        url_count = len(url_ids)
        print "Number of documents is %d" % url_count
        print "Searching..."

        url_cos = {url_id: self.cos_distance(url_id, words_top_tf_idf, length) for url_id in url_ids}
        top_n = self.get_top_n_from_dict(url_cos, self.SHOW_ANSWER)

        print "Answer: "
        for url_id in top_n:
            print "id = %4d  cos = %f" % (url_id[0], url_id[1])
            print self.get_url_by_id(url_id[0])

