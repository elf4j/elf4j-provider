# elf4j-impl

A native implementation of [ELF4J](https://github.com/elf4j/elf4j) (Easy Logging Facade for Java)

## User Story

As an ELF4J facade API user, I want to use an independent logging provider that natively implements the ELF4J service
API.

## Prerequisite

Java 8 or better

## Implementation Notes

- Guiding principle: Reasonable default and Pareto's 80/20 rule

## Features

- Async Logging Only

  Output is always asynchronous with a single writer thread. Considerations on async only include performance and the
  same guiding principle. Mainly, though, when was the last time logging really needed to be synchronous, and blocking
  the application's normal work flow?

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

## Use It...

### Usage Sample

See the ELF4J [usage sample](https://github.com/elf4j/elf4j#for-logging-service-api-users).

### Configuration

- Properties file configuration only

  The default configuration file location is `elf4j-test.properties`; if missing, the `elf4j.properties`. The file has
  to be placed at the root of the class path. Alternatively, the configuration file location can be specified via Java
  system property as an absolute path:

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
    - use lower-case when in doubt

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