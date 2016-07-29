import argparse
import json

parser = argparse.ArgumentParser()
parser.add_argument('--file', help='filepath to input aux')
args = parser.parse_args()

f = open(args.file, 'r')
s = '\\newlabel'

listOfLabels = []

for line in f:
	if line.find(s) == 0:
		line = line.replace('}', '')
		line = line.split('{')
		node = {}
		node['type'] = line[6].split('.')[0]
		node['caption'] = line[1]
		listOfLabels.append(node)

data = {}
data['Labels'] = listOfLabels

print(json.dumps(data))	