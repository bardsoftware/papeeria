# encoding: utf-8
import os
import requests
import wikipedia as wiki
from shutil import rmtree
from bs4 import BeautifulSoup
from argparse import ArgumentParser

HTTP = 'http:'
DOMAIN = 'wikipedia.org'
WIKI = '/wiki/'
CATEGORY_EN = 'Category:'
EN = '//en.'


def get_urls_and_titles_of_en_pages(category_name):
    url_prefix = HTTP + EN + DOMAIN
    soup = BeautifulSoup(requests.get(url_prefix + WIKI + CATEGORY_EN + category_name).text)
    if args.r:
        for a in soup.select('div[id=mw-subcategories] a.CategoryTreeLabel'):
            for url, title in get_urls_and_titles_of_en_pages(a.contents[0]):
                yield url, title
    for a in soup.select('div.mw-category a'):
        if not a.get('href').startswith('/wiki/Category:'):
            yield url_prefix + a.get('href'), a.get('title').split(' – ')[0]


def get_urls_and_titles_of_ru_pages(category_name):
    for en_link, _ in get_urls_and_titles_of_en_pages(category_name):
        soup = BeautifulSoup(requests.get(en_link).text)
        ru_link = soup.select('li.interlanguage-link > a[lang=ru]')
        if ru_link:
            yield HTTP + ru_link[0].get('href'), ru_link[0].get('title').split(' – ')[0]


def write(pages, dir_name):
    out_dir = 'corpus/' + dir_name
    if not os.path.isdir(out_dir):
        os.makedirs(out_dir)
    with open(out_dir + '/index', 'w') as index:
        counter = 0
        for page in pages:
            page_filename = '/page%03d' % counter
            with open(out_dir + page_filename, 'w') as out:
                out.write(page.content)
            index.write('%s %s\n' % (page_filename, page.title))
            print('\'%s\' page has been processed' % page.title)
            counter += 1


def get_pages(urls_and_titles):
    for _, title in urls_and_titles:
        yield wiki.page(title)


def crawl(category_name):
    dir_name = category_name
    if args.ru:
        wiki.set_lang('ru')
        dir_name += '_ru'
    urls = get_urls_and_titles_of_ru_pages(category_name) if args.ru else get_urls_and_titles_of_en_pages(category_name)
    write(get_pages(urls), dir_name)


if __name__ == '__main__':
    argparser = ArgumentParser()
    argparser.add_argument('category', type=str,
                           help='Category to extract from Wikipedia')
    argparser.add_argument('-e', default=False, const=True, nargs='?',
                           help='If -e, erases the previous corpus. Just appends a new category otherwise')
    argparser.add_argument('-ru', default=False, const=True, nargs='?',
                           help='If -ru, downloads only russian pages, if available. '
                                'Downloads english ones otherwise')
    argparser.add_argument('-r', default=False, const=True, nargs='?',
                           help='If -r, recursively crawls subcategories')
    args = argparser.parse_args()
    if args.e:
        rmtree('corpus')
    category_name = args.category.strip().replace(' ', '_')
    crawl(category_name)
