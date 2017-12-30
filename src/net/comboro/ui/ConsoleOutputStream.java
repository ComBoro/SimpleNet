package net.comboro.ui;

import java.awt.*;
import java.io.*;

import net.comboro.SServer;

public class ConsoleOutputStream extends OutputStream {

	private PipedOutputStream out = new PipedOutputStream();
	private Reader reader;

	private final Color color;

	public ConsoleOutputStream(Color color) throws IOException {
		this.color = color;
		PipedInputStream in = new PipedInputStream(out);
		reader = new InputStreamReader(in, "UTF-8");
	}

	@Override
	public void flush() throws IOException {
		if (reader.ready()) {
			char[] chars = new char[1024];
			int n = reader.read(chars);

			String txt = new String(chars, 0, n);

			SServer.append(txt, color);
		}
	}

	@Override
	public void write(byte[] bytes, int i, int i1) throws IOException {
		out.write(bytes, i, i1);
	}

	@Override
	public void write(int i) throws IOException {
		out.write(i);
	}
}
