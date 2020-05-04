package gitlet;

import java.io.File;
import java.io.IOException;


/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author Laiming Huang
 */
public class Main {
    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            System.err.println("Please enter a command.");
            System.exit(0);
        } else {
            processCommand(args);
        }
    }

    /**
     * Process first part of commands.
     * @param args The arguments.
     * @throws IOException
     */
    private static void processCommand(String[] args) throws IOException {
        String command = args[0];
        switch (command) {
        case "init":
            if (args.length != 1) {
                errOperand();
            }
            init();
            break;
        case "add":
            if (args.length != 2) {
                errOperand();
            }
            checkInitialization();
            add(args);
            break;
        case "commit":
            checkInitialization();
            commit(args);
            break;
        case "rm":
            checkInitialization();
            rm(args);
            break;
        case "log":
            checkInitialization();
            log(args);
            break;
        case "global-log":
            checkInitialization();
            glog(args);
            break;
        case "find":
            checkInitialization();
            find(args);
            break;
        case "status":
            checkInitialization();
            status(args);
            break;
        case "checkout":
            checkInitialization();
            checkout(args);
            break;
        case "branch":
            checkInitialization();
            branch(args);
            break;
        case "rm-branch":
            checkInitialization();
            rmBranch(args);
            break;
        default:
            otherCases(args);
        }
    }

    /**
     * Process the rest commands.
     * @param args The arguments.
     * @throws IOException
     */
    private static void otherCases(String[] args) throws IOException {
        String command = args[0];
        switch (command) {
        case "reset":
            checkInitialization();
            reset(args);
            break;
        case "merge":
            checkInitialization();
            merge(args);
            break;
        case "add-remote":
            checkInitialization();
            addRemote(args);
            break;
        case "rm-remote":
            checkInitialization();
            rmRemote(args);
            break;
        case "push":
            checkInitialization();
            push(args);
            break;
        case "fetch":
            checkInitialization();
            fetch(args);
            break;
        case "pull":
            checkInitialization();
            pull(args);
            break;
        default:
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
    }

    /**
     * Execute pull command.
     * @param args The arguments.
     * @throws IOException
     */
    private static void pull(String[] args) throws IOException {
        if (args.length != 3) {
            errOperand();
        } else {
            String remoteName = args[1];
            String rmBranchName = args[2];
            Repo curr = Repo.read();
            curr.pull(remoteName, rmBranchName);
            Repo.save(curr);
        }
    }

    /**
     * Execute fetch command.
     * @param args The arguments.
     * @throws IOException
     */
    private static void fetch(String[] args) throws IOException {
        if (args.length != 3) {
            errOperand();
        } else {
            String remoteName = args[1];
            String rmBranchName = args[2];
            Repo curr = Repo.read();
            curr.fetch(remoteName, rmBranchName);
            Repo.save(curr);
        }
    }

    /**
     * Execute push command.
     * @param args The arguments.
     * @throws IOException
     */
    private static void push(String[] args) throws IOException {
        if (args.length != 3) {
            errOperand();
        } else {
            String remoteName = args[1];
            String rmBranchName = args[2];
            Repo curr = Repo.read();
            curr.push(remoteName, rmBranchName);
            Repo.save(curr);
        }
    }

    /**
     * Execute add-remote command.
     * @param args The arguments.
     * @throws IOException
     */
    private static void addRemote(String[] args) {
        String remoteName = args[1];
        String remoteDir = args[2];
        Repo curr = Repo.read();
        curr.addRemote(remoteName, remoteDir);
        Repo.save(curr);
    }

    /**
     * Execute rm-remote command.
     * @param args The arguments.
     * @throws IOException
     */
    private static void rmRemote(String[] args) {
        if (args.length != 2) {
            errOperand();
        } else {
            String remoteName = args[1];
            Repo curr = Repo.read();
            curr.rmRemote(remoteName);
            Repo.save(curr);
        }
    }

    /**
     * Execute merge command.
     * @param args The arguments.
     * @throws IOException
     */
    private static void merge(String[] args) throws IOException {
        checkInitialization();
        if (args.length != 2) {
            errOperand();
        } else {
            String branch = args[1];
            Repo curr = Repo.read();
            curr.merge(branch);
            Repo.save(curr);
        }
    }

    /**
     * Execute reset command.
     * @param args The arguments.
     * @throws IOException
     */
    private static void reset(String[] args) {
        if (args.length != 2) {
            errOperand();
        }
        checkInitialization();
        Repo curr = Repo.read();
        String commit = args[1];
        curr.reset(commit);
        Repo.save(curr);
    }

    /**
     * Execute rm-branch command.
     * @param args The arguments.
     * @throws IOException
     */
    private static void rmBranch(String[] args) {
        if (args.length != 2) {
            errOperand();
        }
        checkInitialization();
        Repo curr = Repo.read();
        String branchName = args[1];
        curr.rmBranch(branchName);
        Repo.save(curr);
    }

    /**
     * Execute branch command.
     * @param args The arguments.
     * @throws IOException
     */
    private static void branch(String[] args) {
        if (args.length != 2) {
            errOperand();
        }
        checkInitialization();
        Repo curr = Repo.read();
        String branchName = args[1];
        curr.branch(branchName);
        Repo.save(curr);
    }

    /**
     * Execute checkout command.
     * @param args The arguments.
     * @throws IOException
     */
    private static void checkout(String[] args) {
        int type = args.length - 1;
        if (type == 2) {
            if (!args[1].equals("--")) {
                errOperand();
            } else {
                Repo curr = Repo.read();
                String fileName = args[2];
                curr.checkoutByName(fileName);
                Repo.save(curr);
            }
        } else if (type == 3) {
            if (!args[2].equals("--")) {
                errOperand();
            } else {
                Repo curr = Repo.read();
                String commit = args[1];
                String fileName = args[3];
                curr.checkoutLong(commit, fileName);
                Repo.save(curr);
            }
        } else if (type == 1) {
            Repo curr = Repo.read();
            String branch = args[1];
            curr.checkoutByBranch(branch);
            Repo.save(curr);
        } else {
            errOperand();
        }
    }

    /**
     * Check whether working in an initialized Gitlet directory.
     */
    private static void checkInitialization() {
        File gitlet = new File("./.gitlet");
        if (!gitlet.exists()) {
            System.err.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    /**
     * Execute status command.
     * @param args The arguments.
     */
    private static void status(String[] args) {
        checkInitialization();
        if (args.length != 1) {
            errOperand();
        } else {
            Repo curr = Repo.read();
            curr.status();
        }
    }

    /**
     * Print "Incorrect operands" error message.
     */
    public static void errOperand() {
        System.out.println("Incorrect operands.");
        System.exit(0);
    }

    /**
     * Execute find command.
     * @param args The arguments.
     */
    private static void find(String[] args) {
        checkInitialization();
        if (args.length != 2) {
            errOperand();
        } else {
            Repo curr = Repo.read();
            String query = args[1];
            curr.find(query);
        }
    }

    /**
     * Execute global-log command.
     * @param args The arguments.
     */
    private static void glog(String[] args) {
        checkInitialization();
        if (args.length != 1) {
            errOperand();
        } else {
            Repo curr = Repo.read();
            curr.globalLog();
        }
    }

    /**
     * Execute log command.
     * @param args The arguments.
     */
    private static void log(String[] args) {
        checkInitialization();
        if (args.length != 1) {
            errOperand();
        } else {
            Repo curr = Repo.read();
            curr.log();
        }
    }

    /**
     * Execute rm command.
     * @param args The arguments.
     */
    private static void rm(String[] args) {
        if (args.length != 2) {
            errOperand();
        }
        checkInitialization();
        Repo curr = Repo.read();
        String toRemove = args[1];
        curr.rm(toRemove);
        Repo.save(curr);
    }

    /**
     * Execute commit command.
     * @param args The arguments.
     */
    private static void commit(String[] args) throws IOException {
        checkInitialization();
        if (args.length > 2) {
            errOperand();
        } else if (args.length == 1 || args[1].equals("")) {
            System.err.println("Please enter a commit message.");
            System.exit(0);
        } else {
            Repo curr = Repo.read();
            String commitMessage = args[1];
            curr.commit(commitMessage);
            Repo.save(curr);
        }
    }

    /**
     * Execute add command.
     * @param args The arguments.
     */
    private static void add(String[] args) throws IOException {
        Repo curr = Repo.read();
        String fileName = args[1];
        curr.add(fileName);
        Repo.save(curr);
    }

    /**
     * Execute init command.
     */
    private static void init() {
        File gitlet = new File("./.gitlet");
        if (gitlet.exists()) {
            System.out.println("A Gitlet version-control system already "
                + "exists in the current directory.");
            System.exit(0);
        } else {
            gitlet.mkdir();
            Repo raw = new Repo();
            Repo.save(raw);
            return;
        }
    }
}
