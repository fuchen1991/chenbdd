/*
 * ChenBDD, a BDD library.
 * Copyright (c) 2018. Chen Fu, fchen@ios.ac.cn
 * 
 * Licensed under the terms of the GNU LGPL.
 * 
 */
package cn.ac.ios;

import java.util.HashMap;
import java.util.Map;

public class CacheTable {
	Map<String, Integer> cache;
	
	public CacheTable()
	{
		cache = new HashMap<String, Integer>();
	}
	
	public void put(String str, int id)
	{
		cache.put(str, id);
	}
	
	public boolean contains(String str)
	{
		return cache.containsKey(str);
	}
	
	public int get(String str)
	{
		return cache.get(str);
	}
}
