package com.wavefront.labs.sender;

import java.util.HashMap;
import java.util.Map;

public class MetricPoint {

	private String metric;
	private double value;
	private long timestamp;
	private String source;
	private Map<String, String> tags;

	public MetricPoint() {
	}

	public MetricPoint(String metric, double value, long timestamp, String source) {
		this.metric = metric;
		this.value = value;
		this.timestamp = timestamp;
		this.source = source;
	}

	public MetricPoint(String metric, double value, long timestamp, String source, Map<String, String> tags) {
		this(metric, value, timestamp, source);
		this.tags = tags;
	}

	public String toString() {
		StringBuilder metricLine = new StringBuilder();
		metricLine.append(metric)
				.append(" ")
				.append(value)
				.append(" ")
				.append(timestamp)
				.append(" source=\"")
				.append(source)
				.append("\"");
		if (tags != null) {
			for (Map.Entry<String, String> tag : tags.entrySet()) {
				metricLine.append(" \"")
						.append(tag.getKey())
						.append("\"=\"")
						.append(tag.getValue())
						.append("\"");
			}
		}

		return metricLine.toString();
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public void addTag(String name, String value) {
		if (tags == null) {
			tags = new HashMap();
		}

		tags.put(name, value);
	}
}
