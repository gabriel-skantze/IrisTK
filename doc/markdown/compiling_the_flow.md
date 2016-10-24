## Compiling the flow

The flow is defined using XML, which is then compiled into Java source code with a FlowCompiler. The Java code is then compiled into Java byte code with the Java compiler. However, FlowCompiler can also do this step for you, and it can be easily used with the iristk.exe command. Let's take the ChessFlow.xml in the chess app as an example. You can easily compile it from the command line like this:

```
cd %IrisTK%\app\chess
iristk cflow -b ChessFlow.xml
```
 
The -b flag informs the compiler to also compile into a Java class (not just the source), but it can be omitted (since Eclipse can take care of this step anyway). The ChessFlow.xml file is not located directly under app\chess. Instead, it must be located where the Java source file should be created (app\chess\src\iristk\app\chess). However, the FlowCompiler will automatically find this file and place both the .Java and .class files in the right locations. As you can see there is also a build.xml file in the chess folder that contains a task for doing this (which might be more convenient if using Eclipse).

Once the flow is compiled, you can use the FlowModule class to add it as a module to the IrisSystem:

```java
flow = new ChessFlow();
iris.addModule("flow", new FlowModule(flow));
```