package com.njy.project.simulator.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.njy.project.simulator.bus.AccessMode;
import com.njy.project.simulator.bus.Bus;
import com.njy.project.simulator.device.MainMemory;
import com.njy.project.simulator.util.Util;

public class BusTest {

	@Test
	public void test() {
		Bus bus = new Bus(new MainMemory(0, Util.oneM));
		int data = 0x87654321;
		int address = 0x100;

		bus.write(address, data);

		// test word
		data = bus.read(address);
		assertEquals(0x87654321, data);

		// test halfword
		data = bus.read(address, AccessMode.HALFWORD, false);
		assertEquals(0x8765, data);
		data = bus.read(address, AccessMode.HALFWORD, false);
		assertEquals(0xffff8765, data);

		// test byte
		data = bus.read(address, AccessMode.BYTE, false);
		assertEquals(0x87, data);
		data = bus.read(address, AccessMode.BYTE, true);
		assertEquals(0xffffff87, data);

		// test invalid address
		address = 0xf0000000;
		bus.write(address, data);
		data = bus.read(address);
		assertEquals(0, data);
	}

}
