# coding: utf-8

from http.server import BaseHTTPRequestHandler, HTTPServer
from subprocess import check_output
from urllib.parse import urlparse, parse_qs
from urllib.request import urlopen

OK_CODE = 200
DEFAULT_PORT = 8080


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        req_url = urlparse(self.path)
        if req_url.path == '/q/pdf':
            query_components = parse_qs(req_url.query)
            url = query_components["url"][0]
            with urlopen(url) as in_file, open("tmp/tmp.pdf", "wb") as out_file:
                out_file.write(in_file.read())
            out = check_output(['java', '-jar', 'jar/NER.jar', 'search', 'tmp', '-pdf', '-ru'])
            self.send_response(200)
            self.send_header("Access-Control-Allow-Origin", "*")
            self.send_header("Content-type", "text/plain")
            self.end_headers()
            self.wfile.write(out)

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