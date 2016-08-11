#!/bin/bash

# usage info
show_help() {
cat << EOF
Usage: ${0##*/} [options...] [file]
Launch hunspell on file, convert output to JSON format 
and write to standard output.

    -h         display this help and exit
    -d dict    use custom dictionaries

Example: ${0##*/} demo.tex                      # launch hunspell on 'demo.tex'
         ${0##*/} -d ru_RU ru_demo.tex          # use russian dictionary
EOF
}

DICT=
OPTIND=1

# command line arguments processing
while getopts ":hd:" opt; do
    case "$opt" in
        h)
            show_help
            exit 0
            ;;
        d)
            DICT="-p $OPTARG"
            ;;
        ?)
            echo -e "Invalid option: -$OPTARG\n" >&2
            show_help >&2
            exit 1
            ;;
    esac
done
shift $((OPTIND-1))

if [ "$#" != "1" ]
then
    echo -e "Missing input file\n" >&2
    show_help >&2
    exit 2
fi

INFILE=$1

JSON=$(
    cat $INFILE    | 
    hunspell -a -t | 
    grep "^&.*"    | 
    awk $DICT '
    BEGIN { print "{" }
    {
        print "\t\""$2"\": [" ;

        split($0, split_string, ": ");
        options_number=split(split_string[2], options, ", ");

        for (i = 1; i <= options_number; i++) 
            {print "\t\t\""options[i]"\","} 
        
        print "\t]," }
    END { print "}" }
    '
)

echo $JSON

