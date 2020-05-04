package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

/**
 * Repo class containing current gitlet monitoring status.
 * @author Laiming Huang.
 */
public class Repo implements Serializable {

    /**
     * Staged area.
     */
    private HashMap<String, String> _staged;
    /**
     * Untracked files.
     */
    private HashSet<String> _untracked;
    /**
     * Current head.
     */
    private Commit head;
    /**
     * All the commits.
     */
    private HashMap<String, Commit> commitCollection;
    /**
     * All the branches.
     */
    private HashMap<String, Branch> branchCollection;
    /**
     * All the remote names.
     */
    private HashMap<String, String> _remotes;

    /**
     * Default constructor for Repo class.
     */
    public Repo() {
        File staging = new File("./.gitlet/.staging/");
        staging.mkdirs();
        branchCollection = new HashMap<String, Branch>();
        _staged = new HashMap<String, String>();
        _remotes = new HashMap<String, String>();
        _untracked = new HashSet<String>();
        commitCollection = new HashMap<String, Commit>();
        Commit initialCommit = new Commit();
        Branch master = new Branch("master", initialCommit);
        master.toCurr();
        head = master.getHead();
        branchCollection.put("master", master);
        commitCollection.put(initialCommit.toSha1(), initialCommit);
    }

    /**
     * Save the current repo.
     * @param base Repo to save.
     */
    public static void save(Repo base) {
        File repo = new File("./.gitlet/repo");
        try {
            ObjectOutputStream out =
                new ObjectOutputStream(new FileOutputStream(repo));
            out.writeObject(base);
            out.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Read the current repo.
     * @return a repo.
     */
    public static Repo read() {
        Repo repo = new Repo();
        File serializedRepo = new File(".gitlet/repo");
        try {
            ObjectInputStream inp =
                new ObjectInputStream(new FileInputStream(serializedRepo));
            repo = (Repo) inp.readObject();
            inp.close();
        } catch (IOException | ClassCastException
            | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
        return repo;
    }

    /**
     * Execute add command.
     * @param fileName file to be staged.
     */
    public void add(String fileName) throws IOException {
        File toAdd = new File(fileName);
        if (!toAdd.exists()) {
            System.err.println("File does not exist.");
            System.exit(0);
        } else {
            String fileContent = Utils.readContentsAsString(toAdd);
            String fileSha1 = Utils.sha1(fileContent);
            if (_staged.containsKey(fileName)) {
                if (_staged.get(fileName).equals(fileSha1)) {
                    if (head.getTrackedFiles().get(fileName) != null
                        && head.getTrackedFiles().get(fileName)
                        .equals(fileSha1)) {
                        File staged = new File(".gitlet/.staging/" + fileSha1);
                        if (staged.exists()) {
                            staged.delete();
                        }
                        if (_staged.containsKey(fileName)) {
                            _staged.remove(fileName);
                        }
                    }
                } else {
                    if (head.getTrackedFiles().get(fileName) != null
                        && head.getTrackedFiles().get(fileName)
                        .equals(fileSha1)) {
                        File staged =
                            new File(".gitlet/.staging/" + fileSha1);
                        if (staged.exists()) {
                            staged.delete();
                        }
                        if (_staged.containsKey(fileName)) {
                            _staged.remove(fileName);
                        }
                    }
                    String oldFileSha1 = _staged.get(fileName);
                    File oldfile =
                        new File(".gitlet/.staging/" + oldFileSha1);
                    oldfile.delete();
                    _staged.remove(fileName);
                    File newfile =
                        new File(".gitlet/.staging/" + fileSha1);
                    newfile.createNewFile();
                    Utils.writeContents(newfile, fileContent);
                    _staged.put(fileName, fileSha1);
                }
            } else {
                addHelper(fileName, fileSha1, fileContent);
            }
            if (_untracked.contains(fileName)) {
                _untracked.remove(fileName);
            }
        }
    }

    /**
     * Helper for add command.
     * @param fileName File name to add.
     * @param fileSha1 Sha1 of file.
     * @param fileContent Content of file.
     * @throws IOException
     */
    private void addHelper(String fileName, String fileSha1,
                           String fileContent) throws IOException {
        if (head.getTrackedFiles().get(fileName) != null
            && head.getTrackedFiles().get(fileName).equals(fileSha1)) {
            File staged =
                new File(".gitlet/.staging/" + fileSha1);
            if (staged.exists()) {
                staged.delete();
            }
        } else {
            File staged =
                new File(".gitlet/.staging/" + fileSha1);
            staged.createNewFile();
            Utils.writeContents(staged, fileContent);
            _staged.put(fileName, fileSha1);
        }
    }

    /**
     * Execute commit command.
     * @param commitMessage The commit message.
     */
    public void commit(String commitMessage) throws IOException {
        if (_staged.isEmpty() && _untracked.isEmpty()) {
            System.err.println("No changes added to the commit.");
            System.exit(0);
        } else {
            HashMap<String, String> currTracking =
                new HashMap<String, String>();
            currTracking.putAll(_staged);
            for (String name : head.getTrackedFiles().keySet()) {
                if (!currTracking.containsKey(name)) {
                    String inheritedFileSha1 = head.getTrackedFiles().get(name);
                    currTracking.put(name, inheritedFileSha1);
                }
            }

            for (String name : head.getTrackedFiles().keySet()) {
                if (_untracked.contains(name)) {
                    currTracking.remove(name);
                }
            }

            String parentSha1 = head.toSha1();
            Date now = new Date();
            Commit c = new Commit(currTracking, parentSha1, commitMessage, now);
            head.getChildren().add(c.toSha1());
            commitCollection.put(c.toSha1(), c);
            currBranch().changeHeadTo(c);
            head = c;
            _staged.clear();
            _untracked.clear();
        }
    }

    /**
     * Execute rm command.
     * @param toRemove File to remove.
     */
    public void rm(String toRemove) {
        if (!_staged.containsKey(toRemove)
            && !head.getTrackedFiles().containsKey(toRemove)) {
            System.err.println("No reason to remove the file.");
            System.exit(0);
        }
        if (_staged.containsKey(toRemove)) {
            _staged.remove(toRemove);
        }
        if (head.getTrackedFiles().containsKey(toRemove)) {
            _untracked.add(toRemove);
        }
        File fileToRm = new File(toRemove);
        if (fileToRm.exists()) {
            if (head.getTrackedFiles().containsKey(toRemove)) {
                fileToRm.delete();
            }
        }
    }

    /**
     * Find the current branch.
     * @return current branch.
     */
    private Branch currBranch() {
        for (Branch branch : branchCollection.values()) {
            if (branch.isCurrBranch()) {
                return branch;
            }
        }
        return null;
    }

    /**
     * Execute checkout (by namd) command.
     * @param fileName The name of file to checkout.
     */
    public void checkoutByName(String fileName) {
        if (!head.getTrackedFiles().containsKey(fileName)) {
            System.err.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            File file = new File(fileName);
            if (file.exists()) {
                String fileSha1 = head.getTrackedFiles().get(fileName);
                File theFile =
                    new File(".gitlet/.staging/" + fileSha1);
                String f = Utils.readContentsAsString(theFile);
                Utils.writeContents(file, f);
            } else {
                try {
                    file.createNewFile();
                    String fileSha1 = head.getTrackedFiles().get(fileName);
                    File theFile =
                        new File(".gitlet/.staging/" + fileSha1);
                    String f = Utils.readContentsAsString(theFile);
                    Utils.writeContents(file, f);
                } catch (IOException e) {
                    System.exit(0);
                }
            }

        }
    }

    /**
     * Execute checkout (by commit and file name) command.
     * @param commit The commit to checkout.
     * @param fileName The file name to checkout.
     */
    public void checkoutLong(String commit, String fileName) {
        String realCommitID = "NOT FOUND";
        for (String id : commitCollection.keySet()) {
            if (id.startsWith(commit)) {
                realCommitID = id;
            }
        }
        if (!commitCollection.containsKey(realCommitID)) {
            System.err.println("No commit with that id exists.");
            System.exit(0);
        } else if (!commitCollection.get(realCommitID)
            .getTrackedFiles().containsKey(fileName)) {
            System.err.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            String fileSha1 = commitCollection.get(realCommitID)
                .getTrackedFiles().get(fileName);
            File theFile = new File(".gitlet/.staging/" + fileSha1);
            String f = Utils.readContentsAsString(theFile);

            File file = new File(fileName);
            if (file.exists()) {
                Utils.writeContents(file, f);
            } else {
                try {
                    file.createNewFile();
                    Utils.writeContents(file, f);
                } catch (IOException e) {
                    System.exit(0);
                }
            }
        }

    }

    /**
     * Execute checkout (by branch) command.
     * @param branch The branch to checkout.
     */
    public void checkoutByBranch(String branch) {
        if (!branchCollection.containsKey(branch)) {
            System.err.println("No such branch exists.");
            System.exit(0);
        } else if (branchCollection.get(branch).isCurrBranch()) {
            System.err.println("No need to checkout the current branch.");
            System.exit(0);
        } else {
            Branch theBranch = branchCollection.get(branch);
            Commit theBranchHead = theBranch.getHead();
            HashMap<String, String> currTracking
                = head.getTrackedFiles();
            HashMap<String, String> theBranchTracking
                = theBranchHead.getTrackedFiles();
            for (String s : theBranchTracking.keySet()) {
                if (!currTracking.containsKey(s)) {
                    File theFile = new File(s);
                    if (theFile.exists()) {
                        System.err.println("There is an untracked "
                            + "file in the way; "
                            + "delete it or add it first.");
                        System.exit(0);
                    }
                }
            }
            for (String file : theBranchTracking.keySet()) {
                checkoutLong(theBranchHead.toSha1(), file);
            }
            for (String file : currTracking.keySet()) {
                if (!theBranchTracking.containsKey(file)) {
                    File theFile = new File(file);
                    if (theFile.exists()) {
                        theFile.delete();
                    }
                }
            }
            currBranch().disCurr();
            theBranch.toCurr();
            head = theBranch.getHead();
            _staged.clear();
        }
    }

    /**
     * Execute log command.
     */
    public void log() {
        Commit curr = head;
        while (curr != null && !curr.isRoot()) {
            System.out.println("===");
            System.out.println("commit " + curr.toSha1());
            if (curr.getParents().size() == 2) {
                System.out.println("Merge: " + curr.getParent1().substring(0, 7)
                    + " " + curr.getParent2().substring(0, 7));
            }
            System.out.println(curr.timeToString());
            System.out.println(curr.getMessage());
            System.out.println();
            curr = commitCollection.get(curr.getParent());
        }
        System.out.println("===");
        System.out.println("commit " + curr.toSha1());
        if (curr.getParents().size() == 2) {
            System.out.println("Merge: " + curr.getParent1().substring(0, 7)
                + " " + curr.getParent2().substring(0, 7));
        }
        System.out.println(curr.timeToString());
        System.out.println(curr.getMessage());
    }

    /**
     * Execute global-log command.
     */
    public void globalLog() {
        Commit root = null;
        for (String commit : commitCollection.keySet()) {
            Commit curr = commitCollection.get(commit);
            if (!curr.isRoot()) {
                System.out.println("===");
                System.out.println("commit " + curr.toSha1());
                System.out.println(curr.timeToString());
                System.out.println(curr.getMessage());
                System.out.println();
            } else {
                root = curr;
            }
        }
        System.out.println("===");
        System.out.println("commit " + root.toSha1());
        System.out.println(root.timeToString());
        System.out.println(root.getMessage());
        System.out.println();
    }

    /**
     * Execute branch command.
     * @param branchName The name of the branch to add.
     */
    public void branch(String branchName) {
        if (branchCollection.containsKey(branchName)) {
            System.err.println("A branch with that name already exists.");
            System.exit(0);
        } else {
            Branch branch = new Branch(branchName, head);
            branchCollection.put(branchName, branch);
        }
    }

    /**
     * Execute rm-branch command.
     * @param branchName The name of the branch to add.
     */
    public void rmBranch(String branchName) {
        if (!branchCollection.containsKey(branchName)) {
            System.err.println("A branch with that name does not exist.");
            System.exit(0);
        } else if (branchCollection.get(branchName).isCurrBranch()) {
            System.err.println("Cannot remove the current branch.");
            System.exit(0);
        } else {
            branchCollection.remove(branchName);
        }
    }

    /**
     * Execute reset command.
     * @param commit The commit to reset to.
     */
    public void reset(String commit) {
        String realCommitID = "NOT FOUND";
        for (String id : commitCollection.keySet()) {
            if (id.startsWith(commit)) {
                realCommitID = id;
            }
        }
        if (!commitCollection.containsKey(realCommitID)) {
            System.err.println("No commit with that id exists.");
            System.exit(0);
        }
        HashMap<String, String> theCommit = commitCollection
            .get(realCommitID).getTrackedFiles();
        Set<String> trackedNames = theCommit.keySet();
        File all = new File("./");
        File[] allFiles = all.listFiles();

        for (File file : allFiles) {
            String fileName = file.getName();
            if (!head.getTrackedFiles().containsKey(fileName)
                && trackedNames.contains(fileName)) {
                System.err.println("There is an untracked "
                    + "file in the way"
                    + "; delete it or add it first.");
                System.exit(0);
            }
        }
        for (String name : trackedNames) {
            checkoutLong(realCommitID, name);
        }
        for (String name : head.getTrackedFiles().keySet()) {
            if (!trackedNames.contains(name)) {
                File tmp = new File(name);
                if (tmp.exists()) {
                    tmp.delete();
                }
            }
        }
        currBranch().changeHeadTo(commitCollection.get(realCommitID));
        this.head = currBranch().getHead();
        _staged.clear();
        _untracked.clear();
    }

    /**
     * Execute find command.
     * @param key The commit to find.
     */
    public void find(String key) {
        Boolean found = false;
        if (commitCollection.isEmpty()) {
            System.err.println("Found no commit with that message.");
            System.exit(0);
        } else {
            for (Commit commit : commitCollection.values()) {
                if (commit.getMessage().equals(key)) {
                    found = true;
                    System.out.println(commit.toSha1());
                }
            }
        }
        if (!found) {
            System.err.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    /**
     * Execute status command.
     */
    public void status() {
        System.out.println("=== Branches ===");
        Object[] branches =
            new ArrayList<String>(branchCollection.keySet()).toArray();
        Arrays.sort(branches);
        for (Object branch : branches) {
            if (branch.equals(currBranch().getName())) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        Object[] stagedFiles =
            new ArrayList<String>(_staged.keySet()).toArray();
        Arrays.sort(stagedFiles);
        for (Object stagedFile : stagedFiles) {
            System.out.println(stagedFile);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        Object[] removedFiles = new ArrayList<String>(_untracked).toArray();
        Arrays.sort(removedFiles);
        for (Object removedFile : removedFiles) {
            System.out.println(removedFile);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        File all = new File("./");
        File[] allFiles = all.listFiles();
        for (File file : allFiles) {
            if (Utils.checkPlainFile(file)) {
                if (modifiedHelper(file)) {
                    System.out.println(file.getName() + " (modified)");
                }
            }
        }
        deletedHelper();
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for (File file : allFiles) {
            if (Utils.checkPlainFile(file)) {
                if (!_staged.containsKey(file.getName())
                    && (!head.getTrackedFiles().containsKey(file.getName()))) {
                    System.out.println(file.getName());
                }
            }
        }
    }

    /**
     * Helper for modified.
     * @param file File to check.
     * @return Whether a file is modified.
     */
    public boolean modifiedHelper(File file) {
        boolean res = false;
        String fileName = file.getName();

        if (head.getTrackedFiles().containsKey(fileName)) {
            String fileStream = Utils.readContentsAsString(file);
            String fileSha1 = Utils.sha1(fileStream);
            if (!head.getTrackedFiles().get(fileName).equals(fileSha1)) {
                if (!_staged.containsKey(fileName)) {
                    res = true;
                }
            }
        }
        if (_staged.containsKey(fileName)) {
            String fileStream = Utils.readContentsAsString(file);
            String fileSha1 = Utils.sha1(fileStream);
            if (!_staged.get(fileName).equals(fileSha1)) {
                res = true;
            }
        }
        return res;
    }

    /**
     * Helper for deleted.
     */
    public void deletedHelper() {
        for (String fileName : _staged.keySet()) {
            File toCheck = new File(fileName);
            if (!toCheck.exists()) {
                System.out.println(toCheck.getName() + " (deleted)");
            }
        }

        for (String fileName : head.getTrackedFiles().keySet()) {
            if (!_untracked.contains(fileName)) {
                File theFile = new File(fileName);
                if (!theFile.exists()) {
                    System.out.println(theFile.getName() + " (deleted)");
                }
            }
        }
    }

    /**
     * Help merge command.
     * @param branch branch to merge to.
     * @throws IOException
     */
    public void mergeErrs(String branch) throws IOException {
        if (!branchCollection.containsKey(branch)) {
            System.err.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (!_staged.isEmpty() || !_untracked.isEmpty()) {
            System.err.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (branchCollection.get(branch).isCurrBranch()) {
            System.err.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Branch theBranch = branchCollection.get(branch);
        Commit theBranchHead = theBranch.getHead();
        HashMap<String, String> currTracking =
            head.getTrackedFiles();
        HashMap<String, String> theBranchTracking =
            theBranchHead.getTrackedFiles();
        Branch givenBranch = branchCollection.get(branch);
        for (String s : theBranchTracking.keySet()) {
            if (!currTracking.containsKey(s)) {
                File theFile = new File(s);
                if (theFile.exists()) {
                    System.err.println("There is an untracked"
                        + " file in the way"
                        + "; delete it or add it first.");
                    System.exit(0);
                }
            }
        }
    }
    /**
     * Execute merge command.
     * @param branch branch to merge to.
     * @throws IOException
     */
    public void merge(String branch) throws IOException {
        Boolean foundConflict = false;
        mergeErrs(branch);
        Branch givenBranch = branchCollection.get(branch);
        Commit splitPoint = splitPointHelper(givenBranch);
        HashMap<String, String> spTracked = splitPoint.getTrackedFiles();
        HashMap<String, String> targetBranchTracked =
            givenBranch.getHead().getTrackedFiles();
        HashMap<String, String> currTracked = head.getTrackedFiles();
        if (checkCase2(splitPoint, givenBranch)) {
            return;
        }
        for (String fileName : spTracked.keySet()) {
            if (targetBranchTracked.containsKey(fileName)
                && currTracked.containsKey(fileName)) {
                if (!targetBranchTracked.get(fileName)
                    .equals(spTracked.get(fileName))
                    && currTracked.get(fileName)
                    .equals(spTracked.get(fileName))) {
                    checkoutLong(givenBranch.getHead()
                        .toSha1(), fileName);
                    _staged.put(fileName, targetBranchTracked.get(fileName));
                } else if (!targetBranchTracked.get(fileName)
                    .equals(spTracked.get(fileName))
                    && (!currTracked.get(fileName)
                    .equals(spTracked.get(fileName)))) {
                    if (!currTracked.get(fileName)
                        .equals(targetBranchTracked.get(fileName))) {
                        conflictReport(fileName, branch);
                        foundConflict = true;
                    }
                }
            } else if (!targetBranchTracked.containsKey(fileName)
                && currTracked.containsKey(fileName)) {
                if (spTracked.get(fileName).equals(currTracked.get(fileName))) {
                    File toDelete = new File(fileName);
                    if (toDelete.exists()) {
                        toDelete.delete();
                    }
                    _untracked.add(fileName);
                } else {
                    conflictReport(fileName, branch);
                    foundConflict = true;
                }
            } else if (targetBranchTracked.containsKey(fileName)
                && (!currTracked.containsKey(fileName))) {
                if (!spTracked.get(fileName)
                    .equals(targetBranchTracked.get(fileName))) {
                    conflictReport(fileName, branch);
                    foundConflict = true;
                }
            }
        }
        mergeRest(targetBranchTracked, spTracked, currTracked,
            branch, foundConflict, givenBranch);
    }

    /**
     * Helper.
     * @param splitPoint sp.
     * @param givenBranch gb.
     * @return true or false.
     */
    private boolean checkCase2(Commit splitPoint, Branch givenBranch) {
        if (splitPoint.toSha1().equals(head.toSha1())) {
            System.out.println("Current branch fast-forwarded.");
            for (String file : givenBranch.getHead()
                .getTrackedFiles().keySet()) {
                checkoutLong(givenBranch.getHead().toSha1(), file);
            }
            for (String file : currBranch().getHead()
                .getTrackedFiles().keySet()) {
                if (!givenBranch.getHead()
                    .getTrackedFiles().containsKey(file)) {
                    File theFile = new File(file);
                    if (theFile.exists()) {
                        theFile.delete();
                    }
                }
            }
            currBranch().changeHeadTo(givenBranch.getHead());
            return true;
        } else if (splitPoint.toSha1()
            .equals(givenBranch.getHead().toSha1())) {
            System.out.println("Given branch is "
                + "an ancestor of the current branch.");
            givenBranch.changeHeadTo(head);
            return true;
        }
        return false;
    }

    /**
     * Helper.
     * @param targetBranchTracked da.
     * @param spTracked da.
     * @param currTracked da.
     * @param branch da.
     * @param foundConflict da.
     * @param givenBranch da.
     * @throws IOException
     */
    private void mergeRest(HashMap<String, String> targetBranchTracked,
                           HashMap<String, String> spTracked,
                           HashMap<String, String> currTracked,
                           String branch,
                           Boolean foundConflict,
                           Branch givenBranch) throws IOException {
        for (String s : targetBranchTracked.keySet()) {
            if (!spTracked.containsKey(s)) {
                if (currTracked.containsKey(s)) {
                    if (!currTracked.get(s)
                        .equals(targetBranchTracked.get(s))) {
                        conflictReport(s, branch);
                        foundConflict = true;
                    }
                } else if (!currTracked.containsKey(s)) {
                    checkoutLong(givenBranch.getHead().toSha1(), s);
                    _staged.put(s, targetBranchTracked.get(s));
                }
            }
        }
        mergedCommit(branch, foundConflict);
    }

    /**
     * Commit when merging.
     * @param targetBranch Branch to merge to.
     * @param conflict Whether conflict is found.
     * @throws IOException
     */
    public void mergedCommit(String targetBranch,
                             boolean conflict) throws IOException {
        if (_staged.isEmpty() && _untracked.isEmpty()) {
            System.err.println("No changes added to the commit.");
            System.exit(0);
        } else {
            HashMap<String, String> currTracking =
                new HashMap<String, String>();
            currTracking.putAll(_staged);
            for (String name : head.getTrackedFiles().keySet()) {
                if (!currTracking.containsKey(name)) {
                    String inheritedFileSha1 = head.getTrackedFiles().get(name);
                    currTracking.put(name, inheritedFileSha1);
                }
            }

            for (String name : head.getTrackedFiles().keySet()) {
                if (_untracked.contains(name)) {
                    currTracking.remove(name);
                }
            }

            String commitMessage = "Merged " + targetBranch
                + " into " + currBranch().getName() + ".";
            String parentSha1 = head.toSha1();
            String parent2Sha1 = branchCollection
                .get(targetBranch).getHead().toSha1();
            Date now = new Date();
            Commit c = new Commit(currTracking, parentSha1,
                parent2Sha1, commitMessage, now);
            head.getChildren().add(c.toSha1());
            commitCollection.put(c.toSha1(), c);
            currBranch().changeHeadTo(c);
            head = c;
            _staged.clear();
            _untracked.clear();

            if (conflict) {
                System.out.println("Encountered a merge conflict.");
            }
        }
    }

    /**
     * Report conflict.
     * @param fileName File name.
     * @param targetBranch Target branch.
     * @throws IOException
     */
    private void conflictReport(String fileName,
                                String targetBranch) throws IOException {
        String raw = "";
        String newF = "";
        File toWrite = new File(fileName);
        StringBuilder newContent = new StringBuilder();
        String rawFileSha1 = head.getTrackedFiles().get(fileName);
        File readFrom = new File(".gitlet/.staging/" + rawFileSha1);
        String newFileSha1 = branchCollection.get(targetBranch)
            .getHead().getTrackedFiles().get(fileName);
        File newReadFrom = new File(".gitlet/.staging/" + newFileSha1);
        newContent.append("<<<<<<< HEAD\n");
        if (readFrom.exists() && newReadFrom.exists()) {
            raw = Utils.readContentsAsString(readFrom);
            newF = Utils.readContentsAsString(newReadFrom);
            newContent.append(raw);
            newContent.append("=======\n");
            newContent.append(newF);
        } else if (readFrom.exists()) {
            raw = Utils.readContentsAsString(readFrom);
            newContent.append(raw);
            newContent.append("=======\n");
        } else if (newReadFrom.exists()) {
            newF = Utils.readContentsAsString(newReadFrom);
            newContent.append("=======\n");
            newContent.append(newF);
        }

        newContent.append(">>>>>>>\n");
        if (toWrite.exists()) {
            toWrite.delete();
        }
        toWrite.createNewFile();
        Utils.writeContents(toWrite, newContent.toString());

        String written = Utils.readContentsAsString(toWrite);
        File staged = new File(".gitlet/.staging/"
            + Utils.sha1(written));
        staged.createNewFile();
        Utils.writeContents(staged, written);
        _staged.put(fileName, Utils.sha1(written));
    }

    /**
     * Helper for split point.
     * @param mergeWith Branch to merge with.
     * @return
     */
    private Commit splitPointHelper(Branch mergeWith) {
        Commit targetHead = mergeWith.getHead();
        HashSet<String> allTargetParents = findAllParents(targetHead.toSha1());
        HashSet<String> visited = new HashSet<String>();
        LinkedList<Commit> theList = new LinkedList<Commit>();
        theList.add(head);

        while (!theList.isEmpty()) {
            Commit first = theList.poll();
            if (visited.contains(first.toSha1())) {
                continue;
            }
            if (allTargetParents.contains(first.toSha1())) {
                return first;
            }
            HashSet<String> parents = first.getParents();
            for (String par : parents) {
                theList.add(commitCollection.get(par));
            }
            visited.add(first.toSha1());
        }

        return null;
    }

    /**
     * Finding all parents.
     * @param commit The commit.
     * @return HashSet of all parents for the given commit.
     */
    private HashSet<String> findAllParents(String commit) {
        HashSet<String> res = new HashSet<String>();
        Commit theCommit = commitCollection.get(commit);
        Stack<Commit> s = new Stack<Commit>();
        s.push(theCommit);
        while (!s.empty()) {
            Commit first = s.pop();
            res.add(first.toSha1());
            for (String par : first.getParents()) {
                if (!res.contains(par) && (!par.equals(""))) {
                    s.push(commitCollection.get(par));
                }
            }
        }
        return res;
    }

    /**
     * Execute add-remote command.
     * @param remoteName Remote name to add.
     * @param remoteDir Remote directory.
     */
    public void addRemote(String remoteName, String remoteDir) {
        if (_remotes.containsKey(remoteName)) {
            System.err.println("A remote with that name already exists.");
            System.exit(0);
        }
        _remotes.put(remoteName, remoteDir);
    }

    /**
     * Execute rm-remote command.
     * @param remoteName Remote name to remove.
     */
    public void rmRemote(String remoteName) {
        if (!_remotes.containsKey(remoteName)) {
            System.err.println("A remote with that name does not exist.");
            System.exit(0);
        }
        _remotes.remove(remoteName);
    }

    /**
     * Execute push command.
     * @param remoteName Remote name to push to.
     * @param rmBranchName Remote branch name.
     */
    public void push(String remoteName,
                     String rmBranchName) throws IOException {
        String remoteGitlet = _remotes.get(remoteName);
        File newFile = new File(remoteGitlet);
        if (!(newFile.exists())) {
            System.err.println("Remote directory not found.");
            System.exit(0);
        }
        String remoteAddress = remoteGitlet + "/repo";
        File remoteGitRepo = new File(remoteAddress);
        Repo remoteRepo = Utils.readObject(remoteGitRepo, Repo.class);

        Commit rmBranchHead = remoteRepo
            .branchCollection.get(rmBranchName).getHead();
        HashSet<String> allParents = findAllParents(head.toSha1());
        if (!allParents.contains(rmBranchHead.toSha1())) {
            System.err.println("Please pull down "
                + "remote changes before pushing.");
            System.exit(0);
        } else {
            Commit p = head;
            while (!p.toSha1().equals(rmBranchHead.toSha1())) {
                remoteRepo.commitCollection.put(p.toSha1(), p);
                Collection<String> pTracking = p.getTrackedFiles().values();
                for (String fileSha1 : pTracking) {
                    File curr =
                        new File("./.gitlet/.staging/" + fileSha1);
                    String fileStream = Utils.readContentsAsString(curr);
                    File targetToStage =
                        new File(remoteGitlet + "/.staging/" + fileSha1);
                    if (!targetToStage.exists()) {
                        targetToStage.createNewFile();
                    }
                    Utils.writeContents(targetToStage, fileStream);
                }
                p = commitCollection.get(p.getParent1());
            }
            if (!remoteRepo.branchCollection.containsKey(rmBranchName)) {
                Branch newBranch =
                    new Branch(rmBranchName,
                        remoteRepo.currBranch().getHead());
                remoteRepo.branchCollection.put(rmBranchName, newBranch);
            }

            Commit t = remoteRepo.
                commitCollection.get(head.toSha1());
            remoteRepo.branchCollection.
                get(rmBranchName).changeHeadTo(t);
            remoteRepo.head = remoteRepo.
                branchCollection.get(rmBranchName).getHead();
            Utils.writeObject(remoteGitRepo, remoteRepo);
        }
    }

    /**
     * Execute fetch command.
     * @param remoteName Remote name to fetch from.
     * @param rmBranchName Remote branch name to fetch from.
     */
    public void fetch(String remoteName,
                      String rmBranchName) throws IOException {
        String remoteGitlet = _remotes.get(remoteName);
        File newFile = new File(remoteGitlet);
        if (!(newFile.exists())) {
            System.err.println("Remote directory not found.");
            System.exit(0);
        }
        String remoteAddress = remoteGitlet + "/repo";
        File remoteGitRepo = new File(remoteAddress);
        Repo remoteRepo = Utils.readObject(remoteGitRepo, Repo.class);
        if (!remoteRepo.branchCollection.containsKey(rmBranchName)) {
            System.out.println("That remote does not have that branch.");
            System.exit(0);
        }
        Commit rmBranchHead = remoteRepo
            .branchCollection.get(rmBranchName).getHead();
        Commit p = rmBranchHead;
        while (!commitCollection.containsKey(p.toSha1())) {
            this.commitCollection.put(p.toSha1(), p);
            Collection<String> pTracking = p.getTrackedFiles().values();
            for (String fileSha1 : pTracking) {
                File target =
                    new File(remoteGitlet + "/.staging/" + fileSha1);
                String fileStream = Utils.readContentsAsString(target);
                File curr =
                    new File("./.gitlet/.staging/" + fileSha1);
                if (!curr.exists()) {
                    curr.createNewFile();
                }
                Utils.writeContents(curr, fileStream);
            }
            p = remoteRepo.commitCollection.get(p.getParent1());
        }
        String newBranchName = remoteName + "/" + rmBranchName;
        Branch newBranch =
            new Branch(newBranchName,
                commitCollection.get(rmBranchHead.toSha1()));
        this.branchCollection.put(newBranchName, newBranch);
    }

    /**
     * Execute push command.
     * @param remoteName Remote name to pull from.
     * @param rmBranchName Remote branch name.
     */
    public void pull(String remoteName,
                     String rmBranchName) throws IOException {
        String newBranchName = remoteName + "/" + rmBranchName;
        fetch(remoteName, rmBranchName);
        merge(newBranchName);
    }
}
