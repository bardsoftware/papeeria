#!/bin/bash

# usage info
show_help() {
cat << EOF
Usage: ${0##*/} [options...] [file]
Launch hunspell on file, convert output to JSON format 
and write to standard output if used without '-o'.

    -h         display this help and exit
    -o output  file where to write JSON
    -d dict    use custom dictionaries

Example: ${0##*/} demo.tex                      # launch hunspell on 'demo.tex'
         ${0##*/} -o json.txt summer-2016.tex   # write output to 'json.txt'
         ${0##*/} -d ru_RU ru_demo.tex          # use russian dictionary
EOF
}

OUTFILE=
DICT=

# command line arguments processing
OPTIND=1
while getopts ":hd:o:" opt; do
    case "$opt" in
        h)
            show_help
            exit 0
            ;;
        d)
            DICT="-p $OPTARG"
            ;;
        o)
            OUTFILE="$OPTARG"
            ;;
        ?)
            echo -e "Invalid option: -$OPTARG\n" >&2
            show_help >&2
            exit 1
            ;;
    esac
done
shift $((OPTIND-1))

if [ "$#" != 1 ]
then
    echo -e "Missing input file\n" >&2
    show_help >&2
    exit 2
fi

INFILE=$1

JSON=`
    cat $INFILE  | 
    hunspell -at | 
    grep "^&.*"  | 
    sed "s/,//g" | 
    awk $DICT '
    BEGIN { print "{" }
    {
        print "\t\""$2"\": [" ;

        for (i = 5; i <= NF; i++) 
            if (i != NF) 
                {print "\t\t\""$i"\","} 
            else 
                {print "\t\t\""$i"\""} ;
        
        print "\t]," }
    END { print "}" }
    '` 

if [ -n "$OUTFILE" ]
then
    echo $JSON > $OUTFILE
else
    echo $JSON
fi

