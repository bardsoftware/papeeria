import argparse
import json

parser = argparse.ArgumentParser()
parser.add_argument('--file', help='filepath to input aux')
args = parser.parse_args()

f = open(args.file, 'r')
s = '\\newlabel'

listOfLabels = []

for line in f:
	if (line[:len(s)] == s):
		line = line.replace('}', '')
		line = line.split('{')

		listOfLabels.append(line[1])
data = {}
data['Labels'] = listOfLabels
print(json.dumps(data))