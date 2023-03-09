[![](https://img.shields.io/static/v1?label=github&message=repo&color=blue)](https://github.com/elf4j/elf4j-impl)

# elf4j-impl

A native implementation of [ELF4J](https://github.com/elf4j/elf4j) (Easy Logging Facade for Java)

## User Story

As ELF4J logging service API user, I want to be able to select a logging service provider that natively implements the
ELF4J API, so that I can opt to use it at application deploy time, without code change, as with any other ELF4J logging
service providers.

## Prerequisite

Java 8 or better

## Implementation Notes

- Guiding principle: Reasonable default and Pareto's 80/20 rule

## Features

- Async Logging Only

  Logging output is always asynchronous, considering performance and moreover the 80/20 rule - When was the last time a
  use case truly required logging had to be synchronous, and always blocking the application's normal work flow?

- Console Output Only

  Supports multiple console writers with different configurations. A thought of console only is the 80/20 principle.
  Plus, it's trivial nowadays to forward stdout/stderr streams as a data source to various other types of central
  repositories. This is usually via system level data collector agents - Fluentd/Fluent Bit, ELK, DataDog, Newrelic...
  to name a few.

- Logging Format Patterns

  The usually expected logging format patterns (timestamp, level, thread, class, method, file name, line number, log
  message) and JSON

- Configuration Refresh at Runtime

  API to support refresh configuration at runtime, with option of passing in overriding properties in addition to reload
  of the configuration file. Most frequent use case would be to change the minimum log output level, without restart of
  the application.

## Get It...

[![Maven Central](https://img.shields.io/maven-central/v/io.github.elf4j/elf4j-impl.svg?label=Maven%20Central)](https://central.sonatype.com/search?smo=true&q=pkg%253Amaven%252Fio.github.elf4j%252Felf4j-impl)

## Use It...

### Usage Sample

See the ELF4J [usage sample](https://github.com/elf4j/elf4j#for-logging-service-api-users).

### Configuration

- Properties File Configuration Only

  The default configuration file location is at the root of the application class path, with file
  name `elf4j-test.properties`; or if that is missing, then the `elf4j.properties`. Alternatively, to override the
  default, the configuration file location can be specified via Java system property as an absolute path:

  ```
  java -Delf4j.properties.location=/absoloute/path/to/myConfigurationFile -jar MyApplicaiton.jar
  ``` 

- Level

  Default global minimum output level is TRACE if not configured. Supports global and package level overrides.

- Writer

  Supports multiple console writers, each with individual configurations on output level, stream type (stdout vs.
  stderr), and format pattern.

- Output Format Pattern
    - timestamp: Format configurable per Java DateTimeFormatter syntax, default to ISO local datetime
    - level: Length configurable, default to full length
    - thread: Option of thread name or id, default to name
    - class: Option of simple, full, or compressed (only the first letter for a package segment), default to full
    - method: Not configurable
    - file name: Not configurable
    - line number: Not configurable
    - log message: Not configurable, always prints user message, and exception stack trace if any
    - json: Options to include thread and caller (method, line number, file name) details and minify the JSON string,
      default to no thread/caller detail and pretty print format

- Sample Configuration File
    - When in doubt, use lower-case

  ```properties
  ## Any level is optional, default to TRACE if omitted
  ## This override the default global level
  level=info
  ## These override level of all caller classes included the specified package 
  level@elf4j.impl=error
  level@org.springframework=warn
  ## Any writer is optional, default to a single console writer if no writer configured
  writer1=console
  ## This is the default output pattern
  writer1.pattern={timestamp} {level} [{thread}] {class} - {message}
  ## This would customize the format patterns of the specified writer
  #writer1.pattern={timestamp:yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ} {level:5} [{thread:name|id}] {class:simple|full|compressed}: {message}
  ## Multiple writers are supported, each with its own configurations
  writer2=console
  writer2.level=trace
  ## Default json pattern does not include thread and caller details, and uses pretty print format for the JSON string
  writer2.pattern={json}
  ## This would force the JSON to include the thread/caller details
  #writer2.pattern={json:thread-detail,caller-detail}
  ## This would minify the JSON string from the pretty print format
  #writer2.pattern={json:thread-detail,caller-detail,minify}
  ## This would force the writer to use stderr instead of stdout
  #writer2.stream=stderr
  ```