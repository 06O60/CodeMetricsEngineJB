package weblab;

import java.util.*;

class MaxFlow {
	public static Graph graph1() {
		Node[] nodes = new Node[5];
		ArrayList<Node> nodesAList = new ArrayList<>();
		for(int i = 0; i < 5; ++i) {
			nodes[i] = new Node(i);
			nodesAList.add(nodes[i]);
		}

		nodes[0].addEdge(nodes[2],5);
		nodes[0].addEdge(nodes[3],3);

		nodes[1].addEdge(nodes[4],7);

		nodes[2].addEdge(nodes[4],2);
		nodes[2].addEdge(nodes[1],2);

		nodes[3].addEdge(nodes[1],5);

		return new Graph(nodesAList, nodes[0], nodes[4]);
		// TODO
	}

	public static int maximizeFlow(Graph g) {
		ArrayList<Edge>path;
		int flow = 0;

		while((path = findAugmentingPath(g)) != null) {
			int addedFlow = findBottleneck(path);
			flow+=addedFlow;
			addFlow(path, addedFlow);
		}

		return flow;
	}

	private static void addFlow(ArrayList<Edge> path, int addedFlow) {
		for(Edge e: path){
			e.augmentFlow(addedFlow);
		}
	}

	private static int findBottleneck(ArrayList<Edge> path) {
		int bottleneck = Integer.MAX_VALUE;
		for(Edge e:path) {
			bottleneck = Integer.min(bottleneck, e.getResidual());
		}
		return bottleneck;
	}

	private static ArrayList<Edge> findAugmentingPath(Graph g) {
		Queue<Node> q = new LinkedList<>();
		Map<Node, Edge> prev = new HashMap<>();

		q.add(g.getSource());
		while(!q.isEmpty()){
			Node node = q.remove();
			if(node.equals(g.getSink()))
				break;

			for(Edge e : node.getEdges()) {
				Node edgeEnd = e.getTo();
				if(prev.get(edgeEnd) == null && e.getBackwards().getFlow() > 0) {
					prev.putIfAbsent(edgeEnd, e);
					q.add(edgeEnd);
				}
			}
		}

		return parseToPath(prev, g);
	}

	private static ArrayList<Edge> parseToPath(Map<Node,Edge> prev, Graph g) {
		ArrayList<Edge> path = new ArrayList<>();
		Node n = g.getSink();

		while(!n.equals(g.getSource())) {
			Edge prevEdge = prev.get(n);
			if(prevEdge == null)
				return null;
			path.add(prevEdge);
			n = prevEdge.getFrom();
		}

		return path;
	}

	private static int getFlow(Node source) {
		int flow = 0;
		for(Edge e: source.getEdges()) {
			flow+=e.getFlow();
		}
		return flow;
	}
}

class Graph {

	private List<Node> nodes;

	private Node source;

	private Node sink;

	public Graph(List<Node> nodes, Node source, Node sink) {
		this.nodes = nodes;
		this.source = source;
		this.sink = sink;
	}

	public Node getSink() {
		return sink;
	}

	public Node getSource() {
		return source;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public boolean equals(Object other) {
		if (other instanceof Graph) {
			Graph that = (Graph) other;
			return this.nodes.equals(that.nodes);
		}
		return false;
	}
}

class Node {

	protected int id;
	protected Collection<Edge> edges;

	/**
	 * Create a new node
	 *
	 * @param id: Id for the node.
	 */
	public Node(int id) {
		this.id = id;
		this.edges = new ArrayList<Edge>();
	}

	public void addEdge(Node to, int capacity) {
		Edge e = new Edge(capacity, this, to);
		edges.add(e);
		to.getEdges().add(e.getBackwards());
	}

	public Collection<Edge> getEdges() {
		return edges;
	}

	public int getId() {
		return id;
	}

	public boolean equals(Object other) {
		if (other instanceof Node) {
			Node that = (Node) other;
			if (id == that.getId()) return edges.equals(that.getEdges());
		}
		return false;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getId());
		sb.append(" ");
		sb.append(this.getEdges().size());
		sb.append(":");
		for (Edge e : this.getEdges()) {
			sb.append("(");
			sb.append(e.from.getId());
			sb.append(" --[");
			sb.append(e.capacity);
			sb.append("]-> ");
			sb.append(e.to.getId());
			sb.append(")");
		}
		return sb.toString();
	}
}

class Edge {

	protected int capacity;

	protected int flow;

	protected Node from;

	protected Node to;

	protected Edge backwards;

	private Edge(Edge e) {
		this.flow = e.getCapacity();
		this.capacity = e.getCapacity();
		this.from = e.getTo();
		this.to = e.getFrom();
		this.backwards = e;
	}

	protected Edge(int capacity, Node from, Node to) {
		this.capacity = capacity;
		this.from = from;
		this.to = to;
		this.flow = 0;
		this.backwards = new Edge(this);
	}

	public void augmentFlow(int add) {
		assert (flow + add <= capacity);
		flow += add;
		backwards.setFlow(getResidual());
	}

	public Edge getBackwards() {
		return backwards;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getFlow() {
		return flow;
	}

	public Node getFrom() {
		return from;
	}

	public int getResidual() {
		return capacity - flow;
	}

	public Node getTo() {
		return to;
	}

	private void setFlow(int f) {
		assert (f <= capacity);
		this.flow = f;
	}

	public boolean equals(Object other) {
		if (other instanceof Edge) {
			Edge that = (Edge) other;
			return this.capacity == that.capacity
					       && this.flow == that.flow
					       && this.from.getId() == that.getFrom().getId()
					       && this.to.getId() == that.getTo().getId();
		}
		return false;
	}
}

