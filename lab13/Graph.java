import java.util.*;

/**
 * A weighted graph.
 *
 * @author
 */
public class Graph {

    /**
     * Adjacency lists by vertex number.
     */
    private LinkedList<Edge>[] adjLists;
    /**
     * Number of vertices in me.
     */
    private int vertexCount;

    /**
     * A graph with NUMVERTICES vertices and no edges.
     */
    @SuppressWarnings("unchecked")
    public Graph(int numVertices) {
        adjLists = (LinkedList<Edge>[]) new LinkedList[numVertices];
        for (int k = 0; k < numVertices; k++) {
            adjLists[k] = new LinkedList<Edge>();
        }
        vertexCount = numVertices;
    }

    /**
     * Tests of Graph.
     */
    public static void main(String[] unused) {
        // Put some tests here!

        Graph g1 = new Graph(5);
        g1.addEdge(0, 1, 1);
        g1.addEdge(0, 2, 1);
        g1.addEdge(0, 4, 1);
        g1.addEdge(1, 2, 1);
        g1.addEdge(2, 0, 1);
        g1.addEdge(2, 3, 1);
        g1.addEdge(4, 3, 1);

        Graph g2 = new Graph(5);
        g2.addEdge(0, 1, 1);
        g2.addEdge(0, 2, 1);
        g2.addEdge(0, 4, 1);
        g2.addEdge(1, 2, 1);
        g2.addEdge(2, 3, 1);
        g2.addEdge(4, 3, 1);
    }

    /**
     * Add to the graph a directed edge from vertex V1 to vertex V2,
     * with weight EDGEWEIGHT. If the edge already exists, replaces
     * the weight of the current edge EDGEWEIGHT.
     */
    public void addEdge(int v1, int v2, int edgeWeight) {
        if (!isAdjacent(v1, v2)) {
            LinkedList<Edge> v1Neighbors = adjLists[v1];
            v1Neighbors.add(new Edge(v1, v2, edgeWeight));
        } else {
            LinkedList<Edge> v1Neighbors = adjLists[v1];
            for (Edge e : v1Neighbors) {
                if (e.to() == v2) {
                    e.edgeWeight = edgeWeight;
                }
            }
        }
    }

    /**
     * Add to the graph an undirected edge from vertex V1 to vertex V2,
     * with weight EDGEWEIGHT. If the edge already exists, replaces
     * the weight of the current edge EDGEWEIGHT.
     */
    public void addUndirectedEdge(int v1, int v2, int edgeWeight) {
        addEdge(v1, v2, edgeWeight);
        addEdge(v2, v1, edgeWeight);
    }

    /**
     * Returns true iff there is an edge from vertex FROM to vertex TO.
     */
    public boolean isAdjacent(int from, int to) {
        for (Edge e : adjLists[from]) {
            if (e.to() == to) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of all the neighboring vertices u
     * such that the edge (VERTEX, u) exists in this graph.
     */
    public List<Integer> neighbors(int vertex) {
        ArrayList<Integer> neighbors = new ArrayList<>();
        for (Edge e : adjLists[vertex]) {
            neighbors.add(e.to());
        }
        return neighbors;
    }

    /**
     * Runs Dijkstra's algorithm starting from vertex STARTVERTEX and returns
     *      * an integer array consisting of the shortest distances
     *      * from STARTVERTEX to all other vertices.
     */
//    public int[] dijkstras(int startVertex) {
//        int[] distTo = new int[this.vertexCount];
//
//        for(int i = 0; i < distTo.length; i++) {
//            distTo[i] = Integer.MAX_VALUE;
//        }
//
//        distTo[startVertex] = 0;
//
//        PriorityQueue<Integer> H = new PriorityQueue<Integer>();
//        H.add(startVertex);
//        for(int i = 0;  i < startVertex; i++) {
//            if(i!=startVertex){
//                H.add(i);
//            }
//        }
//
//        while(!H.isEmpty()) {
//            int u = H.poll();
//            for(Edge e : adjLists[u]) {
//                if(distTo[e.to] > distTo[u] + e.info()){
//                    distTo[e.to] = distTo[u] + e.info();
//                }
//            }
//
//        }
//
//        return distTo;
//    }
    int minDistance(int dist[], Boolean sptSet[]) {
        // Initialize min value
        int min = Integer.MAX_VALUE, min_index = -1;

        for (int v = 0; v < vertexCount; v++)
            if (sptSet[v] == false && dist[v] <= min) {
                min = dist[v];
                min_index = v;
            }

        return min_index;
    }

    public int[] dijkstras(int startVertex) {
        int dist[] = new int[vertexCount];

        Boolean sptSet[] = new Boolean[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            dist[i] = Integer.MAX_VALUE;
            sptSet[i] = false;
        }

        dist[startVertex] = 0;

        for (int count = 0; count < vertexCount - 1; count++) {

            int u = minDistance(dist, sptSet);

            sptSet[u] = true;

            for (int v = 0; v < vertexCount; v++){

                if (!sptSet[v] && isAdjacent(u, v) && dist[u] != Integer.MAX_VALUE
                    && dist[v] > dist[u] + getEdge(u, v).info()) {
                    dist[v] = dist[u] + getEdge(u, v).info();
                }

            }

        }

        return dist;
    }

    /**
     * Returns the edge (V1, V2). (you may find this helpful to implement!)
     */
    private Edge getEdge(int v1, int v2) {
            for (Edge e : adjLists[v1]) {
                if (e.to() == v2) {
                    return e;
                }
            }
            return null;
    }

    /**
     * Represents an edge in this graph.
     */
    private class Edge {

        /**
         * End points of this edge.
         */
        private int from, to;
        /**
         * Weight label of this edge.
         */
        private int edgeWeight;

        /**
         * The edge (V0, V1) with weight WEIGHT.
         */
        Edge(int v0, int v1, int weight) {
            this.from = v0;
            this.to = v1;
            this.edgeWeight = weight;
        }

        /**
         * Return neighbor vertex along this edge.
         */
        public int to() {
            return to;
        }

        /**
         * Return weight of this edge.
         */
        public int info() {
            return edgeWeight;
        }

        @Override
        public String toString() {
            return "(" + from + "," + to + ",dist=" + edgeWeight + ")";
        }

    }
}
