### huntojson.sh
Convert hunspell output to JSON for spellchecker.

Launch hunspell on input file, convert output to spellchecker JSON format and write to standard output (if used without `-o`).
Usage: `./huntojson.sh [options...] [file]`
Options:
`-h`         display this help and exit
`-o output`  file where to write JSON
`-d dict`    use custom dictionaries

Example:  
```bash
./huntojson.sh demo.tex                      # launch hunspell on 'demo.tex'
./huntojson.sh -o json.txt summer-2016.tex   # write output to 'json.txt'
./huntojson.sh -d ru_RU ru_demo.tex          # use russian dictionary
```
