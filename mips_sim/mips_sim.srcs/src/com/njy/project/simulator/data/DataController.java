package com.njy.project.simulator.data;

import java.io.IOException;

import com.njy.project.simulator.bus.Bus;
import com.njy.project.simulator.cp0.CP0;
import com.njy.project.simulator.cpu.MIPSCPU;
import com.njy.project.simulator.device.DeviceRom;
import com.njy.project.simulator.device.MainMemory;
import com.njy.project.simulator.device.spi.SPICtrl;
import com.njy.project.simulator.device.spi.SD.SDCard;
import com.njy.project.simulator.device.spi.SD.SlaveSDCard;
import com.njy.project.simulator.file.FileFactory;
import com.njy.project.simulator.file.FileInterface;
import com.njy.project.simulator.file.bin.BinFileReader;
import com.njy.project.simulator.file.coe.COEFileReader;
import com.njy.project.simulator.file.coe.COEReadException;
import com.njy.project.simulator.ui.main.MainConsole;
import com.njy.project.simulator.util.Util;

public class DataController
{
	public static final String version = "0.1 develop";
	private MIPSCPU mips_CPU;
	private Bus bus;
	private CP0 cp0;
	private DeviceRom deviceRom;
	private MainMemory mainMemory;
	private Config config;
	private int pcAddr = 0xff000000;
	private int ramLoadAddr  = 0;
	private int ramSize = Util.oneM * 64;
	private final String defaultRomData = "./rom.bin";
	private final String defaultRamData = "./ram.bin";
	private String sdFile;
	private MainConsole mainConsole;
	private String ramFile;
	private String romFile;
	private boolean isRamFileLoaded = false;
	private boolean isRomFileLoaded = false;
	private boolean showCPUSpeed = false;
	private boolean stepSynPCAddr = true;
	private SPICtrl spiCtrl;
	private static DataController instance = null;
	public static DataController getInstance()
	{
		if(instance == null)
			instance = new DataController();
		
		return instance;
	}
	
	private DataController()
	{
		loadConfig();	
	}
	
	
	/////////////////////////////load config////////////////////////////////////
	private void loadConfig()
	{
		mainConsole = MainConsole.getInstance();
		mainConsole.InsertMessage("Loading Config...");
		config = Config.getInstance();
		String value;
		
		if((value = config.getConfigParam("ramSize")) != null)
		{
			if(ConfigParam.ramSizeMap.containsKey(value))
			{
				ramSize = ConfigParam.ramSizeMap.get(value);
			}
		}
		
		mainConsole.InsertMessage("Ram size = " + ramSize + "Bytes");
		
		///////////init/////////////////
		initCPUAndBus();

		if((value = config.getConfigParam("pcAddr")) != null)
		{
			try {
				pcAddr = Util.parseStringToHex(value);
				if(!bus.isValidPCAddress(pcAddr))
				{
					pcAddr = 0;
				}
				mips_CPU.setPcInit(pcAddr);
				mips_CPU.setPc(pcAddr);
				mainConsole.InsertMessage("PC Address = " + String.format("%08X", pcAddr));
			} catch (NumberFormatException e) {
				// TODO: handle exception
				mainConsole.InsertMessage("Error! Invalid PC Address");
			}
		}
		
		if((value = config.getConfigParam("ramLoadAddr")) != null)
		{
			try {
				ramLoadAddr = Util.parseStringToHex(value);
				mainMemory.setLoadAddr(ramLoadAddr);
				mainConsole.InsertMessage("RAM Load address = " + String.format("%08X", ramLoadAddr));
			} catch (NumberFormatException e) {
				// TODO: handle exception
				mainConsole.InsertMessage("Error! Invalid RAM Load address");
			}
		}
		

		if((value = config.getConfigParam("loadRomOnStart")) != null)
		{
			String newval  = config.getConfigParam("defaultRomFile");
			if(!value.equals("0") && newval != null)
			{
				LoadRom(newval);
			}
		}
		
		if((value = config.getConfigParam("loadRamOnStart")) != null)
		{
			String newval  = config.getConfigParam("defaultRamFile");
			if(!value.equals("0") && newval != null)
			{
				LoadRam(newval);
			}
		}
		
		if((value = config.getConfigParam("sdFile")) != null)
		{
			sdFile = value;
			mainConsole.InsertMessage("Load SD Card at " + value);
			loadSD();
		}
		
		if((value = config.getConfigParam("showCPUSpeed")) != null)
		{
			if(!value.equals("0"))
			{
				mainConsole.InsertMessage("Show CPU Speed");
				showCPUSpeed = true;
			}
		}
		
	}
	
	private void initCPUAndBus()
	{
		mainMemory = new MainMemory(0, ramSize);
		deviceRom = new DeviceRom();
		bus = new Bus(mainMemory);
		bus.mountRom(deviceRom);
		cp0 = new CP0(bus);
		mips_CPU = new MIPSCPU(bus, cp0);
		spiCtrl = new SPICtrl(cp0, bus);
	}
	
	private void loadSD()
	{
		////load sd
		SDCard sdCard = new SDCard();
		SlaveSDCard slaveSDCard = new SlaveSDCard();
		sdCard.setFile(sdFile);
		slaveSDCard.setSdCard(sdCard);
		spiCtrl.addSlave(slaveSDCard,0);
	}
	
	public void saveConfig()
	{
		
	}
	
	public int GetRamSize()
	{
		return bus.getMainMemory().GetRamSize();
	}
	
	public void LoadRom(String path)
	{
		try
		{
			if(path ==null)
				path = defaultRomData;
			deviceRom.initialRom(FileFactory.getInstance().getFile(path));
			mainConsole.InsertMessage("Load ROM from " + path + ".");
			romFile = path;
			isRomFileLoaded = true;
		}
		catch (IOException | COEReadException e)
		{
			e.printStackTrace();
			mainConsole.InsertMessage("Error! Cannot load ROM from " + path + "!");
			return;
		}
		
		
	}
	
	public void LoadRam(String path)
	{
		try
		{
			if(path == null)
				path = defaultRamData;			
			mainMemory.initialRam(FileFactory.getInstance().getFile(path));
			mainConsole.InsertMessage("Load RAM from " + path + ".");
			ramFile = path;
			isRamFileLoaded = true;
		}
		catch (IOException | COEReadException e)
		{
			// TODO: handle exception
			e.printStackTrace();
			mainConsole.InsertMessage("Error! Cannot load RAM from " + path + "!");
			return;
		}
		
		
		//isMemFileLoaded = true;
	}
	
	public int getStatus()
	{
		return mips_CPU.getStatus();
	}
	
	public void Start()
	{
		mainConsole.InsertMessage("Simulator Start");
		if(isRamFileLoaded || isRomFileLoaded)
		{
			//mips_CPU.setPc(deviceRom.GetBase());
			mips_CPU.Start();
			cp0.StartTimer();
		}
			
	}
	
	public void Reload()
	{
		mainConsole.InsertMessage("Reload Data");
		bus.reset();
		if(isRamFileLoaded)
			LoadRam(ramFile);
		if(isRomFileLoaded)
			LoadRom(romFile);
	}
	
	public void Clear()
	{
		mainConsole.InsertMessage("Clear Data");
		isRamFileLoaded = false;
		isRomFileLoaded = false;
		bus.reset();
	}
	
	public void Pause()
	{
		mainConsole.InsertMessage("Simulator Paused");
		mips_CPU.Pause();
		
	}
	
	public void Stop()
	{
		mainConsole.InsertMessage("Simulator Stopped");
		mips_CPU.Stop();
	}
	
	public void Resume()
	{
		mainConsole.InsertMessage("Simulator Resume");
		mips_CPU.Resume();
	}
	
	public void Step()
	{
		mips_CPU.Step();
	}
	
	public void Debug()
	{
		mainConsole.InsertMessage("Simulator Debug");
		if(isRamFileLoaded || isRomFileLoaded)
		{
			//mips_CPU.setPc(deviceRom.GetBase());
			mips_CPU.Debug();
			cp0.StartTimer();
		}
	}


	public MIPSCPU getMips_CPU() {
		return mips_CPU;
	}

	public Bus getBus() {
		return bus;
	}

	public CP0 getCp0() {
		return cp0;
	}

	public boolean isShowCPUSpeed()
	{
		return showCPUSpeed;
	}

	public boolean isStepSynPCAddr()
	{
		return stepSynPCAddr;
	}

	public void setStepSynPCAddr(boolean stepSynPCAddr)
	{
		this.stepSynPCAddr = stepSynPCAddr;
	}

	public String getRamFile()
	{
		return ramFile;
	}

	public String getRomFile()
	{
		return romFile;
	}

	public boolean isRamFileLoaded()
	{
		return isRamFileLoaded;
	}

	public boolean isRomFileLoaded()
	{
		return isRomFileLoaded;
	}

	public int getPcAddr()
	{
		return pcAddr;
	}

	public void setPcAddr(int pcAddr)
	{
		this.pcAddr = pcAddr;
		mips_CPU.setPcInit(pcAddr);
	}

	public SPICtrl getSpiCtrl()
	{
		return spiCtrl;
	}
	
	public void setAllmask(boolean b){
		cp0.setAllmask(b);
	}
	
	public void setMask(int v){
		cp0.setMask(v);
	}
	
	
}
