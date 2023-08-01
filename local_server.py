#!/usr/bin/env python3

import shutil
from httpwatcher import HttpWatcherServer
from tornado.ioloop import IOLoop

shortDict = "ShortDictionary.txt"
longDict = "LongDictionary.txt"

shutil.copyfile(f"shared/resources/{shortDict}", f"pages/{shortDict}")
shutil.copyfile(f"shared/resources/{longDict}", f"pages/{longDict}")

files = ["index.html", "main.js", "worker.js", "style.css", shortDict, longDict]

server = HttpWatcherServer(
    "pages/index.html",
    watch_paths=[f"pages/{file}" for file in files],
)
server.listen()

try:
    # will keep serving until someone hits Ctrl+C
    IOLoop.current().start()
except KeyboardInterrupt:
    server.shutdown()
