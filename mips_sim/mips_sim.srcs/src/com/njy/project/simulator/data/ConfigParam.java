package com.njy.project.simulator.data;

import java.util.HashMap;

import com.njy.project.simulator.util.Util;

public class ConfigParam {
//	public static HashMap<String, HashSet<String>> configHashMap = new HashMap<>();
	//define config params
	public static String[][] configParamStrings =
	{
		{                
			"defaultRomFile",
			"defaultRamFile",
			"loadRomOnStart",
			"loadRamOnStart",
		},
		{
			"ramSize",
			"sdFile",
			"sdSize",
		},
		{
			"pcAddr",
			"showCPUSpeed",
			"disableDelaySlot",
		},
		{
			"ramLoadAddr",
			"printException"
		}
	};
	
	public static String[] configNodeString = 
	{
		"general",
		"system",
		"simulator",
		"advanced", 
	};
	
	public static HashMap<String, Integer> ramSizeMap = new HashMap<>();
	public static HashMap<String, Long> sdSizeMap = new HashMap<>();
	static{
		ramSizeMap.put("1M"  ,Util.oneM);
		ramSizeMap.put("2M"  ,Util.oneM << 1);
		ramSizeMap.put("4M"  ,Util.oneM << 2);
		ramSizeMap.put("8M"  ,Util.oneM << 3);
		ramSizeMap.put("16M" ,Util.oneM << 4);
		ramSizeMap.put("32M" ,Util.oneM << 5);
		ramSizeMap.put("64M" ,Util.oneM << 6);
		ramSizeMap.put("128M",Util.oneM << 7);
		ramSizeMap.put("256M",Util.oneM << 8);
		ramSizeMap.put("512M",Util.oneM << 9);
		ramSizeMap.put("1G"  ,Util.oneG);
		ramSizeMap.put("2G"  ,Util.oneG << 1);
		
		
		sdSizeMap.put("256M",Util.oneM * 1L << 8);
		sdSizeMap.put("512M",Util.oneM * 1L << 9);
		sdSizeMap.put("1G"  ,Util.oneG * 1L);
		sdSizeMap.put("2G"  ,Util.oneG * 1L << 1);
		sdSizeMap.put("4G"  ,Util.oneG * 1L << 2);
		sdSizeMap.put("8G"  ,Util.oneG * 1L << 3);
		sdSizeMap.put("16G" ,Util.oneG * 1L << 4);
		sdSizeMap.put("32G" ,Util.oneG * 1L << 5);
	}
}
