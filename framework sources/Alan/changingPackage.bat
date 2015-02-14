echo START CHANGING PACKAGE NAME...
cd changingPackage
call java -jar changingPackage.jar ..\%1\AndroidManifest.xml %2 ..\%1
cd..
echo END CHANGING PACKAGE NAME
