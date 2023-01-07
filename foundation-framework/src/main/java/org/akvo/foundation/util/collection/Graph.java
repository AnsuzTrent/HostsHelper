package org.akvo.foundation.util.collection;

import org.akvo.foundation.util.collection.graph.Edge;
import org.akvo.foundation.util.collection.graph.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Graph {
    public static final int SELF_CIRCLE = -1;
    private final List<List<Edge>> adjacencyMatrix = new ArrayList<>();
    private final List<Edge> edgeList = new ArrayList<>();
    private final Map<Vertex, Integer> vertexIndexMap = new ConcurrentHashMap<>();
    private final Map<Integer, Vertex> indexVertexMap = new ConcurrentHashMap<>();
    private final boolean directed;

    public Graph(boolean directed) {
        this.directed = directed;
    }

    public void appendVertex(Vertex vertex) {
        int index = refreshAdjacencyMatrix();
        vertexIndexMap.put(vertex, index);
        indexVertexMap.put(index, vertex);
    }

    private int refreshAdjacencyMatrix() {
        adjacencyMatrix.add(new ArrayList<>());
        int newSize = adjacencyMatrix.size();
        adjacencyMatrix.forEach(matrix -> {
            while (matrix.size() < newSize) {
                matrix.add(null);
            }
        });
        return newSize - 1;
    }

    public String printAdjacencyMatrix() {
        AtomicInteger integer = new AtomicInteger(0);
        String headLine = IntStream.range(0, adjacencyMatrix.size())
            .boxed()
            .map(indexVertexMap::get)
            .map(Vertex::toString)
            .collect(Collectors.joining("\t", "\t", ""));
        return adjacencyMatrix.stream()
            .map(row -> row.stream()
                .map(Edge::getWeight)
                .map(String::valueOf)
                .collect(Collectors.joining("\t",
                    indexVertexMap.get(integer.getAndIncrement()).toString(),
                    "")))
            .collect(Collectors.joining("\n", headLine, ""));
    }

    public void appendEdge(Edge edge) {
        if (Objects.isNull(edge)) {
            throw new IllegalArgumentException("edge null");
        }
        if (directed ^ edge.isDirected()) {
            throw new IllegalArgumentException("undirected graph");
        }
        Vertex v1 = edge.getV1();
        Integer i1 = vertexIndexMap.get(v1);
        if (Objects.isNull(i1)) {
            throw new IllegalArgumentException("vertex is not exist: " + v1);
        }
        Vertex v2 = edge.getV2();
        Integer i2 = vertexIndexMap.get(v2);
        if (Objects.isNull(i2)) {
            throw new IllegalArgumentException("vertex is not exist: " + v2);
        }
        edgeList.add(edge);
        adjustAdjacencyMatrix(i1, i2, edge);
        if (!directed) {
            adjustAdjacencyMatrix(i2, i1, edge);
        }
    }

    private void adjustAdjacencyMatrix(Integer i1, Integer i2, Edge edge) {
        List<Edge> row = adjacencyMatrix.get(i1);
        if (row == null || row.isEmpty()) {
            throw new IllegalArgumentException("vertex is not exist: " + i1);
        }
        Edge edge1 = row.get(i2);
        if (edge1 != null) {
            throw new IllegalArgumentException("vertex is not exist: " + i1);
        }
        row.set(i2, edge);
    }
}
