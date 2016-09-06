import argparse
import json

parser = argparse.ArgumentParser()
parser.add_argument('--file', help='filepath to input aux')
args = parser.parse_args()

f = open(args.file, 'r')
s = '\\newlabel'

listOfLabels = []

def getArg(s, i):
    end = s[i+1:].find("}")
    return s[i+1:end + i + 1], end + i + 2

for line in f:
    if line.startswith(s):
        line = line.replace(s, '')
        caption, ind = getArg(line, 0)
        ind += 1
        args = []
        while (line[ind] == '{'):
           a, ind = getArg(line, ind)
           args.append(a)
        print(args)
        node = {}

        if len(args) == 5:
            node['type'] = args[3].split('.')[0]
        else:
            node['type'] = "references"
        node['caption'] = caption
        listOfLabels.append(node)

print(json.dumps(listOfLabels))
