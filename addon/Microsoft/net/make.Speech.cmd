set JAVA_HOME="C:\Program Files\Java\jre1.8.0_60"

call "%VS110COMNTOOLS%vsvars32.bat"
rmdir /q /s work
md work
copy jni4net*.* work
copy IrisTK.Net.Speech\bin\Release\IrisTK.Net.Speech.dll work
proxygen.exe work\IrisTK.Net.Speech.dll -wd work
cd work
call build.cmd

copy IrisTK.Net.Speech.dll ..\..\lib
copy IrisTK.Net.Speech.j4n.dll ..\..\lib
copy IrisTK.Net.Speech.j4n.jar ..\..\lib

pause