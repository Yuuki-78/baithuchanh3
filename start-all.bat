@echo off
title Khoi chay He thong SOA (Bai 2 va Bai 3)

echo ==========================================
echo Dang khoi chay Auth Service (Port 9000)...
cd /d "%~dp0authservice"
start cmd /k "title Auth Service (9000) && sbt run"

echo Dang doi 5 giay de Auth Service khoi dong xong...
timeout /t 5 /nobreak > nul

echo ==========================================
echo Dang khoi chay Product Service (Port 9001)...
cd /d "%~dp0productservice"
start cmd /k "title Product Service (9001) && sbt -Dhttp.port=9001 run"

echo ==========================================
echo Da kich hoat xong tat ca cac service!
pause