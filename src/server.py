# coding: utf-8

from http.server import BaseHTTPRequestHandler, HTTPServer
from subprocess import check_output


OK_CODE = 200
DEFAULT_PORT = 8080


class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        length = self.headers['content-length']
        data = self.rfile.read(int(length))
        self.send_response(OK_CODE)

        print(data[:100])

        with open('tmp/tmp.pdf', 'wb') as tmp:
            tmp.write(data)

        out = check_output(['java', '-jar', 'jar/NER.jar', 'search', 'tmp', '-pdf', '-ru'])
        self.wfile.write(out)

if __name__ == '__main__':
    server = HTTPServer(('', DEFAULT_PORT), Handler)
    print('Started httpserver on port %d' % DEFAULT_PORT)
    server.serve_forever()