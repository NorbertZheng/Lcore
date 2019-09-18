package com.njy.project.simulator.util;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileChooserUtil {
	private JFileChooser fileChooser = new JFileChooser();
	private String currentDirPath = ".";

	private Component parent = null;

	public FileChooserUtil() {
		// TODO Auto-generated constructor stub
	}

	public FileChooserUtil(String currentDirPath) {
		this(currentDirPath, null);
	}

	public FileChooserUtil(Component parent) {
		this(".", parent);
	}

	public FileChooserUtil(String currentDirPath, Component parent) {
		this.currentDirPath = currentDirPath;
		this.parent = parent;
	}
	
	FileFilter fileFilter = new FileFilter() {
		
		@Override
		public String getDescription() {
			return "二进制数据文件";
		}
		
		@Override
		public boolean accept(File f) {
			String nameString = f.getName();
			 if (nameString.endsWith(".bin") || nameString.endsWith(".coe")|| f.isDirectory()) {
	                return true;
	         }
			return false;
		}
	};

	public File getFile() {
		fileChooser.setFileFilter(fileFilter);
		fileChooser.setAcceptAllFileFilterUsed(false);  
		fileChooser.setCurrentDirectory(new File(currentDirPath));
		int result = fileChooser.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			currentDirPath = file.getParent();
			return file;
		}
		return null;
	}
	
	public File getDir() {
		fileChooser.setAcceptAllFileFilterUsed(false);  
		fileChooser.setCurrentDirectory(new File(currentDirPath));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fileChooser.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			currentDirPath = file.getParent();
			return file;
		}
		return null;
	}

	public String saveFile() {
		fileChooser.setCurrentDirectory(new File(currentDirPath));
		int result = fileChooser.showSaveDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			currentDirPath = file.getParent();
			String name = file.getPath();
			return name;
		}
		return null;
	}

	public String getCurrentDirPath() {
		return currentDirPath;
	}

	public void setCurrentDirPath(String currentDirPath) {
		this.currentDirPath = currentDirPath;
	}

	public void setSelectFile() {
		setSelectFile("");
	}

	public void setSelectFile(String file) {
		fileChooser.setSelectedFile(new File(file));
	}
}
