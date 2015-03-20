# encoding: utf-8
import mwclient
from argparse import ArgumentParser
import mwparserfromhell as mwparser
import os


def get_pages(topic):
    site = mwclient.Site('en.wikipedia.org')
    category = site.Pages['Category:' + topic]
    for page in category:
        yield page


def collect(topic):
    topic = topic.strip().replace(' ', '_')
    out_dir = 'corpus/' + topic
    if not os.path.isdir(out_dir):
        os.makedirs(out_dir)
    with open(out_dir + '/index', 'w') as index:
        counter = 0
        for page in get_pages(topic):
            if not page.name.startswith('Category:'):
                page_filename = '/page%03d' % counter
                with open(out_dir + page_filename, 'w') as out:
                    out.write(mwparser.parse(page.text()).strip_code().encode('utf-8'))
                index.write('%s %s\n' % (page_filename, page.name.encode('ascii', 'ignore')))
                print '\'%s\' page has been processed' % page.name
                counter += 1


if __name__ == '__main__':
    argparser = ArgumentParser()
    argparser.add_argument('category', type=str,
                           help='Category to extract from Wikipedia')
    argparser.add_argument('-e', default=False, const=True, nargs='?',
                           help='If -e, erases the previous corpus. Just appends a new category otherwise')
    args = argparser.parse_args()
    if args.e:
        from shutil import rmtree

        rmtree('corpus')
    collect(args.category)

