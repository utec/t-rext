![coverage](https://i.ibb.co/NthWkg9/coverage-status.png)

![https://i.ibb.co/QDCfKkm/trext-150.png](https://i.ibb.co/QDCfKkm/trext-150.png)

T-Rext is a java framework which allows you to automate rest APIs with a rich set of fluent assertions. Its natural assertions and truly helpful error messages improves test code readability and is designed to be super easy for even non-programmers.


# Requirements

- Java 8
- A healthy rest endpoint. 
    - https://api.agify.io/?name=Richard
    - response `{"name":"richard","age":58,"count":95810}`
- A scenario like: 
    - If name is **Richard**, the age must be **58**      

# Install

- Download the latest version from [here](https://drive.google.com/file/d/1pgD5clWrQWEWyFITFtse_kGPU4n7CPy-/view) and zip on any folder
- Unzip it in a folder without spaces in name
- Open a shell and execute:

```
sudo bash install.sh
```

# Steps

- Create some folder like: `/tmp/hello_world`
- Create a feature inside the recently created folder like: `name_predictor.feature`
- Copy and paste the following feature

```
Feature:   Age validation
Ensure that api returns the correct users age

Scenario: Richard's age must be 58
url https://api.agify.io/?name=Richard
method get
asserts
assertThat $.age isEqualTo 58
```

- Execute:

```
java -jar t-rext.jar -mode auto -directory /tmp/hello_world
```



- That's all! You must see an html report in the main folder:

![https://i.ibb.co/4RGbgjx/success-report.png](https://i.ibb.co/4RGbgjx/success-report.png)

# Features

- One feature file could contain several scenarios
- One scenario could use previously saved variables
- Assert validation use [JSON-PATH](https://jsonpath.com/) expressions
- Load global variables with **-variables** parameter
- You could use global variables in any part of your feature files. Example:
  - Rest Api urls
  - Header parameters
  - Query, url and body parameters
- If folder contains several feature files, all of them will be executed. Report changes slightly.
- If exist just one feature, report includes log and metadata in case of error.
- If feature contains several scenarios, you can disabled specific scenario with `disabled true` at scenario level.
- You can comment any line of feature with 	`#`

# Natural Assertions

| assert              | description | example  |
|---------------------|-------------|----------|
| assertThat | mandatory first assert            | assertThat 7 isEqualTo 7 |
| isTrue | looks for a true value  | assertThat $.exist isTrue
| isFalse | looks for a false value | assertThat $.isHero isFalse
| isEqualTo | looks for exact match | assertThat $.content.name isEqualTo "Jane" |
| startsWith | validate if string starts with some value  | assertThat $.content.job startsWith "Dev" |
| endsWith | validate if string value ends with some value  | assertThat "aeiou" endsWith "u" |
| doesNotStartWith | opposite of startsWith | assertThat $.content.job doesNotStartWith "#"  |
| doesNotEndWith | opposite of endsWith | assertThat "aeiou" doesNotEndWith "x" | |
| contains | validate if string contains some value  | assertThat "aeiou" contains "ei" |
| doesNotContain | validate if string does not contain some value  | assertThat "aeiou" doesNotContain "bc" |
| containsOnlyOnce | validate if string contains some value, just one time | assertThat "aeiou" containsOnlyOnce "a" |
| isSubstringOf | validate if string is part of another string | assertThat "ch" isSubstringOf "JRichardsz" |
| doesNotContainAnyWhitespaces | validate if string does not contain any blank space | assertThat $.token doesNotContainAnyWhitespaces |
| isEmpty | validate if string is empty | assertThat $.alias isEmpty |
| isNullOrEmpty | validate if string is null or empty | assertThat $.alias isNullOrEmpty |
| isNotEmpty | opposite of isEmpty | assertThat $.name isNotEmpty |
| isNotNull | similar to isNotEmpty | assertThat $.lastname isNotNull |
| containsOnlyDigits | validate if string contains only numbers | assertThat $.age containsOnlyDigits
| isEqualToIgnoringCase | similar of isEqualTo but without case |  assertThat "PLUSULTRA" isEqualToIgnoringCase "plusultra" |
| isGreaterThan | this > value |  assertThat $.age isGreaterThan 4 |
| isLessThan | this < value |  assertThat $.age isLessThan 10 |
| isGreaterThanOrEqualTo | this >= value |  assertThat $.age isGreaterThanOrEqualTo 5 |
| isLessThanOrEqualTo | this <= value |  assertThat $.age isLessThanOrEqualTo 10 |

# Special variables

- ${srand} = random string
- ${irand} = random integer
- ${drand} = random double

# Advanced settings

More details in [wiki](#)

# TO DO

- Apply cobertura maven plugin
- composed variables like ${book_id}-latest in asserts y context
- Add more asserts from: [assertj](https://joel-costigliola.github.io/assertj/core-8/api/org/assertj/core/api/AbstractComparableAssert.html) 

# Acknowledgments

- [Logo licence](https://creazilla.com/nodes/11735-dinosaur-meal-clipart)
- https://assertj.github.io/doc/


# Note:

- At this moment coverage does not pass the 90 threshold, so jar can not be created. Jar is created removing coverage thresholds
