#!/bin/bash
# Copyright (C) 2012 thuxnder@dexlabs.org
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


pw=NULL

echo "unpacking" &&
java -jar tools/apktool/apktool.jar d $1 out > /dev/null 2>&1 &&
echo "injecting nop sled" &&
python baksmali-modifier.py out/smali > /dev/null &&
echo "repacking" &&
java -jar tools/apktool/apktool.jar b out new.apk > /dev/null 2>&1 &&
echo "extract classes.dex" &&
unzip new.apk classes.dex > /dev/null &&
echo "add obfuscation" &&
python injector.py classes.dex > /dev/null &&
echo "add classes.dex" &&
aapt r new.apk classes.dex > /dev/null &&
aapt a new.apk classes.dex > /dev/null &&
#echo "signing" &&
#echo $pw | jarsigner -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore keystore new.apk KEYNAME > /dev/null 2>&1 ; 
rm classes.dex &&
rm -r out &&
echo "done"


