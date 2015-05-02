# coding=utf-8

from subprocess import call

with open('build/topics', 'r') as topics:
    for line in topics:
        call(['python3.4', '/data/papeeria/src/wiki_crawler.py', "'%s'" % line, '-ru'])

call(['classifier', 'index', '-ru'])