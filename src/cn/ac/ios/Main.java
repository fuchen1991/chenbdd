/*
 * ChenBDD, a BDD library.
 * Copyright (c) 2018. Chen Fu, fchen@ios.ac.cn
 * 
 * Licensed under the terms of the GNU LGPL.
 * 
 */

package cn.ac.ios;

public class Main {

	public static void main(String[] args) {

		for(int i=1;i<=10;i++)
		{
			long start = System.currentTimeMillis();
			System.out.println(i + " queens: " + NQueens(i));
			long end = System.currentTimeMillis();
			System.out.println("Time: " + (end - start) + "ms");
		}
		
	}

	public static int NQueens(int N)
	{
		BDD bdd = new BDD(N*N*N*N, N*N, 0);
		Node[][] X = new Node[N][N];
		
		Node queen = bdd.one();
		for(int i=0;i<N;i++)
			for(int j=0;j<N;j++)
				X[i][j] = bdd.var(i*N+j);
		
		for(int i=0;i<N;i++)
		{
			Node e = bdd.zero();
			for(int j=0;j<N;j++)
			{
				e = bdd.apply(OperationType.OR, e, X[i][j]);
			}
			queen = bdd.apply(OperationType.AND, e, queen);
		}
		
		
		for(int i=0;i<N;i++)
		{
			for(int j=0;j<N;j++)
			{
				Node a = bdd.one();
				Node b = bdd.one();
				Node c = bdd.one();
				Node d = bdd.one();
				
				for(int l=0;l<N;l++)
				{
					if(l != j)
					{
						Node u = bdd.apply(OperationType.NAND, X[i][j], X[i][l]);
						a = bdd.apply(OperationType.AND, a, u);
					}
				}
				
				for(int l=0;l<N;l++)
				{
					if(l != i)
					{
						Node u = bdd.apply(OperationType.NAND, X[l][j], X[i][j]);
						b = bdd.apply(OperationType.AND, b, u);
					}
				}
				
				for (int k = 0; k < N; k++) {
	                int ll = k - i + j;
	                if (ll >= 0 && ll < N) {
	                    if (k != i) {
	                        Node u = bdd.apply(OperationType.NAND, X[k][ll], X[i][j]);
	                        c = bdd.apply(OperationType.AND, c, u);
	                    }
	                }
	            }

	            for (int k = 0; k < N; k++) {
	                int ll = i + j - k;
	                if (ll >= 0 && ll < N) {
	                    if (k != i) {
	                    	Node u = bdd.apply(OperationType.NAND, X[k][ll], X[i][j]);
	                    	d = bdd.apply(OperationType.AND, d, u);
	                    }
	                }
	            }
	            
	            b = bdd.apply(OperationType.AND, a, b);
	            c = bdd.apply(OperationType.AND, b, c);
	            d = bdd.apply(OperationType.AND, c, d);
	            queen = bdd.apply(OperationType.AND, d, queen);
			}
		}
		
		int res = bdd.satCount(queen);
		return res;
	}
}



