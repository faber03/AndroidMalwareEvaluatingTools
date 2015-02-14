#!/usr/bin/env python
# Copyright (C) 2012 pleed@dexlabs.org
#
# Licensed under the Apache License, Version 2.0 (the 'License');
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an 'AS IS' BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


import sys
import os

from pyparsing import *

InjectedCode = ["nop\n" for i in range(10)]
MethodToken = Literal(".method")
AccessFlag = Literal("public") | \
			 Literal("private") | \
			 Literal("protected")| \
			 Literal("abstract")| \
			 Literal("static")| \
			 Literal("constructor")| \
			 Literal("final")| \
			 Literal("native") | \
			 Literal("bridge") | \
			 Literal("synthetic") | \
			 Literal("native") | \
			 Literal("varargs") | \
			 Literal("declared-synchronized")

JavaType = Word(alphas+"[", alphanums +"_$[;/", min=1)
MethodName = Word(alphas+"$_<", alphanums+"_>$", min=1)
ArgList = JavaType
MethodProtoType = MethodName + Suppress("(") + Optional(ArgList) + Suppress(")") + JavaType
MethodDecl = Suppress(MethodToken) + ZeroOrMore(AccessFlag) + Suppress(MethodProtoType)

def injectnops(filename):
	with open(filename, "r") as smalifile:
		lines = smalifile.readlines()
	modified = []
	for index, line in enumerate(lines):
		modified.append(line)
		if line.startswith(".method"):
			try:
				flags = list(MethodDecl.parseString(line.strip("\n"),parseAll=True))
			except Exception as e:
				print line
				raise e
			if "abstract" not in flags and "native" not in flags:
				modified += InjectedCode
		
	with open(filename, "w") as smalifile:
		smalifile.writelines(modified)

def run(directory):
	for dirpath, dinames, filenames in os.walk(directory):
		for filename in filter(lambda x: x.endswith(".smali"), filenames):
			injectnops(os.path.join(dirpath, filename))

def usage():
	print "%s %s"%(sys.argv[0], sys.argv[1])
	print ""
	print "inject nops into baksmali files"

if __name__ == "__main__":
	if len(sys.argv) != 2:
		usage()
	else:
		run(sys.argv[1])


