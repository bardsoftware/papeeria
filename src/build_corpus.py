# coding=utf-8

from subprocess import call

with open('papeeria/build/topics', 'r') as topics:
    for line in topics:
        line = line.strip()
        call(['python3.4', 'papeeria/src/wiki_crawler.py', line, '-ru'])
        print('%s has been processed' % line)

call(['java', '-jar', 'papeeria/jar/NER.jar', 'index', '-ru'])