# Wavefront Sender

This is a simple class to send metrics to a Wavefront Proxy. Also includes a simple MetricPoint class object
to treat and manipulate metrics as objects instead of raw strings.


### Initialization

Create a sender object using any of the following forms
```java
Wavefront wavefront = new Wavefront(); // default is localhost:2878

Wavefront wavefront = new Wavefront("wavefront-proxy", 2878); // sends to wavefront-proxy:2878

Wavefront wavefront = new Wavefront(OutputStream); // sends to specified stream

Wavefront wavefront = new Wavefront(File); // writes metrics to specfied file
```

### Sending metrics

Send metrics using any of the send methods, they are overriden with defaults. Source will default the local
system name or localhost. Timestamp will default to now.
```java
wavefront.send("metric.name", 100); // name of metric.name with a value of 100

wavefront.send("metric.name", 100, "my-system"); // with a source of my-system

wavefront.send("metric.name", 100, "my-system", 1523920579); // with a timestamp in seconds since epoch specified

wavefront.send("metric.name", 100, "my-system", 0); // timestamp of default will be reset to now


String tags = "\"my.tag\"=\"myvalue\" \"anotherTag\"=\"some-value\"";
wavefront.send("metric.name", 100, "my-system", 0, tags); // send tags as one long string

Map<String, String> tags = new HashMap();
tags.put("my.tag", "myvalue");
wavefront.send("metric.name", 100, "my-system", 0, tags); // send tags as a map
```
When sending tags as a string, quotes around the key and value are optional but recommended.


### Sending MetricPoint

MetricPoint object can be sent using the `sendMetric` method. This works well with List and lambdas
```java
List<MetricPoint> metrics;
...
metrics.each(wavefront::sendMetric);
```

