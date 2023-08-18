#!/usr/bin/env python3

import shutil
from http.server import HTTPServer, SimpleHTTPRequestHandler


shortDict = "ShortDictionary.txt"
longDict = "LongDictionary.txt"

# The dictionary files need to be served for (de)compression to work
shutil.copyfile(f"shared/resources/{shortDict}", f"pages/{shortDict}")
shutil.copyfile(f"shared/resources/{longDict}", f"pages/{longDict}")


class RequestHandler(SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs, directory="pages")


port = 8000
httpd = HTTPServer(("", port), RequestHandler)
print(f"Open http://localhost:{port} in your browser")
httpd.serve_forever()
