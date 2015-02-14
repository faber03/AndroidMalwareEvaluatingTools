@echo off
echo.
call disass C:\Users\LuxAgo\Desktop\malware\06fd5e281179fdfad8c84a4a12977a6942b989923826f91b0bb2fc0d4c9e9641
echo.
call reass 06fd5e281179fdfad8c84a4a12977a6942b989923826f91b0bb2fc0d4c9e9641.out
echo.
call repack 06fd5e281179fdfad8c84a4a12977a6942b989923826f91b0bb2fc0d4c9e9641.out \06fd5e281179fdfad8c84a4a12977a6942b989923826f91b0bb2fc0d4c9e9641 C:\Users\LuxAgo\Desktop\malware\signed
move C:\Users\LuxAgo\Desktop\malware\06fd5e281179fdfad8c84a4a12977a6942b989923826f91b0bb2fc0d4c9e9641 C:\Users\LuxAgo\Desktop\malware\evaluated
echo.
call RMDIR /S /Q .\06fd5e281179fdfad8c84a4a12977a6942b989923826f91b0bb2fc0d4c9e9641.out
echo. ------------------------------------------------------------- 
echo.
call disass C:\Users\LuxAgo\Desktop\malware\6ea6a44433e321d81fea8fd2c91d5f7d71f57136b979cc85dac90ab5f8f7b070
echo.
call reass 6ea6a44433e321d81fea8fd2c91d5f7d71f57136b979cc85dac90ab5f8f7b070.out
echo.
call repack 6ea6a44433e321d81fea8fd2c91d5f7d71f57136b979cc85dac90ab5f8f7b070.out \6ea6a44433e321d81fea8fd2c91d5f7d71f57136b979cc85dac90ab5f8f7b070 C:\Users\LuxAgo\Desktop\malware\signed
move C:\Users\LuxAgo\Desktop\malware\6ea6a44433e321d81fea8fd2c91d5f7d71f57136b979cc85dac90ab5f8f7b070 C:\Users\LuxAgo\Desktop\malware\evaluated
echo.
call RMDIR /S /Q .\6ea6a44433e321d81fea8fd2c91d5f7d71f57136b979cc85dac90ab5f8f7b070.out
echo. ------------------------------------------------------------- 
call exit