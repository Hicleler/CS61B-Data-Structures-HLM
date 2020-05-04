import java.util.PriorityQueue;
import java.util.Comparator;
/**
 *  @author Josh Hug
 */

public class MazeAStarPath extends MazeExplorer {
    private int s;
    private int t;
    private Maze maze;

    public MazeAStarPath(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Estimate of the distance from v to the target. */
    private int h(int v) {
        return distance(t,v);
    }

    /** Performs an astar search from vertex s. */
    private int distance(int sourceX,int sourceY,int targetX,int targetY)
    {
        return Math.abs(sourceX-targetX)+Math.abs(sourceY-targetY);
    }
    private int distance(int v,int w)
    {
        int sourceX = maze.toX(v);
        int sourceY = maze.toY(v);
        int targetX = maze.toX(w);
        int targetY = maze.toY(w);
        return distance(sourceX,sourceY,targetX,targetY);
    }
    @SuppressWarnings("unchecked")
    private void astar(int s) {
        @SuppressWarnings("rawtypes")
        PriorityQueue<Integer> pq =new PriorityQueue<Integer>(maze.V(),
            new Comparator()
            {

                @Override
                public int compare(Object lhs, Object rhs) {

                    if(h((Integer)lhs) < h((Integer)rhs))
                    {
                        return -1;
                    }
                    else if(h((Integer)lhs) > h((Integer)rhs))
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                }

            });
        if(s == t)
        {
            return;
        }
        pq.add(s);
        while(!pq.isEmpty())
        {
            int v = pq.remove();
            marked[v] =true;
            announce();
            if(v == t) return;
            if(true)
            {
                for(int w : maze.adj(v))
                {
                    if(distTo[w] > distTo[v]+1)
                    {
                        distTo[w] = distTo[v]+1;
                        edgeTo[w] = v;
                        announce();
                        pq.add(w);
                    }
                }
            }
            marked[v] = true;
        }
    }

    @Override
    public void solve() {
        astar(s);
    }

} 