package com.njy.project.simulator.device;

public interface Device {
	public boolean isValidAddr(int address);
	public boolean isValidAddr(int address, int mode);

	public int read(int address);
	public int read(int address, int mode, boolean signed);

	public void write(int address, int data);
	public void write(int address, int data, int mode);

	/**
	 * 閸掓繂顫愰崠鏍х摠閸屻劌灏崺锟�
	 * 
	 * @param base
	 *            - 閸╁搫娼�
	 * @param size
	 *            - 鐎涙ê鍋嶉崠鍝勩亣鐏忥拷
	 */
	public void initial(int base, int size);

	/**
	 * 闁插秷顔曠拋鎯ь槵
	 */
	public void reset();
	
	public int getindex();
	public int getLastWrite();
}
