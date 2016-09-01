# Author: Sergey Sokolov
import os.path

import hunspell

from spellchecker_pb2 import Suggestions
from parser import Parser


class Spellchecker:
    """
    Class contains multiple hunspell instances (one per language) and provides
    text spellchecking function using all chosen dictionaries.
    """
    def __init__(self, libparser: str, path: str = '', log: object = None):
        self.hunspell_instances = {}
        self.parser = Parser(libparser)
        self.log = log
        self.path = path

    def add_dictionary(self, language: str) -> None:
        """
        Create new hunspell instance and put into instances dictionary.

        :param language: Language that corresponds .dic and .aff files.
        """
        if language in self.hunspell_instances:
            return

        dictionary = "{}/{}.dic".format(self.path, language)
        if not os.path.isfile(dictionary):
            if self.log:
                self.log.write("File not found: {}".format(dictionary))
            return
        affix = "{}/{}.aff".format(self.path, language)
        if not os.path.isfile(affix):
            if self.log:
                self.log.write("File not found: {}".format(affix))
            return

        self.hunspell_instances[language] = hunspell.HunSpell(dictionary, affix)

    def check_text(self, text: str, languages) -> str:
        """
        Check text using chosen languages and return Suggestions message object.

        :param text: Text to check.
        :param languages: Languages list.
        :return: Suggestions message (dictionary-like) for misspelled words.
        """
        for lang in languages:
            if lang not in self.hunspell_instances:
                self.add_dictionary(lang)

        dictionaries = (self.hunspell_instances[lang] for lang in languages if lang in self.hunspell_instances)
        tokens = self.parser.tokenize(text)
        suggestions_map = Suggestions()

        for dictionary in dictionaries:
            for token in tokens:
                if not dictionary.spell(token):
                    suggestions_map.suggestions[token].values.extend(dictionary.suggest(token))

        return suggestions_map
