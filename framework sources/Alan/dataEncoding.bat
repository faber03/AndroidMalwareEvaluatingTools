echo START DATA ENCODING...
cd dataEncoding
call java -jar encrypter.jar ..\%1\smali
mkdir ..\%1\smali\com12345689
copy Decrypter.smali ..\%1\smali\com12345689
cd..
echo END DATA ENCODING.
