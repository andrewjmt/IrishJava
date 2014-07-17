Welcome to IrishJava! IrishJava can solve all your random bit-flipping needs in Java with just a small addition to your JVM arguments. For every method call, IrishJava will randomly flip some of the bits in the arguments and return values. (This is performed recursively on object fields.)  
  
To run a program with IrishJava, format your java command as follows:  
java -javaagent:full/path/to/irishjava.jar ExampleProgram  
  
You can optionally specify a per-bit flip probability (0.01 by default):  
java -javaagent:full/path/to/irishjava.jar=0.005 ExampleProgram
