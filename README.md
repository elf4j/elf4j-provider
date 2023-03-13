[![](https://img.shields.io/static/v1?label=github&message=repo&color=blue)](https://github.com/elf4j/elf4j-impl)

# elf4j-impl

A native logging service provider implementation of [ELF4J](https://github.com/elf4j/elf4j) (Easy Logging Facade for
Java)

## User Story

As an application developer using the ELF4J logging service API, I want to select a logging service provider that
natively implements the ELF4J API, so that I can opt to use it at application deploy time, without code change, as with
any other ELF4J logging service providers.

## Prerequisite

Java 8 or better

## Implementation Notes

* Guiding principle: Reasonable default and Pareto's 80/20 rule

## Features

* Async Logging Only

  Logging output is always asynchronous, considering performance and moreover the 80/20 rule: When was the last time a
  use case truly required that logging had to be synchronous, and always blocking the application's normal work flow?

* Console Output Only

  The thought of console output only is the 80/20 principle, for one. Secondly, no matter the application is hosted
  on-prem or in Cloud, it's nowadays trivial to forward standard streams (stdout/stderr) as a data source to files or
  other types of central repositories. This is usually done by system-level data collector agents - Fluentd/Fluent Bit,
  ELK, DataDog, and New Relic, to name a few - and no longer a concern of application-level logging.

* Logging Format Patterns Including JSON

  JSON is a supported output pattern, in hopes of helping external log analysis tools. This is in addition to the usual
  line-based patterns - timestamp, level, thread, class, method, file name, line number, and log message. The JSON
  pattern can either be the only output of the log entry, or mixed together with other patterns.

* Configuration Refresh at Runtime

  Provides API to support configuration refresh during runtime, with option of passing in overriding properties in
  addition to reloading the configuration file. The most frequent use case would be to change the minimum log output
  level, without restart of the application.

## Get It...

[![Maven Central](https://img.shields.io/maven-central/v/io.github.elf4j/elf4j-impl.svg?label=Maven%20Central)](https://central.sonatype.com/search?smo=true&q=pkg%253Amaven%252Fio.github.elf4j%252Felf4j-impl)

## Use It...

### Usage Sample

See the ELF4J [usage sample](https://github.com/elf4j/elf4j#for-logging-service-api-users).

### Configuration

* Properties File Configuration Only

  The default configuration file location is at the root of the application class path, with file
  name `elf4j-test.properties`; or if that is missing, then `elf4j.properties`. Alternatively, to override the default
  location, use Java system property to provide an absolute path:

  ```
  java -Delf4j.properties.location=/absoloute/path/to/myConfigurationFile -jar MyApplicaiton.jar
  ``` 

  Absence of a configuration file results in no logging (no-op) at runtime. When present, the configuration file
  requires no/zero configuration thus can be empty - the default configuration is a stdout writer with a minimum level
  of `TRACE` and a basic line-based logging pattern. To customize the default logging configuration, see the
  configuration sample file below.

* Level

  The default global logger minimum output level is `TRACE`. Supports global and package level customizations.

* Writer

  Supports multiple console writers. Each writer can have individual configurations on output level and format pattern.
  However, with the comprehensive configuration support on a single writer's pattern and levels per various loggers,
  more than one console writer is rarely necessary.

* Output Format Pattern
    * timestamp: Format configurable per Java
      DateTimeFormatter [pattern syntax](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns),
      default to ISO local datetime
    * level: Length configurable, default to full length
    * thread: Option of thread name or id, default to name
    * class: Option of simple, full, or compressed (only the first letter for a package segment), default to full
    * method: Not configurable, simple method name
    * file name: Not configurable, simple file name
    * line number: Not configurable, where the log is issued in the file
    * log message: Not configurable, always prints user message, and exception stack trace if any
    * json: Options to include thread and caller (method, line number, file name) details and minify the JSON string,
      default to no thread/caller detail and pretty print format

* Output samples of default patterns
    * Line-based
      ```
      2023-03-12T22:59:47.995-05:00 INFO [main] elf4j.impl.IntegrationTest$defaultLogger - Hello, world!
      ```
    * JSON
      ```json
      {
        "timestamp": "2023-03-12T22:59:47.9951759-05:00",
        "level": "INFO",
        "callerClass": "elf4j.impl.IntegrationTest$defaultLogger",
        "message": "Hello, world!"
      }
      ```

* Sample Configuration File
    * When in doubt, use lower-case.

  ```properties
  ### Any level is optional, default to TRACE if omitted
  ### This override the default global level
  level=info
  ### Global console output stream type, default to stdout; cannot differ per individual writers
  #console.out.stream=stderr
  ### These override level of all caller classes included the specified package
  #level@elf4j.impl=error
  level@org.springframework=warn
  ### Any writer is optional, default to a single console writer if no writer configured
  writer1=console
  ### This is the default output pattern, can be omitted
  writer1.pattern={timestamp} {level} [{thread}] {class} - {message}
  ### This would customize the format patterns of the specified writer
  #writer1.pattern={timestamp:yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ} {level:5} [{thread:name|id}] {class:simple|full|compressed}: {message}
  ### Multiple writers are supported, each with its own configurations
  writer2=console
  #writer2.level=trace
  ### Default json pattern does not include thread and caller details, and uses pretty print format for the JSON string
  #writer2.pattern={json}
  ### This would force the JSON to include the thread/caller details
  writer2.pattern={json:caller-thread,caller-detail}
  ### This would minify the JSON string from the pretty print format
  #writer2.pattern={json:caller-thread,caller-detail,minify}
  ### This would force the writer to use stderr instead of stdout
  writer3=console
  writer3.pattern={json}
  ```