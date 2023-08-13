# Code Structure

## Prerequisites

- tested on these versions
    - java -version
        - openjdk version "18.0.1" 2022-04-19
        - OpenJDK Runtime Environment Corretto-18.0.1.10.1 (build 18.0.1+10-FR)
        - OpenJDK 64-Bit Server VM Corretto-18.0.1.10.1 (build 18.0.1+10-FR, mixed mode, sharing)
    - dot -V
        - dot - graphviz version 8.0.2 (20230410.1723)
    - mvn -v
        - Maven home: /usr/local/Cellar/maven/3.9.1/libexec
        - Java version: 18.0.1, vendor: Amazon.com Inc., runtime:
          /Library/Java/JavaVirtualMachines/amazon-corretto-18.jdk/Contents/Home
        - Default locale: en_US, platform encoding: UTF-8
        - OS name: "mac os x", version: "12.6.8", arch: "x86_64", family: "mac"

## Commands

- how to test
    - `mvn test`
- how to build
    - `mvn package`
- how to run
    - `java -jar console/target/code-structure-console.jar local-config/my-config.json`

## Configuration Example

```json
{
  "inputDir": "/Users/me/github.com/my-gitlab-id/my-project",
  "outputDir": "generated/my-project",
  "sourcePrefix": "https://github.com/my-gitlab-id/my-project/blob/staging/",
  "reportStyleName": "simple",
  "binary": {
    "includeRegexPatterns": [
      "_build/dev/lib/my_project/.*\\.beam"
    ],
    "excludeRegexPatterns": []
  },
  "source": {
    "includeRegexPatterns": [
      ".*\\.ex",
      ".*\\.exs"
    ],
    "excludeRegexPatterns": []
  }
}
```

## Configuration

- inputDir
    - location of your source code and binaries
    - you must build your project before running this analysis
- outputDir
    - location of reports and logs
    - you can find the final report at
        - $outputDir/report/index.html
- sourcePrefix
    - link to version control
    - this is prepended to the source code file path relative to inputDir
- reportStyleName
    - simple or table
    - determines how much detail there is in each graph node
- binary
    - includeRegexPatterns
        - list of regular expressions that match which binary files should be scanned
    - excludeRegexPatterns
        - list of regular expressions that match which binary files should not be scanned
- source
    - includeRegexPatterns
        - list of regular expressions that match where source code files are located
    - excludeRegexPatterns
        - list of regular expressions that match where source code files are not located
