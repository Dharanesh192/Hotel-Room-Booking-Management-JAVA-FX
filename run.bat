@echo off
setlocal

set "JAVAFX_LIB=javafx-sdk-25\lib"
set "POSTGRES_JAR=PostgreSQL\postgresql-42.7.3.jar"
set "SRC_DIR=Code"
set "OUT_DIR=Compiled"
set "MAIN_CLASS=MainView"

if not exist "%JAVAFX_LIB%" (
    echo [ERROR] JavaFX library not found at "%JAVAFX_LIB%".
    exit /b 1
)

if not exist "%POSTGRES_JAR%" (
    echo [ERROR] PostgreSQL JDBC jar not found at "%POSTGRES_JAR%".
    exit /b 1
)

if not exist "%OUT_DIR%" (
    mkdir "%OUT_DIR%"
)

echo Compiling Java files...
javac --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -cp "%POSTGRES_JAR%" -d "%OUT_DIR%" "%SRC_DIR%\*.java"
if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)

echo Running application...
java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -cp "%OUT_DIR%;%POSTGRES_JAR%" %MAIN_CLASS%

endlocal
