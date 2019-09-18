package com.njy.project.simulator.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.njy.project.simulator.bus.AccessMode;
import com.njy.project.simulator.device.AbstractDevice;
import com.njy.project.simulator.device.Device;

public class DeviceTest {
	Device device;

	@Before
	public void setUp() throws Exception {
		device = new AbstractDevice(0xFFFFFFF0, 8) {
		};
	}

	@Test
	public void test() {
		int data = 0;
		int address = 0xFFFFFFF0;
		data = device.read(address);
		assertEquals(0, data);

		data = 0x12345678;
		device.write(address, data);
		data = device.read(address);
		assertEquals(0x12345678, data);
		data = device.read(address, AccessMode.BYTE, false);
		assertEquals(0x12, data);
		data = 0x87654321;
		device.write(address, data);
		data = device.read(address, AccessMode.BYTE, true);
		assertEquals(0xffffff87, data);
		data = device.read(address, AccessMode.HALFWORD, false);
		assertEquals(0x8765, data);
		data = device.read(address, AccessMode.HALFWORD, true);
		assertEquals(0xffff8765, data);
		
		device.initial(0, 0x100000); // 1M
		data = 0xfedcba78;
		address = 0x100;
		device.write(address, data);
		data = device.read(address);
		assertEquals(0xfedcba78, data);
	}

}
