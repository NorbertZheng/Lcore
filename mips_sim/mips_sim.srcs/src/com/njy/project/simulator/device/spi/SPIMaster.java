package com.njy.project.simulator.device.spi;

public interface SPIMaster
{
	public int transmitData(int data);
	public void SlaveToSPI(int data);
	public int SPIToSlave();
}
