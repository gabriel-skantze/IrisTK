set JAVA_HOME="C:\Program Files (x86)\Java\jre1.8.0_60"
call "%VS110COMNTOOLS%vsvars32.bat"
rmdir /q /s work
md work
copy jni4net*.* work
copy IrisTK.Net.Kinect2\x86\*.dll work
copy IrisTK.Net.Kinect2\Face\x86\*.dll work
cd work
..\proxygen32.exe IrisTK.Net.Kinect2.dll -wd . -dp Microsoft.Kinect2.dll;Microsoft.Kinect.Face.dll
call build.cmd

copy IrisTK.Net.Kinect2.dll ..\..\lib\x86
copy IrisTK.Net.Kinect2.j4n.dll ..\..\lib\x86
copy IrisTK.Net.Kinect2.j4n.jar ..\..\lib

pause