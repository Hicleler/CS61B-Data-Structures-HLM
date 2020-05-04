import java.util.Observable;
/**
 *  @author Josh Hug
 */

public class MazeCycles extends MazeExplorer {

    private Maze maze;
    private int cycle = -1;
    int s;
    int t;

    public MazeCycles(Maze m) {
        super(m);
        maze = m;
        s = maze.xyTo1D(1, 1);
        t = maze.xyTo1D(maze.N(), maze.N());
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    @Override
    public void solve() {
        dfs(s);
    }

    private void dfs(int s)
    {
        marked[s] = true;
        for(int w : maze.adj(s))
        {
            if(edgeTo[s]!=w)
            {
                announce();
                edgeTo[w] = s;
                cycle = w;
                return;
            }
            if(!marked[w])
            {
                edgeTo[w] = s;
                dfs(w);
            }
        }
    }
}