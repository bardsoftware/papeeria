import time

import grpc
from concurrent import futures

from spellchecker import Spellchecker
import spellchecker_pb2


_ONE_DAY_IN_SECONDS = 60 * 60 * 24


class SpellcheckerServicer(spellchecker_pb2.SpellcheckServicer):
    """
    gRPC service to check text that comes from stubs.
    """
    def __init__(self):
        self._checker_ = Spellchecker("/usr/share/hunspell", "en_US")

    def CheckText(self, request, context):
        text = str(request.text, encoding="UTF-8")
        languages = [lang for lang in request.language]
        json = self._checker_.check_text(text, languages)
        return spellchecker_pb2.Suggestions(json=json)


def serve(max_workers: int = 2):
    server = grpc.server(
        futures.ThreadPoolExecutor(max_workers=max_workers)
    )
    spellchecker_pb2.add_SpellcheckServicer_to_server(
        SpellcheckerServicer(),
        server
    )
    server.add_insecure_port('[::]:50051')
    server.start()
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)

if __name__ == '__main__':
    serve()
