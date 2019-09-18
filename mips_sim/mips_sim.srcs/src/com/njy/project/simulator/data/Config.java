package com.njy.project.simulator.data;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.njy.project.simulator.util.Util;

public class Config
{
	private static Config instance = null;

	private Config()
	{
		initConfigHashMap();
		LoadConfig();
	}

	public static Config getInstance()
	{
		if (instance == null)
			instance = new Config();

		return instance;
	}
	
	private void initConfigHashMap()
	{
		for(int i = 0; i < ConfigParam.configNodeString.length; i++)
		{
			HashMap<String, String> hashMap = new HashMap<>();
			String[] paramString = ConfigParam.configParamStrings[i];
			for(int j =0; j < paramString.length; j++)
			{
				hashMap.put(paramString[j], null);
			}
			configHashMap.put(ConfigParam.configNodeString[i], hashMap);
		}
	}

	private void LoadConfig()
	{
		try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(defaultConfigPath);
             
            NodeList root = document.getChildNodes();
            for (int i = 0; i < root.getLength(); i++) 
            {
                NodeList config = root.item(i).getChildNodes();
                //System.out.println(config.getLength());
                for(int j=0; j < config.getLength(); j++)
                {
                	Node node = config.item(j);
                	//System.out.println(node.getTextContent());
                	if(configHashMap.containsKey(node.getNodeName()))
                	{
                		loadNodeConfig(node);
                	}
                }
                //System.out.println(config.getNodeName());
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (ParserConfigurationException e) {
            System.out.println(e.getMessage());
        } catch (SAXException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
	}
	
	private void loadNodeConfig(Node nodes)
	{
		HashMap<String, String> paramHashMap = configHashMap.get(nodes.getNodeName());
		NodeList config = nodes.getChildNodes();
		for(int i=0; i < config.getLength(); i++)
        {
        	Node node = config.item(i);
        	String nodename = node.getNodeName();
        	if(paramHashMap.containsKey(nodename))
        	{
        		System.out.println("put " + nodename + " value : " + node.getTextContent());
        		paramHashMap.put(nodename, node.getTextContent());
        	}
        } 
		
	}

	public void saveConfig()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			this.document = builder.newDocument();
			Element root = this.document.createElement("config"); 
	        this.document.appendChild(root);
	        
	        
	        int icount = ConfigParam.configNodeString.length;
	        
	        for(int i = 0; i < icount; i++)
	        {
	        	Element iPath = this.document.createElement(ConfigParam.configNodeString[i]);
	        	int jcount = ConfigParam.configParamStrings[i].length;
	        	for(int j = 0; j < jcount; j++)
	        	{
	        		Element jPath = this.document.createElement( ConfigParam.configParamStrings[i][j]); 
	        		jPath.appendChild(this.document.createTextNode(configHashMap.get(ConfigParam.configNodeString[i]).get(ConfigParam.configParamStrings[i][j]))); 
	        		iPath.appendChild(jPath);
	        	}
	        }
	        
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(document);
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            PrintWriter pw = new PrintWriter(new FileOutputStream(defaultConfigPath));
            StreamResult result = new StreamResult(pw);
            transformer.transform(source, result);
            //System.out.println("���XML�ļ��ɹ�!");   
	        
		} 
		catch (ParserConfigurationException e)
		{
			System.out.println(e.getMessage());
		}
		catch (TransformerConfigurationException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (TransformerException e) {
            System.out.println(e.getMessage());
        }
	}
	
	public static void main(String args[])
	{
		Config.getInstance();
		int a = 0;
		a = a++;
	}
	
	public String getConfigParam(String param)
	{
		for(int i = 0; i < ConfigParam.configNodeString.length; i++)
		{
			HashMap<String, String> hashMap = configHashMap.get(ConfigParam.configNodeString[i]);
			String resultString = hashMap.get(param);
			if(resultString != null)
				return resultString;
		}
		
		return null;
	}
	
	public void setConfigParam(String param, String value)
	{
		for(int i = 0; i < ConfigParam.configNodeString.length; i++)
		{
			HashMap<String, String> hashMap = configHashMap.get(ConfigParam.configNodeString[i]);
			if(hashMap.containsKey(param))
				hashMap.put(param, value);
		}
	}
	
	private HashMap<String, HashMap<String, String>> configHashMap = new HashMap<>();
	private Document document;
	
	final private String defaultConfigPath = "./config.xml";
	
	
	
}
