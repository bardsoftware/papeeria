# coding=utf-8

from subprocess import call

with open('/data/papeeria/build/topics', 'r') as topics:
    for line in topics:
        call(['python3.4', '/data/papeeria/src/wiki_crawler.py', "'%s'" % line, '-ru'])
        print('%s has been processed' % line[:-1])

call(['classifier', 'index', '-ru'])