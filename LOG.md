## 2020-10-7
* Changed:
removed the `toAST` function, because parse to `AST` is alternative.
remove it will decrease the JVM memory. 
* Memory usage:
make less memory usage. see above for details.

## 2020-10-13
* Improve serialize & deserialize performance:
by changing the `String.format` to `StringBuilder.append()...`.

## 2020-10-15
* Bug fix
fixed inner-string parsed bug & added new tests for it
* Performance improved
deserialize performance improved
* `class name` changed
change `InValidSyntaxException` to `InvalidSyntaxExeption`

## later