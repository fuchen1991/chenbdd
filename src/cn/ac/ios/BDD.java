/*
 * ChenBDD, a BDD library.
 * Copyright (c) 2018. Chen Fu, fchen@ios.ac.cn
 * 
 * Licensed under the terms of the GNU LGPL.
 * 
 */
package cn.ac.ios;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BDD {
	private UniqueTable unique;
	private CacheTable cache;
	private int zeroId = -1;
	private int oneId = -1;
	private int varSize;
	private Node one,zero;
	private int varStartIndex;
	
	public BDD(int nodeSize, int varSize, int start)
	{
		//unique = new UniqueTableArray(nodeSize, varSize+1);
		unique = new UniqueTableMap(nodeSize);
		cache = new CacheTable();
		zero = createZero();
		one = createOne();
		this.varSize = varSize;
		varStartIndex = start;
	}
	
	private Node createZero()
	{
		Node node = unique.insertNode(0, 0, 0);
		node.setZero(true);
		zeroId = node.getId();
		return node;
	}
	
	private Node createOne()
	{
		Node node = unique.insertNode(0, 0, 0);
		node.setOne(true);
		oneId = node.getId();
		return node;
	}
	
	public Node one()
	{
		return one;
	}
	
	public Node zero()
	{
		return zero;
	}
	
	public Node var(int var)
	{
		Node node = unique.insertNode(var, zeroId, oneId);
		return node;
	}

	public Node apply(OperationType op, Node node1, Node node2)
	{
		String key = "";
		if(op == OperationType.AND)
			key = "and_";
		if(op == OperationType.OR)
			key = "or_";
		if(op == OperationType.XOR)
			key = "xor_";
		if(op == OperationType.NAND)
			key = "nand_";
		key = key + node1.getId() + "_" + node2.getId();

		if(cache.contains(key))
			return unique.getNodeById(cache.get(key));
		
		if(op == OperationType.AND)
		{
			if(node1.isOne())
				return node2;
			if(node2.isOne())
				return node1;
			if(node1.isZero())
				return node1;
			if(node2.isZero())
				return node2;
			if(node1.equals(node2))
				return node1;
		}
		if(op == OperationType.OR)
		{
			if(node1.isOne())
				return node1;
			if(node2.isOne())
				return node2;
			if(node1.isZero())
				return node2;
			if(node2.isZero())
				return node1;
			if(node1.equals(node2))
				return node1;
		}
		if(op == OperationType.XOR)
		{
			if(node1.isZero())
				return node2;
			if(node2.isZero())
				return node1;
			if(node1.equals(node2))
				return zero;
		}
		if(op == OperationType.NAND)
		{
			if(node1.isOne() && node2.isOne())
				return zero;
			if(node1.isZero() || node2.isZero())
				return one;
		}

		Node w0,w1;
		int xi, xj;
		if(node1.isOne() || node1.isZero())
			xi = varSize + 1;
		else
			xi = node1.getVar();

		if(node2.isOne() || node2.isZero())
			xj = varSize + 1;
		else
			xj = node2.getVar();
		int xk = xi;
		if(xi < xj)
		{
			
			w0 = apply(op,unique.getNodeById(node1.getLeft()), node2);
			w1 = apply(op,unique.getNodeById(node1.getRight()), node2);
		}
		else if(xi == xj)
		{
			w0 = apply(op,unique.getNodeById(node1.getLeft()), unique.getNodeById(node2.getLeft()));
			w1 = apply(op,unique.getNodeById(node1.getRight()), unique.getNodeById(node2.getRight()));
		}
		else
		{
			xk = xj;
			w0 = apply(op,node1, unique.getNodeById(node2.getLeft()));
			w1 = apply(op,node1, unique.getNodeById(node2.getRight()));
		}
		
		if(w0.equals(w1))
			return w0;
		if(unique.contains(xk, w0.getId(), w1.getId()))
			return unique.getNodeById(unique.getId(xk, w0.getId(), w1.getId()));
		
		Node node = unique.insertNode(xk, w0.getId(), w1.getId());
		cache.put(key, node.getId());
		return node;
	}
	
	private Node copy(Node node)
	{
		if(node.isOne() || node.isZero())
			return node;
		
		int leftId = node.getLeft();
		int rightId = node.getRight();
		Node left = unique.getNodeById(leftId);
		Node right = unique.getNodeById(rightId);
		Node cl = copy(left);
		Node cr = copy(right);
		Node res = unique.insertNode(node.getVar(), cl.getId(), cr.getId());
		
		return res;
	}
	
	public Node not(Node node)
	{
		String key = "not_" + node.getId();
		if(cache.contains(key))
			return unique.getNodeById(cache.get(key));
		
		Node copyNode = copy(node);
		Stack<Node> stack = new Stack<Node>();
		stack.push(copyNode);
		while(!stack.isEmpty())
		{
			Node n = stack.pop();
			Node left = unique.getNodeById(n.getLeft());
			Node right = unique.getNodeById(n.getRight());
			if(left.isOne())
			{
				n.setLeft(zeroId);
			}
			else if(left.isZero())
			{
				n.setLeft(oneId);
			}
			else
			{
				stack.push(left);
			}
			
			if(right.isOne())
			{
				n.setRight(zeroId);
			}
			else if(right.isZero())
			{
				n.setRight(oneId);
			}
			else
			{
				stack.push(right);
			}
			
		}
		cache.put(key, copyNode.getId());
		return copyNode;
	}
	
	public Node restrict(Node node, int var, int value)
	{
		String key = "restrict_" + node.getId() + "_" + var + "_" + value;
		if(cache.contains(key))
			return unique.getNodeById(cache.get(key));
		
		if(node.getVar() == var)
		{
			if(value == 1)
				return unique.getNodeById(node.getRight());
			else
				return unique.getNodeById(node.getLeft());
		}
		if(node.isOne() || node.isZero() || node.getVar()>var)
			return node;
			
		Node copyNode = copy(node);
		Stack<Node> stack = new Stack<Node>();
		stack.push(copyNode);
		while(!stack.isEmpty())
		{
			Node n = stack.pop();
			Node left = unique.getNodeById(n.getLeft());
			Node right = unique.getNodeById(n.getRight());
			if(left.isOne() || left.isZero() || left.getVar()>var)
			{
				//do nothing
			}
			else if(left.getVar() == var)
			{
				if(value == 1)
					n.setLeft(left.getRight());
				else
					n.setLeft(left.getLeft());
			}
			else
			{
				stack.push(left);
			}
			
			if(right.isOne() || right.isZero() || right.getVar()>var)
			{
				//do nothing
			}
			else if(right.getVar() == var)
			{
				if(value == 1)
					n.setRight(left.getRight());
				else
					n.setRight(left.getLeft());
			}
			else
			{
				stack.push(right);
			}
		}
		cache.put(key, copyNode.getId());
		return copyNode;
	}
	
	public Node compose(Node f, Node g, int var)
	{
		String key = "compose_" + f.getId() + "_" + g.getId() + "_" + var;
		if(cache.contains(key))
			return unique.getNodeById(cache.get(key));
		
		Node node = apply(OperationType.OR, apply(OperationType.AND, g, restrict(f, var, 1)), apply(OperationType.AND, not(g), restrict(f, var, 0)));
		cache.put(key, node.getId());
		return node;
	}
	
	public Node exist(Node f, List<Integer> vars)
	{
		if(vars == null || vars.size() == 0)
			return f;
		
		StringBuilder builder = new StringBuilder();
		builder.append("exist_");
		builder.append(f.getId());
		for(int var : vars)
		{
			builder.append("_");
			builder.append(var);
		}
		String key = builder.toString();
		if(cache.contains(key))
			return unique.getNodeById(cache.get(key));
		
		int var = vars.get(0);
		vars.remove(0);
		Node node = apply(OperationType.OR,restrict(f, var, 1),restrict(f, var, 0));
		Node res = exist(node, vars);
		cache.put(key, res.getId());
		return res;
	}
	
	public Node forall(Node f, List<Integer> vars)
	{
		if(vars == null || vars.size() == 0)
			return f;
		
		StringBuilder builder = new StringBuilder();
		builder.append("forall_");
		builder.append(f.getId());
		for(int var : vars)
		{
			builder.append("_");
			builder.append(var);
		}
		String key = builder.toString();
		if(cache.contains(key))
			return unique.getNodeById(cache.get(key));
		
		
		int var = vars.get(0);
		vars.remove(0);
		Node node = apply(OperationType.AND,restrict(f, var, 1),restrict(f, var, 0));
		Node res = forall(node, vars);
		cache.put(key, res.getId());
		return res;
	}
	
	public Node relprod(Node f, Node g, List<Integer> vars)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("relprod_");
		builder.append(f.getId());
		builder.append("_");
		builder.append(g.getId());
		for(int var : vars)
		{
			builder.append("_");
			builder.append(var);
		}
		String key = builder.toString();
		if(cache.contains(key))
			return unique.getNodeById(cache.get(key));
		
		Node node = exist(apply(OperationType.AND, f,g),vars);
		cache.put(key, node.getId());
		return node;
	}
	
	public boolean equal(Node f, Node g)
	{
		return f.equals(g);
	}
	
	public int eval(Node f, int[] values)
	{
		assert values.length == this.varSize;
		
		Node tmp = f;
		while(!tmp.isOne() && !tmp.isZero())
		{
			if(values[tmp.getVar()-varStartIndex] == 1)
			{
				tmp = unique.getNodeById(tmp.getRight());
			}
			else
			{
				tmp = unique.getNodeById(tmp.getLeft());
			}
		}
		if(tmp.isOne())
			return 1;
		else
			return 0;
	}
	
	public int[] satisfy(Node f)
	{
		int[] res = new int[varSize];
		if(f.isZero())
			return null;
		Node tmp = f;
		while(!tmp.isOne() && !tmp.isZero())
		{
			Node left = unique.getNodeById(tmp.getLeft());
			Node right = unique.getNodeById(tmp.getRight());
			if(left.isOne())
			{
				return res;
			}
			if(right.isOne())
			{
				res[tmp.getVar()-varStartIndex] = 1;
				return res;
			}
			if(!left.isZero())
			{
				tmp = left;
				continue;
			}
			
			tmp = right;
			res[tmp.getVar()-varStartIndex] = 1;
		}

		return res;
	}
	
	public List<int[]> satisfy_all(Node f)
	{
		List<int[]> res = new ArrayList<int[]>();
		help(f, res, new int[varSize], varStartIndex);
		return res;
	}
	
	private void help(Node f, List<int[]> res, int[] current, int varIndex)
	{
		if(f.isZero())
			return;
		List<int[]> temp = new ArrayList<int[]>();
		if(f.isOne())
		{
			temp.add(current);
			for(int i=varIndex;i<=this.varSize-1 + varStartIndex;i++)
			{
				int num = temp.size();
				for(int j=0;j<num;j++)
				{
					int[] r = temp.get(0);
					temp.remove(0);
					temp.add(r);
					int[] rr = r.clone();
					rr[i-1] = 1;
					temp.add(rr);
				}
			}
			res.addAll(temp);
			return;
		}

		Node left = unique.getNodeById(f.getLeft());
		Node right = unique.getNodeById(f.getRight());
		int var = f.getVar();
		if(var > varIndex)
		{
			temp.add(current);
			for(int i=varIndex;i<var;i++)
			{
				int num = temp.size();
				for(int j=0;j<num;j++)
				{
					int[] r = temp.get(0);
					temp.remove(0);
					temp.add(r);
					int[] rr = r.clone();
					rr[i-1] = 1;
					temp.add(rr);
				}
			}
			for(int[] array : temp)
			{
				help(f,res,array,var);
			}
			return;
		}
		
		if(!left.isZero())
		{
			help(left,res,current,var+1);
		}
		if(!right.isZero())
		{
			int[] c = current.clone();
			c[var-1] = 1;
			help(right,res,c,var+1);
		}
	}
	
	public int satCount(Node f)
	{
		return helpCount(f, varStartIndex);
	}
	
	private int helpCount(Node f, int varIndex)
	{
		if(f.isZero())
			return 0;
		if(f.isOne())
		{
			int res = 1;
			for(int i=varIndex;i<=varSize-1 + varStartIndex;i++)
			{
				res = res * 2;
			}
			return res;
		}
		
		Node left = unique.getNodeById(f.getLeft());
		Node right = unique.getNodeById(f.getRight());
		int var = f.getVar();
		if(var > varIndex)
		{
			int tmp =1;
			for(int i=varIndex;i<var;i++)
			{
				tmp *= 2;
			}
			return tmp * helpCount(f, var);
		}
		
		return helpCount(left, var+1) + helpCount(right, var + 1);
	}
}















