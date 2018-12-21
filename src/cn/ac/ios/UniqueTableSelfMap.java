package cn.ac.ios;


public class UniqueTableSelfMap implements UniqueTable{
	int[] tripleToId;
	Node[] nodes;
	int size;
	int currentId;
	int realNodeSize;
	int BUCKET = 20;
	
	
	static final int PAIR(int a, int b) {
        return ((a + b) * (a + b + 1) / 2 + a);
    }
    static final int TRIPLE(int a, int b, int c) {
        return (PAIR(c, PAIR(a, b)));
    }
    final int NODEHASH(int lvl, int l, int h) {
        return Math.abs(TRIPLE(lvl, l, h) % (size * BUCKET));
    }
    
    public UniqueTableSelfMap(int size)
	{
		this.size = size;
		nodes = new Node[size];
		tripleToId = new int[size * BUCKET];
		for(int i=0;i<size * BUCKET;i++)
			tripleToId[i] = -1;
		currentId = 0;
	}
    
	@Override
	public boolean contains(int var, int left, int right) {
		int index = NODEHASH(var, left, right);
		int id = tripleToId[index];
		while(id != -1)
		{
			Node node = nodes[id];
			if(var == node.getVar() && left == node.getLeft() && right == node.getRight())
				return true;
			index = (index+1) % (size * BUCKET);
			id = tripleToId[index];
		}
		return false;
	}

	@Override
	public int getId(int var, int left, int right) {
		int index = NODEHASH(var, left, right);
		int id = tripleToId[index];
		while(id != -1)
		{
			Node node = nodes[id];
			if(var == node.getVar() && left == node.getLeft() && right == node.getRight())
				return id;
			index = (index+1) % (size * BUCKET);
			id = tripleToId[index];
		}
		return -1;
	}

	@Override
	public Node getNodeById(int id) {
		return nodes[id];
	}

	@Override
	public Node insertNode(int var, int left, int right) {
		realNodeSize++;
		
		if(currentId >= size)
			this.enLarge(2 * size);
		
		int index = NODEHASH(var, left, right);
		while(tripleToId[index] != -1)
		{
			index = (index + 1) % (size * BUCKET);
		}
		Node node = new Node(var, left, right, currentId);
		tripleToId[index] = currentId;
		nodes[currentId]=node;
		currentId++;
		
		return node;		
	}

	private void enLarge(int newSize)
	{
		Node[] newNodes = new Node[newSize];
		for(int i=0;i<this.size;i++)
		{
			newNodes[i] = nodes[i];
		}
		nodes = newNodes;
		int oldSize = size;
		this.size = newSize;
		tripleToId = new int[newSize * BUCKET];
		for(int i=0;i<newSize * BUCKET;i++)
		{
			tripleToId[i] = -1;
		}
		for(int i=0;i<oldSize;i++)
		{
			Node node = nodes[i];
			int index = NODEHASH(node.getVar(), node.getLeft(), node.getRight());
			while(tripleToId[index] != -1)
			{
				index = (index + 1) % (size * BUCKET);
			}
			tripleToId[index] = node.getId();
		}
		
	}
	@Override
	public int getRealNodeNum() {
		return realNodeSize;
	}

}
