@echo off
echo %time%
mvn clean package shade:shade
timeout 5 > NUL
echo %time%
