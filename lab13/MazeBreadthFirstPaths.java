import java.util.PriorityQueue;

/**
 * @author Josh Hug
 */
public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits visible fields:
    protected int[] distTo;
    protected int[] edgeTo;
    protected boolean[] marked;
    */
    int s;
    int t;

    /**
     * A breadth-first search of paths in M from (SOURCEX, SOURCEY) to
     * (TARGETX, TARGETY).
     */
    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY,
                                 int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /**
     * Conducts a breadth first search of the maze starting at the source.
     */
    private void bfs(int s) {
        /* Your code here. */
        PriorityQueue<Integer> q = new PriorityQueue<Integer>();
        for (int v = 0; v < maze.V(); v++) distTo[v] = Integer.MAX_VALUE;
        distTo[s] = 0;
        marked[s] = true;

        q.add(s);
        announce();


        while (!q.isEmpty()) {
            int v = q.poll();
            if (v == t) return;
            announce();
            for (int w : maze.adj(v)) {
                if (!marked[w]) {
                    edgeTo[w] = v;
                    announce();
                    distTo[w] = distTo[v] + 1;
                    marked[w] = true;
                    q.add(w);
                }
            }
        }
    }


    @Override
    public void solve() {
        bfs(s);
    }
}

