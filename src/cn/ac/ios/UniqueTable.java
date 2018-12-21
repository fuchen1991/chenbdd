/*
 * ChenBDD, a BDD library.
 * Copyright (c) 2018. Chen Fu, fchen@ios.ac.cn
 * 
 * Licensed under the terms of the GNU LGPL.
 * 
 */

package cn.ac.ios;

public interface UniqueTable {
	public boolean contains(int var, int left, int right);
	public int getId(int var, int left, int right);
	public Node getNodeById(int id);
	public Node insertNode(int var, int left, int right);
	public int getRealNodeNum();
}
