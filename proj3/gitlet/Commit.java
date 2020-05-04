package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/** Gitlet Commit.
 * @author Laiming Huang
 */
public class Commit implements Serializable {

    /**
     * All children of the commit.
     */
    private ArrayList<String> children;
    /**
     * All tracked files of the commit.
     */
    private HashMap<String, String> trackedFiles;
    /**
     * First parent of the commit.
     */
    private String parent;
    /**
     * Second parent of the commit.
     */
    private String parent2;
    /**
     * Message of the commit.
     */
    private String message;
    /**
     * Commit time.
     */
    private Date _time;

    /**
     * Default Constructor.
     */
    public Commit() {
        parent = "";
        parent2 = "";
        message = "initial commit";
        _time = new Date(0);
        trackedFiles = new HashMap<String, String>();
        children = new ArrayList<String>();
    }

    /**
     * Constructor when there is only one parent.
     * @param trackingFiles All tracking files of the commit.
     * @param parent1 The sole parent of the commit.
     * @param msg Message of ths commit.
     * @param time Time of the commit.
     */
    public Commit(HashMap<String, String> trackingFiles, String parent1,
                  String msg, Date time) {
        this.parent = parent1;
        this.parent2 = null;
        this._time = time;
        this.message = msg;
        this.trackedFiles = new HashMap<String, String>();
        this.trackedFiles.putAll(trackingFiles);
        this.children = new ArrayList<String>();
    }

    /**
     * Constructor when there are two parents.
     * @param trackingFiles All tracking files of the commit.
     * @param parent1 The first parent of the commit.
     * @param secondParent The second parent of the commit.
     * @param theMessage Message of ths commit.
     * @param time Time of the commit.
     */
    public Commit(HashMap<String, String> trackingFiles, String parent1,
                  String secondParent, String theMessage, Date time) {
        this.parent = parent1;
        this.parent2 = secondParent;
        this._time = time;
        this.message = theMessage;
        this.trackedFiles = new HashMap<String, String>();
        this.trackedFiles.putAll(trackingFiles);
        this.children = new ArrayList<String>();
    }

    /**
     * Get the first parent of the commit.
     * @return Parent 1.
     */
    public String getParent() {
        return parent;
    }

    /**
     * Get the first parent of the commit.
     * @return Parent 1.
     */
    public String getParent1() {
        return parent;
    }

    /**
     * Get the second parent of the commit.
     * @return Parent 2.
     */
    public String getParent2() {
        return parent2;
    }

    /**
     * Get parent(s) of the commit.
     * @return HashSet of arent(s).
     */
    public HashSet<String> getParents() {
        HashSet<String> parents = new HashSet<String>();
        if (parent != null) {
            parents.add(parent);
        }
        if (parent2 != null) {
            parents.add(parent2);
        }
        return parents;
    }

    /**
     * Get the commit message.
     * @return message of the commit.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get commit time.
     * @return Time of the commit.
     */
    public Date getTime() {
        return _time;
    }

    /**
     * Get the tracking files.
     * @return Tracking files of the commit.
     */
    public HashMap<String, String> getTrackedFiles() {
        return trackedFiles;
    }

    /**
     * Get the children of the commit.
     * @return Children of the commit.
     */
    public ArrayList<String> getChildren() {
        return children;
    }

    /**
     * Check whether this commit is the initial (root) commit.
     * @return Whether this commit is the initial (root) commit.
     */
    public boolean isRoot() {
        return message.equals("initial commit");
    }

    /**
     * Convert time to standard format.
     * @return Time in standard format.
     */
    public String timeToString() {
        SimpleDateFormat formatedDate =
            new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        String res = formatedDate.format(_time);
        return "Date: " + res;
    }

    /**
     * Generate Sha1 of the commit.
     * @return Sha1 of the commit.
     */
    public String toSha1() {
        String represent = "";
        StringBuilder tmp = new StringBuilder();
        tmp.append(parent);
        tmp.append(message);
        tmp.append(timeToString());
        represent = tmp.toString();
        return Utils.sha1(represent);
    }
}
