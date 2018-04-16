package com.wavefront.labs.sender;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

/**
 * When using this class, you must ensure to call close after each batch of metrics are sent.
 * Do not keep connections open, as they can become stale and production code will fail.
 */

public class Wavefront implements AutoCloseable {

	private static final String DEFAULT_HOST = "localhost";
	private static final Integer DEFAULT_PORT = 2878;

	private OutputStreamWriter wfWriter;

	private long metricsSent = 0;
	private long delayEach = 0;
	private long delayTime = 0;

	public Wavefront() throws IOException {
		this(DEFAULT_HOST, DEFAULT_PORT);
	}

	public Wavefront(File file) throws FileNotFoundException {
		this(file, false);
	}

	public Wavefront(File file, boolean append) throws FileNotFoundException {
		wfWriter = new OutputStreamWriter(new FileOutputStream(file, append));
	}

	public Wavefront(OutputStream outputStream) {
		wfWriter = new OutputStreamWriter(outputStream);
	}

	public Wavefront(String host, Integer port) throws IOException {
		Socket socket = new Socket(host, port);
		wfWriter = new OutputStreamWriter(socket.getOutputStream(), "ASCII");
	}

	public void setDelay(long delayEach, long delayTime) {
		this.delayEach = delayEach;
		this.delayTime = delayTime;
	}

	public void sendLine(String metricLine) throws RuntimeException {
		if (!metricLine.endsWith("\n")) {
			metricLine += "\n";
		}

		try {
			wfWriter.write(metricLine);
			wfWriter.flush();

			metricsSent++;
			if (delayEach > 0 && metricsSent % delayEach == 0) {
				try {
					System.out.println("Wavefront: " + metricsSent + " metrics sent, delaying: " + delayTime + "ms");
					Thread.sleep(delayTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendMetric(com.wavefront.labs.sender.MetricPoint metricPoint) {
		send(metricPoint.getMetric(), metricPoint.getValue(), metricPoint.getSource(), metricPoint.getTimestamp(), metricPoint.getTags());
	}

	public void send(String metricName, double value) {
		send(metricName, value, null);
	}

	public void send(String metricName, double value, String source) {
		send(metricName, value, source, new Date().getTime());
	}

	public void send(String metricName, double value, String source, long timestamp) {
		send(metricName, value, source, timestamp, "");
	}

	public void send(String metricName, double value, String source, long timestamp, Map<String, String> tags) {

		StringBuilder sbTags = new StringBuilder();
		if (tags != null) {
			for (Map.Entry<String, String> tag : tags.entrySet()) {
				sbTags.append(" " + tag.getKey() + "=\"" + tag.getValue() + "\"");
			}
		}

		send(metricName, value, source, timestamp, sbTags.toString());
	}

	public void send(String metricName, double value, String source, long timestamp, String tags) {
		if (source == null || source.equals("")) {
			try {
				source = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				source = "localhost";
			}
		}

		if (timestamp == 0) {
			timestamp = new Date().getTime();
		}

		String metricLine = metricName + " " + value + " " + timestamp + " source=\"" + source + "\" " + tags + "\n";
		sendLine(metricLine);
	}

	public void sendFile(String filename) {
		sendFile(new File(filename));
	}

	public void sendFile(File file) {
		try (Stream<String> stream = Files.lines(file.toPath())) {
			stream.forEach(this::sendLine);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			wfWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
