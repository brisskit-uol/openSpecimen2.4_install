@echo off
:: script for updating property files
SETLOCAL EnableExtensions
SETLOCAL EnableDelayedExpansion
if "%3"=="" (
  ECHO Script will optionally accept 3 args: PropKey PropVal File
  SET PROPKEY=UseCompression
  SET PROPVAL=false
  SET FILE=config.properties
) ELSE (
  SET PROPKEY=%1
  SET PROPVAL=%2
  SET FILE=%3
)
FINDSTR /B %PROPKEY% %FILE% >nul
IF %ERRORLEVEL% EQU 1 GOTO nowork
MOVE /Y "%FILE%" "%FILE%.bak"
FOR /F "USEBACKQ tokens=*" %%A IN (`TYPE "%FILE%.bak" ^|FIND /N /I "%PROPKEY%"`) DO (
  SET LINE=%%A
)
FOR /F "tokens=1,2* delims=]" %%S in ("%LINE%") DO SET LINE=%%S
SET /A LINE=%LINE:~1,6%
SET /A COUNT=1
FOR /F "USEBACKQ tokens=*" %%A IN (`FIND /V "" ^<"%FILE%.bak"`) DO (
  IF "!COUNT!" NEQ "%LINE%" (
      ECHO %%A>>"%FILE%"
  ) ELSE (
      ECHO %PROPKEY%=%PROPVAL%>>"%FILE%"
      ECHO Updated %FILE% with value %PROPKEY%=%PROPVAL%
  )
  SET /A COUNT+=1
)
GOTO end
:nowork
echo Didn't find matching string %PROPKEY% in %FILE%. No work to do.
pause
:end