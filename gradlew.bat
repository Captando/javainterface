@echo off
set DIR=%~dp0
set APP_BASE_NAME=%~n0
set DEFAULT_JVM_OPTS=-Xmx64m -Xms64m

set CLASSPATH=%DIR%gradle\wrapper\gradle-wrapper.jar

if not defined JAVA_HOME goto findJavaFromPath
set JAVA_EXE=%JAVA_HOME%\bin\java.exe
if exist "%JAVA_EXE%" goto init

echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
goto fail

:findJavaFromPath
set JAVA_EXE=java.exe
goto init

:init
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% -Dorg.gradle.appname=%APP_BASE_NAME% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
goto end

:fail
exit /b 1

:end
