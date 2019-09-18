package com.njy.project.simulator.test;

public class Haha {
	public static int [] a={
		1,1,1,1,0,0,0,1,1,1,1,1,0,0,1
	};
	
	public static void main(String args[])
	{
		int max = 0;
		int count = 0;
		for(int i =0; i< a.length; i++)
		{
			if(a[i] > 0) 
			{
				count ++;
				if(count > max) max = count;
			}
			else
			{
				count = 0;
			}
			
		}
		
		System.out.println(max);
	}
}
