echo START CODE REORDERING...
cd codeReordering
call java -jar codeReorder.jar ..\%1\smali
cd..
echo END CODE REORDERING.
