package org.akvo.foundation.util.collection.graph;

public abstract class Edge {
    Vertex v1;
    Vertex v2;
    int weight;
    boolean directed;

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public Vertex getV1() {
        return v1;
    }

    public void setV1(Vertex v1) {
        this.v1 = v1;
    }

    public Vertex getV2() {
        return v2;
    }

    public void setV2(Vertex v2) {
        this.v2 = v2;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
