echo START IDENTIFIER RENAMING...
cd identifierRenaming
call java -jar identifierRenaming.jar ..\%1\AndroidManifest.xml ..\%1 %2
cd..
echo END IDENTIFIER RENAMIN
