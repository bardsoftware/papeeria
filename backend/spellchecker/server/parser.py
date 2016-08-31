from ctypes import cdll, c_char_p, pointer, Structure
from weakref import proxy


class Parser:
    """
    Class wraps CFFI for LaTeX parser from Hunspell source code.
    """
    def __init__(self):
        # note: these fields' names doesn't start with "_" since deleting of this 
        # object isn't so straightforward -- for some reason interpreter destroys 
        # these "private" fields before __del__ is invoked.
        self.parser_lib_ = cdll.LoadLibrary("../libparser/build/libparser.so")
        self.parser_ = self.parser_lib_.latex_parser()

    def __del__(self):
        self.parser_lib_.delete_parser(self.parser_)

    def put_line(self, line: str):
        """
        Put the line into the parser instance so to tokenize it.

        :param line: A line from text to tokenize.
        """
        self.parser_lib_.put_line(self.parser_, bytes(line, encoding="UTF-8"))

    def next_token(self):
        """
        Get next token from the line.

        :return: Token from the line that was inserted into the parser.
        """
        # actually, we have to put a pointer to pointer to char so
        # to allocate new memory for the token
        if self.parser_lib_.next_token(self.parser_, self._token_ptr_):
            word = str(self._token_ptr_.contents.token, encoding="UTF-8")
            # also we have to free the allocated memory manually
            self.parser_lib_.free_token(self._token_ptr_)
            return word
        else:
            return None

    def tokenize(self, line: str):
        """
        Tokenize line and return as generator.
        Generator function simpifies the interface and makes parser rather
        lightweight and easy to use.

        :param line: A line to tokenize.
        :return: generator object that extracts and yields tokens from the line.
        """
        self.put_line(line)
        word = True
        while word:
            if self.parser_lib_.next_token(self.parser_, self._token_ptr_):
                word = str(self._token_ptr_.contents.token, encoding="UTF-8")
                # also we have to free the allocated memory manually
                self.parser_lib_.free_token(self._token_ptr_)
                yield word
            else:
                return

    class Token(Structure):
        """
        We need this class just to point to pointer to char.
        It could be just char**, but this way seemed more elegant.
        """
        _fields_ = [("token", c_char_p)]

    _token_ptr_ = pointer(Token())
