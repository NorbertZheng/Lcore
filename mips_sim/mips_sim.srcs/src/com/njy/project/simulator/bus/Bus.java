package com.njy.project.simulator.bus;

import com.njy.project.simulator.device.AbstractDevice;
import com.njy.project.simulator.device.Device;
import com.njy.project.simulator.device.DeviceRom;
import com.njy.project.simulator.device.MainMemory;

/**
 * 閹崵鍤庨敍灞藉瘶閹奉兛瀵岀�姗堢礄娑旂喐妲哥拋鎯ь槵娑斿绔撮敍澶婂挤娑擄拷缁鍨拋鎯ь槵閿涘本鐗撮幑顔兼勾閸э拷闁瀚ㄧ拋鎯ь槵閿涘苯鍟�潻娑滎攽閹垮秳缍�
 * 
 * @author dell
 * 
 */
public class Bus {

	/**
	 * Bus must have main memory
	 * 
	 * @param mainMemory
	 *            - initial the main memory
	 */
	public Bus(MainMemory mainMemory) {
		// TODO Auto-generated constructor stub
		this.mainMemory = mainMemory;
		initDevice();
	}
	
	private void initDevice()
	{
		for(int i = 0; i  < 256 ; i++)
		{
			devices[i] = new AbstractDevice(0xffff0000, i , 8);
		}
	}

	public void mountRom(DeviceRom rom) {
		this.rom = rom;
	}
	
	/**
	 * Mount a new device
	 * 
	 * @param device
	 *            - device to be mounted
	 */
	public void mountDevice(Device device) {
			devices[device.getindex()] = device;
	}

	/**
	 * Unmount a device
	 * 
	 * @param device
	 *            - device to be unmounted
	 */
	public void unmountDevice(int index) {
		devices[index] = null;
	}

	/**
	 * It calls read(address, WORD, false). It uses default parameters
	 * 
	 * @param address
	 * @return
	 */
	final public int read(int address) {
		int ret = find(address, AccessMode.WORD).read(address);
		if(address == 0xffff030C )
		{
			System.out.println("keycode:" + ret);
		}
		return ret;
	}

	/**
	 * The practical operation to read.
	 * 
	 * @param address
	 *            - The memory address
	 * @param mode
	 *            - The access mode: byte, halfword, word
	 * @param signed
	 *            - If it isn't a word, it is used to indicate whether should be
	 *            sign extended.
	 * @return
	 */
	final public int read(int address, int mode, boolean signed) {
		int ret = find(address, mode).read(address, mode, signed);
		if(address == 0xffff030C )
		{
			System.out.println("keycode:" + ret);
		}
		return ret;
	}

	/**
	 * A convenient way to write. It calls another write operation
	 * 
	 * @param address
	 * @param data
	 */
	final public void write(int address, int data) {
		write(address, data, AccessMode.WORD);
	}

	/**
	 * The practical operation to write.
	 * 
	 * @param address
	 * @param data
	 * @param mode
	 */
	final public void write(int address, int data, int mode) {
		find(address, mode).write(address, data, mode);
	}

	/**
	 * According to the address and access mode, find a suitable device,
	 * including main memory.
	 * 
	 * @param address
	 * @param mode
	 * @return
	 */
	private Device find(int address, int mode) 
	{
		if((address >>> 24) == 0xff)
		{
			if((address >>> 16) == 0xffff) 
				return devices[(address >> 8) & 0xff];	
			return rom;
		}
		
		return mainMemory;
	}

	public void reset() 
	{
		for (Device device : devices) {
			device.reset();
		}
		mainMemory.reset();
		rom.reset();
	}

	final public MainMemory getMainMemory() {
		return mainMemory;
	}
	
	final public Device getDevice(int index)
	{
		return devices[index];
	}
	
	final public DeviceRom getRom()
	{
		return rom;
	}
	
	final public boolean isValidAddress(int addr)
	{
		if((addr >>> 24) == 0xff) return true;
		
		return mainMemory.isValidAddr(addr);
	}
	
	final public boolean isValidPCAddress(int addr)
	{
		if((addr >>> 16) == 0xffff) return false;
		if((addr >>> 24) == 0xff) return true;
		
		return mainMemory.isValidAddr(addr);
	}

	private MainMemory mainMemory;
	private DeviceRom rom;
	private Device[] devices = new Device[256];
}
