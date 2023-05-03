# elf4j-provider

The native logging _service provider_ implementation of [ELF4J](https://github.com/elf4j/elf4j) (Easy Logging Facade for
Java), and an independent drop-in logging solution for any Java application

## User Story

As an application developer using the ELF4J logging facade, I want to have the option of using a runtime log _service
provider_ that natively implements
the [API and SPI](https://github.com/elf4j/elf4j#log-service-interface-and-access-api) of ELF4J.

## Prerequisite

Java 8 or better

## Implementation Notes

* Guiding principle: Reasonable default and Pareto's 80/20 rule

* This is simply a packaging unit of the [elf4j-engine](https://github.com/elf4j/elf4j-engine), using the
  Java [Service Provider Framework](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) mechanism.
  See [elf4j-engine](https://github.com/elf4j/elf4j-engine) for implementation details.

## Installation

[![Maven Central](https://img.shields.io/maven-central/v/io.github.elf4j/elf4j-provider.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.elf4j%22%20AND%20a:%22elf4j-provider%22)

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

## Features

### Async Logging Only

Logging output is always asynchronous, considering performance and moreover the 80/20 rule: When was the last time a use
case truly required that logging had to be synchronous, and always blocking the application's normal work flow?

### Standard Streams Output Only

Besides the standard streams (stdout/stderr), it may be trivial for the application logging to support other output
channels. Yet it's arguably more trivial for the hosting system to redirect/forward standard streams as a data source to
other destinations than the system Console, e.g. log files and/or other central repositories. Such log data aggregation
process may be as simple as a Linux shell redirect, or as sophisticated as running collector agents of comprehensive
monitoring/observability services, but no longer a concern of the application-level logging.

### Log Patterns Including JSON

JSON is a supported output pattern, in hopes of helping external log analysis tools. This is in addition to the usual
line-based patterns - timestamp, level, thread, class, method, file name, line number, and log message.

### Configuration Refresh at Runtime

Supports configuration refresh during runtime via API, with option of passing in replacement properties instead of
reloading the configuration file. The most frequent use case would be to change the minimum log output level, without
restarting the application.

## Usage

* As with any other [ELF4J](https://github.com/elf4j/elf4j) logging provider, client application should code
  against [service API](https://github.com/elf4j/elf4j#service-interface-and-access-api) of the ELF4J facade, and drop
  in this provider implementation as a runtime dependency shown in the "Installation" section.

* See ELF4J for [API sample usage](https://github.com/elf4j/elf4j#use-it---for-log-service-api-clients).

* In case of multiple ELF4J service providers in classpath, pick this one like so:
  ```
  java -Delf4j.logger.factory.fqcn="elf4j.engine.NativeLoggerFactory" MyApplication
  ```  
  More details [here](https://github.com/elf4j/elf4j/blob/main/README.md#only-one-in-effect-logging-provider).

## Configuration

### Properties File

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

### Level

The default severity level of a `Logger` instance from `Logger.instance()` is `INFO`, which is not configurable: if
needed, use the ELF4J [API](https://github.com/elf4j/elf4j#logging-service-interface-and-access-api) to switch levels
on `Logger` instances. By contrast, the level of minimum output for both log writers and caller classes is configurable,
with the default of `TRACE`. For log writers, the default level can be configured/overridden per each writer. For caller
classes, the minimum output level can be configured on global, packages, or individual classes. A log output is only
rendered when the `Logger` instance severity level is on or above the configured minimum output levels of both the log
writer and the caller class.

### Writer

The elf4j-engine supports multiple standard-stream writers. Each writer can have individual configurations on format
pattern, minimum output level, and type of out stream (stdout/err/auto). The same log entry will be output once per each
writer. Practically, however, more than one writer is rarely necessary given what a single writer can achieve with the
comprehensive support on log patterns and minimum output levels per caller classes.

### Output Format Pattern

All individual patterns, including the JSON pattern, can either be the only output of the log entry, or mixed together
with any other patterns. They take the form of `{pattern:displayOptions}`, where multiple display options are separated
by commas. Patterns inside curly brace pairs are predefined and will be interpreted before output, while patterns
outside curly brace pairs are output verbatim. The predefined patterns are:

* `timestamp`: Date time format configurable via Java
  `DateTimeFormatter` [pattern syntax](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns),
  default to ISO datetime format with time zone offset of the application running host
* `level`: Length configurable, default to full length
* `thread`: Option of `name` or `id`, default to name
* `class`: Option of `simple`, `full`, or `compressed` (only the first letter for a package segment), default
  to `simple`
* `method`: No configuration options, simple method name
* `filename`: No configuration options, simple file name
* `linenumber`: No configuration options, where the log is issued in the file
* `sysprop`: Option `name` for the JRE System Property
* `sysenv`: Option `name` for the system environment variable
* `message`: No configuration options, always prints user message, and exception stack trace if any
* `json`: Multiple options allowed - `caller-thread` to include caller thread (name and id), `caller-detail` to include
  caller stack detail (class, method, filename, linenumber), and option `pretty` to indent the JSON text to more
  readable format. Default is no thread/caller detail and the minified single-line format

**Pattern Output Samples**

Line-based Default

* Pattern: none, which is the same as

  ```
  {timestamp} {level} {class} - {message}
  ```

* Output:

  ```
  2023-03-22T21:11:33.040-05:00 INFO IntegrationTest$defaultLogger - Hello, world!
  ```

Line-based Customized

* Pattern:
  ```
  {timestamp:yyyy-MM-dd'T'HH:mm:ss.SSSXXX} {level:5} [{thread:name}] {class:compressed}#{method}(L{linenumber}@{filename}) -- {message}
  ```
* Output:
  ```
  2023-03-30T17:14:54.735-05:00 INFO  [main] e.e.IntegrationTest$defaultLogger#hey(L50@IntegrationTest.java) -- Hello, world!
  ```

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
### Standard out stream type, stdout or stderr, default is stdout
stream=stderr
### Optional default override of writer pattern
#pattern={json}
### standard is the only supported writer type
writer1=standard
### This is the default output pattern, can be omitted
#writer1.pattern={timestamp} {level} {class} - {message}
### This would customize the format patterns of the specified writer
#writer1.pattern={timestamp:yyyy-MM-dd'T'HH:mm:ss.SSSXXX} {level:5} [{thread:id}] {class:compressed}#{method}(L{linenumber}@{filename}) - {message}
### Multiple writers are supported, each with its own configurations
writer2=standard
#writer2.level=trace
### Default json pattern does not include thread and caller details, and uses minified one-line format for the JSON string
#writer2.pattern={json}
### This would force the JSON to include the thread/caller details, and pretty print
writer2.pattern={json:caller-thread,caller-detail,pretty}
### Optional buffer capacity for dispatch of log entry tasks
buffer.front=256
### Optional buffer capacity for data bytes queued for standard out stream
buffer.back=262144
```

### Configuration Refresh

`LogServiceManager.INSTANCE.refreshAll()` will reload the configuration file and apply the latest file properties during
runtime. `LogServiceManager.INSTANCE.refreshAll(Properties)` will apply the passed-in properties as the replacement of
the current properties, and the configuration file will be ignored.

## Performance

It's not how fast you fill up the target log file or repository, it's how fast you relieve the application from logging
duty back to its own business.

For most log destinations (e.g. system console or log files), it is a requirement that logs arrive at the final
destination in chronological order. That means the entire logging process needs to happen sequentially for all the log
entries/events. In other words, the logging process is conceptually a single-thread activity, which has a limited
throughput. Regardless the logging process is synchronous or asynchronous to the application's main business workflow,
if the application's logging frequency is higher than the logging throughput limit, then over time, the main workflow
will be blocked and bound by the logging throughput.

For the log processing throughput alone, synchronous process often outperform its asynchronous counterpart because
synchronous process does not incur the additional cost of resources to facilitate asynchronous communications. However,
for the main application process that issues the logs, the benefit of asynchronous logging is that, when the logging
task buffer is not full between the application process and the asynchronous log processing, the logging throughput has
little impact on the main workflow. The application would just "fire and forget" the logging events, and continue its
main business workflow without further blocking.

Some logging information has to be gathered by the main application thread, synchronously to the business domain
workflow. For example, caller thread and detail information such as method name, line number, or file name are
performance-wise expensive to retrieve, yet unavailable for a different/asynchronous thread to look up. The elf4j-engine
takes measures to minimize the synchronous portion of the logging work before handing off the rest to an asynchronous
process. Nevertheless, it helps if the client application can do without performance-sensitive log information in the
first place; e.g. the default log pattern configuration does not include caller detail and thread information.

Once required information is gathered, the rest of the logging process (data processing and output) is asynchronous. As
long as the work queue hosting the asynchronous logging tasks (a.k.a. the asynchronous buffer) is not full, asynchronous
logging has little performance impact to the main business workflow. However, when the buffer is full (a.k.a. buffer
overload), asynchronous logging may delay the main workflow more severely than its synchronous counterpart because not
only it has to block while awaiting available buffer capacity but also the extra cost of facilitating asynchronous
communication will now add to that of the main workflow. By contrast, synchronous logging without buffer will always
delay the main workflow with every transaction regardless, albeit having no additional cost for asynchrony.

To take the desired advantage of asynchronous logging, it requires that the buffer is not overloaded. Buffer can also
help a conceptually synchronous pipeline by providing some "batch effect", e.g. flushing data to an output stream in
larger "batches" often outperforms frequent and smaller-sized flushes. Since in reality the buffer capacity is always
limited, it is important to set up the capacity to maximize the log process throughput and minimize buffer overloads.

The elf4j-engine has two buffers. A front buffer that, on the one end, takes in log entries/events from the main
application process and, on the other end, hands over the logging tasks to a single log processing thread. The single
thread ensures chronological order among log events, although, the processing of a single log event can be
multithreaded. For example, in case of multiple writers, they can process the same log event in parallel; however, they
need to coordinate and await the completion of all writer threads for that log event before converging back to the
single processing thread and moving on to process the next. There is also a back buffer that, on the one end, takes in
the data bytes from the log processing thread and, on the other end, flushes to the target standard out stream. The back
buffer provides the batch effect for the stream data flushing.

The default front buffer capacity is 262,144 log entries/events (hydrated in-memory objects), and the default back
buffer capacity is 256 log events (in bytes). If that does not fit your needs, one way to adjust the capacities would be
to first set the front buffer to the capacity your host environment can afford for logging; this can be the
larger/dominant capacity between the two; then start to test and adjust the back buffer capacity for best possible
overall throughput of the system. It usually does not take a large back buffer to batch the bytes flushed to the out
stream. It is also possible to set both buffer capacities to zero (0); this would simulate a synchronous logging, whose
throughput may be a useful reference for comparison. To some extent, "performance is a choice".

Note that more down-line flushes may happen than what the back buffer is configured, depending on the actual channel and
destination e.g. the stdout console stream may flush on every line of text, and stderr may flush every character.

The standard stream destinations are often redirected/replaced by the host environment or user, in which case further
manoeuvres may help channel performance. For example, if the target repository is a log file on disk, then

```shell
java MyApplication | cat >logFile
```

may outperform

```shell
java MyApplication >logFile
```

due to the buffering effect of piping and `cat`.

Such external setups fall into the category of increasing channel bandwidth, and are considered outside the scope of
application-level logging.
