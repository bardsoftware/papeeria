# coding=utf-8

from subprocess import call

with open('/data/papeeria/build/topics', 'r') as topics:
    for line in topics:
        line = line.strip()
        call(['python3.4', '/data/papeeria/src/wiki_crawler.py', line, '-ru'])
        print('%s has been processed' % line)

call(['java', '-jar', '/data/papeeria/jar/NER.jar', 'index', '-ru'])