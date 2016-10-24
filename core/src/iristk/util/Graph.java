package iristk.util;

import java.util.*;

public class Graph<Vertex, Edge> {

	private Set<Vertex> settledNodes;
	private Set<Vertex> unSettledNodes;
	private Map<Vertex, Vertex> predecessors;
	private Map<Vertex, Float> distance;
	
	private Map<Vertex,List<Edge>> outEdges = new HashMap<>();
	private Map<Edge,Vertex> destinations = new HashMap<>();
	private Map<Edge,Float> weights = new HashMap<>();

	private void findMinimalDistances(Vertex node) {
		List<Vertex> adjacentNodes = getNeighbors(node);
		for (Vertex target : adjacentNodes) {
			float dist = getShortestDistance(node) + weights.get(getEdge(node, target));
			if (getShortestDistance(target) > dist) {
				distance.put(target, dist);
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}
	}

	private List<Vertex> getNeighbors(Vertex node) {
		List<Vertex> neighbors = new LinkedList<Vertex>();
		if (outEdges.containsKey(node)) {
			for (Edge edge : outEdges.get(node)) {
				Vertex dest = destinations.get(edge);
				if (!isSettled(dest)) {
					neighbors.add(dest);
				}
			}
		}
		return neighbors;
	}

	private Vertex getMinimum(Set<Vertex> vertexes) {
		Vertex minimum = null;
		for (Vertex vertex : vertexes) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}

	private boolean isSettled(Vertex vertex) {
		return settledNodes.contains(vertex);
	}

	private float getShortestDistance(Vertex destination) {
		Float d = distance.get(destination);
		if (d == null) {
			return Float.MAX_VALUE;
		} else {
			return d;
		}
	}

	/*
	 * This method returns the path from the source to the selected target and
	 * NULL if no path exists
	 */
	public LinkedList<Edge> getShortestPath(Vertex source, Vertex target) {
		settledNodes = new HashSet<Vertex>();
		unSettledNodes = new HashSet<Vertex>();
		distance = new HashMap<Vertex, Float>();
		predecessors = new HashMap<Vertex, Vertex>();
		distance.put(source, 0f);
		unSettledNodes.add(source);
		while (unSettledNodes.size() > 0) {
			Vertex node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node);
		}

		// check if a path exists
		if (predecessors.get(target) == null) {
			return null;
		}
		
		LinkedList<Edge> path = new LinkedList<Edge>();
		Vertex step = target;
		Vertex prev;
		while (predecessors.get(step) != null) {
			prev = step;
			step = predecessors.get(step);
			path.add(0, getEdge(step, prev));
		}
		return path;
	}

	private Edge getEdge(Vertex from, Vertex to) {
		if (outEdges.get(from) != null) {
			for (Edge edge : outEdges.get(from)) {
				if (destinations.get(edge).equals(to)) {
					return edge;
				}
			}
		}
		return null;
	}

	public void addEdge(Edge e, Vertex begin, Vertex end, float weight) {
		OUTER: {
			if (outEdges.containsKey(begin)) {
				List<Edge> edges = outEdges.get(begin);
				for (int i = 0; i < edges.size(); i++) {
					Edge edge = edges.get(i);
					Vertex dest = destinations.get(edge);
					if (dest.equals(end) && weight < weights.get(edge)) {
						edges.set(i, e);
						break OUTER;
					}
				}
				edges.add(e);
			} else {
				LinkedList<Edge> list = new LinkedList<>();
				list.add(e);
				outEdges.put(begin, list);
			}
		}
		destinations.put(e, end);
		weights.put(e, weight);
	}

}
