## 2020-10-7
* Changed:
removed the `toAST` function, because parse to `AST` is alternative.
remove it will decrease the JVM memory.
 
* Memory usage:
make less memory usage. see above for details.

## 2020-10-13
* Improve serialize & deserialize performance:
by changing the `String.format` to `StringBuilder.append()...`.

## later