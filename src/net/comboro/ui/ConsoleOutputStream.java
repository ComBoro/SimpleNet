/*
 *   ComBoro's Network Server
 *   Copyright (C) 2018  ComBoro
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.comboro.ui;

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
