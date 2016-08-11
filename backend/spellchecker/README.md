### huntojson.sh
Convert hunspell output to JSON for spellchecker.

Launch hunspell on input file, convert output to spellchecker JSON format and write to standard output.
Usage: `./huntojson.sh [options...] [file]`
Options:
`-h`         display this help and exit
`-d dict`    use custom dictionaries

Example:  
```bash
./huntojson.sh demo.tex                      # launch hunspell on 'demo.tex'
./huntojson.sh -d ru_RU ru_demo.tex          # use russian dictionary
```
