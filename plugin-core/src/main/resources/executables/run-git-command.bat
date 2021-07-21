echo off

 setlocal enabledelayedexpansion




echo %*
echo GIT_DIR=%1%
set fileoutdir=%cd%
D:
cd %1%
set RESTVAR=%*
set rep=
echo %RESTVAR%


echo GIT_COMMAND= %RESTVAR%


 set GIT_RUN_COMMAND=%RESTVAR%
 set f=%1%
 set t=

 echo.
 echo       f = [%f%]
 echo       t = [%t%]

 echo.
 echo     GIT_RUN_COMMAND = [%GIT_RUN_COMMAND%]

 set GIT_RUN_COMMAND=!GIT_RUN_COMMAND:%f%=%t%!

 echo %GIT_RUN_COMMAND%

echo %RESTVAR%

echo GIT_COMMAND= %GIT_RUN_COMMAND%
start /B /wait %GIT_RUN_COMMAND% >>%fileoutdir%\git_command_out.txt

cd %fileoutdir%
echo %fileoutdir%
echo %cd%