# elf4j-provider

A native log _service provider_ implementation of [ELF4J](https://github.com/elf4j/elf4j) (Easy Logging Facade for
Java), and a complete drop-in logging solution for any Java application

## User Story

As an application developer using the ELF4J logging facade, I want to have the option of using a runtime log _service
provider_ that natively implements
the [API and SPI](https://github.com/elf4j/elf4j#logging-service-interface-and-access-api) of ELF4J.

## Prerequisite

Java 8 or better

## Implementation Notes

* Guiding principle: Reasonable default and Pareto's 80/20 rule

* This is simply a packaging unit of the [elf4j-engine](https://github.com/elf4j/elf4j-engine), using the
  Java [Service Provider Framework](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) mechanism.
  See [elf4j-engine](https://github.com/elf4j/elf4j-engine) for implementation details.

## Features

* Async Logging Only

  Logging output is always asynchronous, considering performance and moreover the 80/20 rule: When was the last time a
  use case truly required that logging had to be synchronous, and always blocking the application's normal work flow?

* Standard Streams Output Only

  Besides the standard streams (stdout/stderr), it may be trivial for the application logging to support other output
  channels. Yet it's arguably more trivial for the hosting system to redirect/forward standard streams as a data source
  to other destinations than the system Console, e.g. to log files and/or other central repositories. Such log data
  aggregation can be as simple as a Linux shell redirect or sophisticated as collector agents of
  monitoring/observability services, but not a concern of the application-level logging.

* Logging Format Patterns Including JSON

  JSON is a supported output pattern, in hopes of helping external log analysis tools. This is in addition to the usual
  line-based patterns - timestamp, level, thread, class, method, file name, line number, and log message. The JSON
  pattern can either be the only output of the log entry, or mixed together with other patterns.

* Configuration Refresh at Runtime

  Supports configuration refresh during runtime via API, with option of passing in replacement properties instead of
  reloading the configuration file. The most frequent use case would be to change the minimum log output level, without
  restarting the application.

## Get It...

[![Maven Central](https://img.shields.io/maven-central/v/io.github.elf4j/elf4j-provider.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.elf4j%22%20AND%20a:%22elf4j-provider%22)

## Use It...

As with any other [ELF4J](https://github.com/elf4j/elf4j) logging provider, client application should code
against [service API](https://github.com/elf4j/elf4j#service-interface-and-access-api) of the ELF4J facade, and drop in
this provider implementation as a runtime-scope dependency in Maven or other build tools alike:

```html
...
<dependency>
    <groupId>io.github.elf4j</groupId>
    <artifactId>elf4j</artifactId>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>io.github.elf4j</groupId>
    <artifactId>elf4j-provider</artifactId>
    <scope>runtime</scope>
</dependency>
...
```

In case of multiple ELF4J service providers in classpath, pick this one like so:

```
java -Delf4j.logger.factory.fqcn="elf4j.engine.NativeLoggerFactory" MyApplication
```

More details [here](https://github.com/elf4j/elf4j#no-op-by-default).

### API Sample Usage

See ELF4J facade [usage sample](https://github.com/elf4j/elf4j#use-it---for-logging-service-api-clients).

### Configuration

**Properties File Configuration Only**

The default configuration file location is at the root of the application class path, with file
name `elf4j-test.properties`, or if that is missing, `elf4j.properties`. Alternatively, to override the default
location, use Java system property to provide an absolute path:

```
java -Delf4j.properties.location=/absoloute/path/to/myConfigurationFile -jar MyApplicaiton.jar
``` 

Absence of a configuration file results in no logging (no-op) at runtime. When present, though, the configuration file
requires zero/no configuration thus can be empty: the default configuration is a stdout writer with a minimum level
of `TRACE` and a basic line-based logging pattern. To customize the default logging configuration, see the
configuration sample file below.

**Level**

The default minimum output level is `TRACE`, which can be configured on global, package, or individual class level of
the caller classes. The default severity level of a logger instance from `Logger.instance()` is `INFO`, which is not
configurable: Use the ELF4J [API](https://github.com/elf4j/elf4j#logging-service-interface-and-access-api) to switch 
`Logger` levels per application needs.

**Writer**

Supports multiple writers; a log entry will be output by each writer once per configuration. Each writer can have 
individual configurations on minimum output level, format pattern, and type of out stream (stdout/err/auto). However,
given the comprehensive support on writer patterns and various minimum output levels per caller classes, more than 
one stream writer is rarely necessary.

**Output Format Pattern**

* timestamp: Format configurable per Java
  `DateTimeFormatter` [pattern syntax](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns),
  default to ISO local datetime
* level: Length configurable, default to full length
* thread: Option of thread name or id, default to name
* class: Option of simple, full, or compressed (only the first letter for a package segment), default to full
* method: No configuration options, simple method name
* file name: No configuration options, simple file name
* line number: No configuration options, where the log is issued in the file
* log message: No configuration options, always prints user message, and exception stack trace if any
* json: Options to include thread (name, id) and caller (method, line number, file name) details and pretty-print the
  JSON string, default is no thread/caller detail and the minified single-line format

**Output samples**

Line-based Default

* Pattern: none

* Output:

  `2023-03-22T21:11:33.040-05:00 INFO elf4j.engine.IntegrationTest$defaultLogger - Hello, world!`

Line-based Customized

* Pattern:
  ```
  {timestamp:yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ} {level:5} [{thread:name}] {class:compressed}#{method} - {message}
  ```
* Output:

  `2023-03-22T21:14:24.051-05:00 INFO  [main] e.e.IntegrationTest$defaultLogger#hey - Hello, world!`

JSON Default

* Pattern:
  ```
  {json}
  ```
* Output:
  ```
  {"timestamp":"2023-03-22T21:21:29.7319284-05:00","level":"INFO","callerClass":"elf4j.engine.IntegrationTest$defaultLogger","message":"Hello, world!"}
  ```

JSON Customized

* Pattern:
  ```
  {json:caller-thread,caller-detail,pretty}
  ```
* Output:
  ```json
  {
    "timestamp": "2023-03-14T21:21:33.1180212-05:00",
    "level": "INFO",
    "callerThread": {
      "name": "main",
      "id": 1
    },
    "callerDetail": {
      "className": "elf4j.provider.IntegrationTest$defaultLogger",
      "methodName": "hey",
      "lineNumber": 41,
      "fileName": "IntegrationTest.java"
    },
    "message": "Hello, world!"
  }
  ```

**Sample Configuration File**

* When in doubt, use lower-case.

```properties
### Zero configuration mandatory, this file can be empty - default to a line-based writer with simple log pattern
### global no-op flag, overriding and will turn off all logging if set true
#noop=true
### Minimum output level is optional, default to TRACE if omitted
level=info
### These override the output level of all caller classes included the specified packages
level@org.springframework=warn
level@org.apache=error
### Writer is optional, default to a simple standard streams writer
### Global standard out stream type - stdout/stderr/auto - default to stdout. auto means to use stdout if severity level is lower than WARN, otherwise use stderr
standard.stream=stderr
### standard out stream is the only supported writer type
writer1=standard
### Writer stream type if present overrides global type. If no stream type configured at either global or writer level, default to stdout
writer1.stream=auto
### This is the default output pattern, can be omitted
#writer1.pattern={timestamp} {level} [{thread}] {class} - {message}
### This would customize the format patterns of the specified writer
#writer1.pattern={timestamp:yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ} {level:5} [{thread:name|id}] {class:simple|full|compressed} - {message}
### Multiple writers are supported, each with its own configurations
writer2=standard
#writer2.level=trace
### Default json pattern does not include thread and caller details, and uses minified one-line format for the JSON string
#writer2.pattern={json}
### This would force the JSON to include the thread/caller details, and pretty print
writer2.pattern={json:caller-thread,caller-detail,pretty}
```

**Configuration Refresh**

`ServiceConfigurationManager.refreshConfiguration()` will reload the configuration file and apply the latest file
properties during runtime. `ServiceConfigurationManager.refreshConfiguration(Properties)` will apply the passed-in
properties as the complete configuration replacement, and the configuration file will be ignored.
