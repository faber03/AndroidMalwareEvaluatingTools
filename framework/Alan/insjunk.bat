cd dalvikobfuscator
if %2==nop (
	echo START INSERT JUNK INSTRUCTIONS NOP...
	python baksmali-modifier.py ..\%1\smali
	echo END INSERT JUNK INSTRUCTIONS NOP
	)
if %2==branch (
	echo START INSERT JUNK INSTRUCTION BRANCH...
	python baksmali-modifier.py ..\%1\smali
	call java -jar nopToJunk1.jar ..\%1\smali
	echo END INSERT JUNK INSTRUCTION BRANCH
	)
if %2==garbage (
	echo START INSERT JUNK INSTRUCTION GARBAGE...
	call java -jar nopToJunk2.jar ..\%1\smali
	echo END INSERT JUNK INSTRUCTION GARBAGE
	)
if %2==nop-garbage (
	echo START INSERT JUNK INSTRUCTION NOP-GARBAGE...
	python baksmali-modifier.py ..\%1\smali
	call java -jar nopToJunk2.jar ..\%1\smali
	echo END INSERT JUNK INSTRUCTION NOP-GARBAGE
	)
if %2==branch-garbage (
	echo START INSERT JUNK INSTRUCTION BRANCH-GARBAGE...
	python baksmali-modifier.py ..\%1\smali
	call java -jar nopToJunk1.jar ..\%1\smali
	call java -jar nopToJunk2.jar ..\%1\smali
	echo END INSERT JUNK INSTRUCTION BRANCH-GARBAGE
	)
cd..
