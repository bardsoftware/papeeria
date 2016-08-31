# Author: Sergey Sokolov
import grpc

import spellchecker_pb2


channel = grpc.insecure_channel('localhost:50051')
stub = spellchecker_pb2.SpellcheckStub(channel)
Text = spellchecker_pb2.Text()
Text.text = b"test line wth errorz"
Text.language.extend(["en_US", "ru_RU"])
response = stub.CheckText(Text)
print(response.json)
