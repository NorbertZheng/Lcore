package com.njy.project.simulator.test;

public class pmn
{
	final public static int N = 120;
	static long dp[][] = new long[N + 1][N + 1];
	static{
		for(int i = 1; i <= N; i++){
			dp[i][i] = 1;
			dp[1][i] = 1;
			for(int j = i + 1; j <= N; j++)
				dp[j][i] = 0;
		}
	}
	public static long p(int m, int n){
		if(n < 1 || m < 1) return 0;
		if(dp[m][n]!=0) return dp[m][n];
		int d = n - m;
		dp[m][n] = 0;
		for(int i = 1; i <= m; i++)
			dp[m][n] += p(i, d);
		return dp[m][n];
	}
	
	public static void main(String args[])
	{
		for(int i = 1; i <= N; i++){
			int ii = 0;
			for(int j = 1; j <= i; j++){
				ii += p(j, i);
			}
			
			System.out.println(i + ":" + ii);
		}
	}
	
}
