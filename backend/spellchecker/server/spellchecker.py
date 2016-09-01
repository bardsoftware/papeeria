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
    def __init__(self, libparser: str, path: str = '', language: str ='', log: object = None):
        self.hunspell_instances = {}
        self.parser = Parser(libparser)
        self.log = log

        if path is not '':
            self.add_dictionary(path, language)

    def add_dictionary(self, path: str, language: str) -> None:
        """
        Create new hunspell instance and put into instances dictionary.

        :param path: Path to .dic and .aff files.
        :param language: Language that corresponds .dic and .aff files.
        """
        if language in self.hunspell_instances:
            return

        dictionary = "{}/{}.dic".format(path, language)
        if not os.path.isfile(dictionary):
            if self.log:
                self.log.write("File not found: {}".format(dictionary))
            return
        affix = "{}/{}.aff".format(path, language)
        if not os.path.isfile(affix):
            if self.log:
                self.log.write("File not found: {}".format(affix))
            return

        self.hunspell_instances[language] = hunspell.HunSpell(dictionary, affix)

    def check_text(self, text: str, languages) -> str:
        """
        Check text using chosen languages and return JSON with suggestions.

        :param text: Text to check.
        :param languages: Languages list.
        :return: JSON with suggestions for misspelled words.
        """
        suggestions_map = Suggestions()
        hunspells = (self.hunspell_instances[lang] for lang in languages if lang in self.hunspell_instances)
        tokens = self.parser.tokenize(text)

        for h in hunspells:
            for token in tokens:
                if not h.spell(token):
                    suggestions_map.suggestions[token].values.extend(h.suggest(token))

        return suggestions_map
