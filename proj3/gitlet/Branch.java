package gitlet;

import java.io.Serializable;

/** Gitlet Branch.
 * @author Laiming Huang
 */

public class Branch implements Serializable {

    /** Name of the branch. */
    private String branchName;
    /** The head of the branch. */
    private Commit branchHead;
    /** Whether this branch is the current branch. */
    private boolean currBranch;

    /** Constructor for the Branch class.
     * @param head head.
     * @param name name.
     */
    public Branch(String name, Commit head) {
        this.branchName = name;
        this.branchHead = head;
        this.currBranch = false;
    }

    /**
     * Get name of the branch.
     * @return Return branch name.
     */
    public String getName() {
        return branchName;
    }

    /**
     * Get head of the branch.
     * @return Return current head name.
     */
    public Commit getHead() {
        return branchHead;
    }

    /**
     * Check whether the branch is the current branch.
     * @return Return whether the branch is the current branch.
     */
    public boolean isCurrBranch() {
        return currBranch;
    }

    /**
     * Set this branch to be current branch.
     */
    public void toCurr() {
        currBranch = true;
    }

    /**
     * Set this branch to not be current branch.
     */
    public void disCurr() {
        currBranch = false;
    }

    /**
     * Change this branch's head to given commit.
     * @param c Given commit that we wish to set to head.
     */
    public void changeHeadTo(Commit c) {
        this.branchHead = c;
    }

}
