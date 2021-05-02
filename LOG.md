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
* Bug fix:
1. fixed inner-string parsed bug & added new tests for it
2. fixed symbol in car&cdr functioning bugs
* Performance improved:
deserialize performance improved
* `class name` changed:
change `InValidSyntaxException` to `InvalidSyntaxExeption`

## 2020-10-26
* Add `STool` class and the `toJSON` function
* Move `minimize` function from the `SObjParser` class into the `STool` class
* Add the `nullValueTest` test
* Update `README.md` file: add `toJSON` usage

## 2020-10-28
* Code structured
* Add `listIndex` function to SObjNode functionality
* Add `deep` function, and it's test
* Add `beautify` function for SObj

## 2020-12-26
* Switched from `SObjNOde` to `SObjTable` that improved the query performance.

## 2021-05-02
* Added support for Primitive-Object parse.
* primitive-type array serialize bug fixed.
* difference type in array serialize bug fixed.

## later