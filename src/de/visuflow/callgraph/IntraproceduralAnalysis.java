package de.visuflow.callgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unipaderborn.visuflow.model.VFClass;
import de.unipaderborn.visuflow.model.VFMethod;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class IntraproceduralAnalysis extends ForwardFlowAnalysis<Unit, Set<FlowAbstraction>> {
	public int flowThroughCount = 0;

	static ExceptionalUnitGraph eg;
	public static int nodeNumber;
	public static int edgeNumber;
	public static int nodeCount = 0;
	public static int edgeCount = 0;
	public static HashMap<Unit, Integer> nodesMap = new HashMap<>();
	public static HashMap<Integer, List<Integer>> edgesMap = new HashMap<>();
	public static HashMap<SootMethod, ControlFlowGraph> hashMap = new HashMap<>();
	public static Node[] nodes = new Node[20];
	public static Edge[] edges = new Edge[20];
	public static List<Node> listNodes;
	public static List<Edge> listEdges;

	public IntraproceduralAnalysis(Body b, final List<VFClass> vfClasses) {
		super(new ExceptionalUnitGraph(b));
		Options.v().set_keep_line_number(true);
		nodeNumber=0;
		edgeNumber=0;
		listNodes = new ArrayList<>();
		listEdges = new ArrayList<>();
		ControlFlowGraph g = new ControlFlowGraph();
		Unit head = null;
		eg = new ExceptionalUnitGraph(b);
		List<Unit> list = eg.getHeads();
		Iterator<Unit> it1 = list.iterator();
		while (it1.hasNext()) {
			head = it1.next();
			nodeNumber++;
			Node node = new Node(head, nodeNumber);
			listNodes.add(node);
			break;
		}
		traverseUnits(head);
		g.listEdges = listEdges;
		g.listNodes = listNodes;
		VFMethod method = new VFMethod(b.getMethod());
		method.setBody(b);
		method.setControlFlowGraph(g);
		
}

	public static void traverseUnits(Unit currentNode) {
		boolean present = false;
		List<Unit> l = eg.getSuccsOf(currentNode);
		Iterator<Unit> it = l.iterator();
		while (it.hasNext()) {
			Unit temp = it.next();
			Iterator<Node> nodesIterator = listNodes.iterator();
			while (nodesIterator.hasNext()) {
				Node node = (Node) nodesIterator.next();
				if (node.getLabel().equals(temp)) {
					present = true;
				}
			}
			if (!present) {
				nodeNumber++;
				Node node = new Node(temp, nodeNumber);
				listNodes.add(node);
			}
			Node source = null, destination = null;
			Iterator<Node> it1 = listNodes.iterator();
			while (it1.hasNext()) {
				Node node = (Node) it1.next();
				if (node.getLabel().equals(currentNode)) {
					source = node;
				}
				if (node.getLabel().equals(temp)) {
					destination = node;
				}
			}
			edgeNumber++;
			Edge edgeEntry = new Edge(edgeNumber, source, destination);
			listEdges.add(edgeEntry);
			traverseUnits(temp);
		}

	}

	@Override
	protected void flowThrough(Set<FlowAbstraction> in, Unit d, Set<FlowAbstraction> out) {

	}

	@Override
	protected Set<FlowAbstraction> newInitialFlow() {
		return new HashSet<FlowAbstraction>();
	}

	@Override
	protected Set<FlowAbstraction> entryInitialFlow() {
		return new HashSet<FlowAbstraction>();
	}

	@Override
	protected void merge(Set<FlowAbstraction> in1, Set<FlowAbstraction> in2, Set<FlowAbstraction> out) {
		out.addAll(in1);
		out.addAll(in2);
	}

	@Override
	protected void copy(Set<FlowAbstraction> source, Set<FlowAbstraction> dest) {
		dest.clear();
		dest.addAll(source);
	}

	public void doAnalyis() {
		super.doAnalysis();
	}

}
