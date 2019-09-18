package com.njy.project.simulator.ui;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UIController {
	private static UIController instance = null;
	
	private UIController()
	{
		
	}
	
	public static UIController getInstance() {
		if(instance == null)
			instance = new UIController();
		
		return instance;
	}
	
	public void AddUI(UIInterface ui)
	{
		uiNameMap.put(ui.getClass().getName(), ui);
	}
	
	public void RemoveUI(UIInterface ui)
	{
		uiNameMap.remove(ui.getClass().getName());
	}
	
	public void RemoveUI(String name)
	{
		uiNameMap.remove(name);
	}
	
	public boolean ContainsUI(String name)
	{
		return uiNameMap.containsKey(name);
	}
	
	public void Refresh(String uiName)
	{
		uiNameMap.get(uiName).Refresh();
	}
	
	synchronized public void RefreshAllExcept(String uiName)
	{	
		Iterator<Map.Entry<String, UIInterface>> iter = uiNameMap.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry<String, UIInterface> entry = iter.next();
			String key = entry.getKey();
			UIInterface value = entry.getValue();
			if(key.equals(uiName) || value == null)
				continue;	
			value.Refresh();
		}
	}
	
	synchronized public void RefreshAllExcept(UIInterface ui)
	{	
		Iterator<Map.Entry<String, UIInterface>> iter = uiNameMap.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry<String, UIInterface> entry = iter.next();
			String key = entry.getKey();
			UIInterface value = entry.getValue();
			if(key.equals(ui.getClass().getName()) || value == null)
				continue;	
			value.Refresh();
		}
	}
	
	synchronized public void RefreshAll() 
	{
		Iterator<Map.Entry<String, UIInterface>> iter = uiNameMap.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry<String, UIInterface> entry = iter.next();
			UIInterface value = entry.getValue();
			if(value!=null) 
				value.Refresh();
		}
	}
	private HashMap<String, UIInterface> uiNameMap = new HashMap<>();
}
