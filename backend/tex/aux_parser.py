import argparse
import json

parser = argparse.ArgumentParser()
parser.add_argument('--file', help='filepath to input aux')
args = parser.parse_args()

f = open(args.file, 'r')
PREFIX = '\\newlabel'

listOfLabels = []

def getArg(s, i):
    end = s.find("}", i + 1)
    if end == -1:
        return None, len(s)
    return s[i + 1: end], end + 1

for line in f:
    if line.startswith(PREFIX):
        line = line.replace(PREFIX, '')
        args = []
        ind = 0
        while ind < len(line):
            if line[ind] == '{' and line[ind+1] == '{':
                ind += 1
            arg, ind = getArg(line, ind)
            if arg != None:
                args.append(arg)

        node = {}
        if len(args) == 6:
            node['type'] = args[4].split('.')[0]
        else:
            node['type'] = "references"
        node['caption'] = args[0]
        listOfLabels.append(node)

print(json.dumps(listOfLabels))
