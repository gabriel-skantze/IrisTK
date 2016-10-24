set JAVA_HOME="C:\Program Files\Java\jre1.8.0_60"
call "%VS110COMNTOOLS%vsvars32.bat"
rmdir /q /s work
md work
copy jni4net*.* work
copy IrisTK.Net.Kinect\bin\Release\*.dll work
proxygen.exe work\IrisTK.Net.Kinect.dll -wd work -dp work\Microsoft.Kinect.dll
cd work
call build.cmd

copy Microsoft.Kinect.dll ..\..\lib

copy IrisTK.Net.Kinect.dll ..\..\lib
copy IrisTK.Net.Kinect.j4n.dll ..\..\lib
copy IrisTK.Net.Kinect.j4n.jar ..\..\lib

pause