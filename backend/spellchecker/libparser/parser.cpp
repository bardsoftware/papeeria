// Author: Sergey Sokolov
#include "parsers/latexparser.hxx"
#include <cstring>
#include <iostream>
#include <assert.h>

extern "C" {

    struct Token {
        char* token;
    };

    std::string alphabet("qwertzuiopasdfghjklyxcvbnmQWERTZUIOPASDFGHJKLYXCVBNM");

    LaTeXParser* latex_parser() {
        return new LaTeXParser(alphabet.c_str());
    }

    void delete_parser(LaTeXParser *parser) {
        delete parser;
    }

    void put_line(LaTeXParser* parser, char* line) {
        parser->put_line(line);
    }

    int next_token(LaTeXParser *parser, Token *token) {
        parser->set_url_checking(true);
        std::string next;
        if (parser->next_token(next)) {
            token->token = new char[next.size()+1];
            std::copy(next.begin(), next.end(), token->token);
            token->token[next.size()] = '\0';
            return 1;
        }
        else {
            token->token = NULL;
            return 0;
        }
    }

    void free_token(Token *token) {
        if (token->token) {
            delete [] token->token;
        }
    }
}

