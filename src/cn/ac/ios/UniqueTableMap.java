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

/*
 * Use HashMap as the unique table
 */
public class UniqueTableMap implements UniqueTable{
	Map<Triple, Integer> unique;
	Node[] nodes;
	int size;
	int currentId;
	
	public UniqueTableMap(int size)
	{
		this.size = size;
		nodes = new Node[size];
		unique = new HashMap<Triple, Integer>();
		currentId = 0;
	}
	
	private void enLarge(int newSize)
	{
		//System.out.println(currentId);
		assert newSize > this.size;
		
		Node[] newNodes = new Node[newSize];
		for(int i=0;i<this.size;i++)
		{
			newNodes[i] = nodes[i];
		}
		nodes = newNodes;
		this.size = newSize;
	}
	
	public boolean contains(int var, int left, int right)
	{
		Triple triple = new Triple(var, left, right);
		return this.unique.containsKey(triple);
	}
	
	public int getId(int var, int left, int right)
	{
		Triple triple = new Triple(var, left, right);
		return this.unique.get(triple);
	}
	
	public Node getNodeById(int id)
	{
		return nodes[id];
	}
	
	public Node insertNode(int var, int left, int right)
	{
		if(currentId >= size)
			this.enLarge(2 * size);
		
		Triple triple = new Triple(var, left, right);
		Node node = new Node(var, left, right, currentId);
		unique.put(triple, currentId);
		nodes[currentId]=node;
		currentId++;
		return node;
	}

	@Override
	public int getRealNodeNum() {
		return unique.size();
	}
}

class Triple {
	int var;
	int left;
	int right;
	
	public Triple(int var, int left, int right)
	{
		this.var = var;
		this.left = left;
		this.right = right;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + left;
		result = prime * result + right;
		result = prime * result + var;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Triple other = (Triple) obj;
		if (left != other.left)
			return false;
		if (right != other.right)
			return false;
		if (var != other.var)
			return false;
		return true;
	}

	
	
	
}

