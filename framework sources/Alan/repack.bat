echo START REPACKING...
cd signapk
call java -jar signapk.jar certificate.pem key.pk8  ..\%1\dist\%2 %3\%2
cd..
echo END REPACKING.
