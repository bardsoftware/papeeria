import argparse
import json

parser = argparse.ArgumentParser()
parser.add_argument('--file', help='filepath to input aux')
args = parser.parse_args()

f = open(args.file, 'r')
PREFIX = '\\newlabel'

listOfLabels = []

def parseArg(s, i):
    end = s.find('}', i + 1)
    if end == -1:
        return None, len(s)
    return s[i + 1: end], end + 1

def parseLabel(s, prefix = PREFIX):
    s = s.replace(prefix, '')

    ind = 0
    args = []
    while ind < len(s):
            if s[ind] == '{' and s[ind+1] == '{':
                ind += 1
            arg, ind = parseArg(s, ind)
            if arg is not None:
                args.append(arg)
    return args

for line in f:
    if line.startswith(PREFIX):
        args = parseLabel(line)

        node = {}
        if len(args) == 6:
            node['type'] = args[4].split('.')[0]
        else:
            node['type'] = 'references'
        node['caption'] = args[0]
        listOfLabels.append(node)

print(json.dumps(listOfLabels))
