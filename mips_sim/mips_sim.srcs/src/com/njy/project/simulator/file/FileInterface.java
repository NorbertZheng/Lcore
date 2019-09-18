package com.njy.project.simulator.file;

import java.io.IOException;

public interface FileInterface
{
	public String getPath();
	public int readByte() throws IOException;
	public void close() throws IOException;
}
