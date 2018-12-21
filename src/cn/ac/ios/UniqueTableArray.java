/*
 * ChenBDD, a BDD library.
 * Copyright (c) 2018. Chen Fu, fchen@ios.ac.cn
 * 
 * Licensed under the terms of the GNU LGPL.
 * 
 */

package cn.ac.ios;

/*
 * Use array as the unique table
 */
public class UniqueTableArray implements UniqueTable{
	int[][][] unique;
	Node[] nodes;
	int nodeSize;
	int varSize;
	int currentId;
	
	public UniqueTableArray(int nodeSize, int varSize)
	{
		this.nodeSize = nodeSize;
		this.varSize = varSize;
		unique = new int[varSize][nodeSize][nodeSize];
		nodes = new Node[nodeSize];
		this.currentId = 1;
	}
	
	private void enLarge(int newNodeSize, int newVarSize)
	{
		assert newNodeSize >= this.nodeSize;
		assert newVarSize >= this.varSize;
		
		int[][][] newUnique = new int[newVarSize][newNodeSize][newNodeSize];
		for(int i=0;i<varSize;i++)
		{
			for(int j=0;j<nodeSize;j++)
			{
				for(int k=0;k<nodeSize;k++)
				{
					newUnique[i][j][k] = unique[i][j][k];
				}
			}
		}
		this.unique = newUnique;
		Node[] newNodes = new Node[newNodeSize];
		for(int i=0;i<nodeSize;i++)
		{
			newNodes[i] = nodes[i];
		}
		nodes = newNodes;
		
		this.nodeSize = newNodeSize;
		this.varSize = newVarSize;
	}

	@Override
	public boolean contains(int var, int left, int right) {

		return unique[var][left][right] != 0;
	}

	@Override
	public int getId(int var, int left, int right) {

		return unique[var][left][right];
	}

	@Override
	public Node getNodeById(int id) {

		return nodes[id];
	}

	@Override
	public Node insertNode(int var, int left, int right) {
//		if(var >= this.varSize-1)
//			this.enLarge(nodeSize, varSize * 2);
		if(currentId >= this.nodeSize-1)
			this.enLarge(nodeSize * 2, varSize);
		
		Node node = new Node(var, left, right, currentId);
		this.unique[var][left][right] = currentId;
		nodes[currentId]=node;
		currentId++;
		return node;
	}

	@Override
	public int getRealNodeNum() {
		// TODO Auto-generated method stub
		return nodeSize;
	}
	
	
	
	
}
