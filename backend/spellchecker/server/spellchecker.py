# Author: Sergey Sokolov
import os.path

import hunspell
import ujson
from spellchecker_pb2 import Suggestions

from parser import Parser


class Spellchecker:
    """
    Class contains multiple hunspell instances (one per language) and provides
    text spellchecking function using all chosen dictionaries.
    """
    def __init__(self, path: str = '', language: str ='', log: object = None):
        self._hunspell_instances = {}
        self._parser = Parser("../libparser/build/libparser.so")
        self._log = log

        if path is not '':
            self.add_dictionary(path, language)

    def add_dictionary(self, path: str, language: str) -> None:
        """
        Create new hunspell instance and put into instances dictionary.

        :param path: Path to .dic and .aff files.
        :param language: Language that corresponds .dic and .aff files.
        """
        if language in self._hunspell_instances:
            return

        dictionary = "{}/{}.dic".format(path, language)
        if not os.path.isfile(dictionary):
            if self._log:
                self._log.write("File not found: {}".format(dictionary))
            return
        affix = "{}/{}.aff".format(path, language)
        if not os.path.isfile(affix):
            if self._log:
                self._log.write("File not found: {}".format(affix))
            return

        self._hunspell_instances[language] = hunspell.HunSpell(dictionary, affix)

    def check_text(self, text: str, languages) -> str:
        """
        Check text using chosen languages and return JSON with suggestions.

        :param text: Text to check.
        :param languages: Languages list.
        :return: JSON with suggestions for misspelled words.
        """
        suggestions_map = Suggestions()
        hunspells = (self._hunspell_instances[lang] for lang in languages if lang in self._hunspell_instances)
        tokens = self._parser.tokenize(text)

        for h in hunspells:
            for token in tokens:
                if not h.spell(token):
                    suggestions_map.suggestions[token].values.extend(h.suggest(token))

        # return ujson.dumps(suggestions)
        return suggestions_map
