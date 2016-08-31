from spellchecker import Spellchecker
import time
import hunspell

with open("/home/tener/sandbox/papeeria-hunspell/testcase/smalltest") as tests:
    t = ""
    for l in tests:
        t += l + "\n"
    s = Spellchecker("/usr/share/hunspell", "en_US")
    t1 = time.time()
    x = s.check_text(t, ["en_US"])
    print(time.time() - t1)
    print(x)
