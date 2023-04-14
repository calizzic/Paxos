package paxos;

import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is the main class you need to implement paxos instances.
 * It corresponds to a single Paxos peer.
 */
public class Paxos implements PaxosRMI, Runnable {
    ReentrantLock mutex;
    String[] peers; // hostnames of all peers
    int[] ports; // ports of all peers
    int me; // this peer's index into peers[] and ports[]

    Registry registry;
    PaxosRMI stub;

    AtomicBoolean dead; // for testing
    AtomicBoolean unreliable; // for testing

    // Your data here
    Object value;
    int highestPrepare;
    int highestAccept;
    Object acceptedValue;
    int seq;
    boolean decided;
    int decidedSeq;

    /**
     * Call the constructor to create a Paxos peer.
     * The hostnames of all the Paxos peers (including this one)
     * are in peers[]. The ports are in ports[].
     */
    public Paxos(int me, String[] peers, int[] ports) {

        this.me = me;
        this.peers = peers;
        this.ports = ports;
        this.mutex = new ReentrantLock();
        this.dead = new AtomicBoolean(false);
        this.unreliable = new AtomicBoolean(false);

        // Your initialization code here
        value = null;
        highestPrepare = -1;
        highestAccept = -1;
        acceptedValue = -1;

        // register peers, do not modify this part
        try {
            System.setProperty("java.rmi.server.hostname", this.peers[this.me]);
            registry = LocateRegistry.createRegistry(this.ports[this.me]);
            stub = (PaxosRMI) UnicastRemoteObject.exportObject(this, this.ports[this.me]);
            registry.rebind("Paxos", stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Call() sends an RMI to the RMI handler on server with
     * arguments rmi name, request message, and server id. It
     * waits for the reply and return a response message if
     * the server responded, and return null if Call() was not
     * be able to contact the server.
     *
     * You should assume that Call() will time out and return
     * null after a while if it doesn't get a reply from the server.
     *
     * Please use Call() to send all RMIs and please don't change
     * this function.
     */
    public Response Call(String rmi, Request req, int id) {
        Response callReply = null;

        PaxosRMI stub;
        try {
            Registry registry = LocateRegistry.getRegistry(this.ports[id]);
            stub = (PaxosRMI) registry.lookup("Paxos");
            if (rmi.equals("Prepare"))
                callReply = stub.Prepare(req);
            else if (rmi.equals("Accept"))
                callReply = stub.Accept(req);
            else if (rmi.equals("Decide"))
                callReply = stub.Decide(req);
            else
                System.out.println("Wrong parameters!");
        } catch (Exception e) {
            return null;
        }
        return callReply;
    }

    /**
     * The application wants Paxos to start agreement on instance seq,
     * with proposed value v. Start() should start a new thread to run
     * Paxos on instance seq. Multiple instances can be run concurrently.
     *
     * Hint: You may start a thread using the runnable interface of
     * Paxos object. One Paxos object may have multiple instances, each
     * instance corresponds to one proposed value/command. Java does not
     * support passing arguments to a thread, so you may reset seq and v
     * in Paxos object before starting a new thread. There is one issue
     * that variable may change before the new thread actually reads it.
     * Test won't fail in this case.
     *
     * Start() just starts a new thread to initialize the agreement.
     * The application will call Status() to find out if/when agreement
     * is reached.
     */
    public void Start(int seq, Object value) {
        // Your code here - basically call this.run after initializing parameters
        this.value = value;
        this.seq = seq;
        Thread t = new Thread(this);
        t.start();
        return;
    }

    @Override
    public void run() {
        //Your code here
        /*
        PSEUDOCODE FROM CANVAS
        1: proposer(v):
        2: while not decided: do
        3: choose n, unique and higher than any n seen so far
        4: send prepare(n) to all servers including self
        5: if prepare ok(n, na, va) from majority then
        6: v′ = va with highest na; choose own v otherwise
        7: send accept(n, v′) to all
        8: if accept ok(n) from majority then
        9: send decided(v′) to all
        10: end if
        11: end if
        12: end while
         */
        //while(!.equals(decided)){
        //}
        int sequence = this.seq;
        Object value = this.value;
    }

    // RMI Handler for prepare requests
    public Response Prepare(Request req) {
        // your code here
        /*
        PSEUDOCODE FROM CANVAS
        18: acceptor’s prepare(n) handler:
        19: if n > np then
        20: np = n
        21: reply prepare ok(n, na, va)
        22: else
        23: reply prepare reject
        24: end if
        */
       if(req.sequenceNum > this.highestPrepare){
            this.highestPrepare = req.sequenceNum;
            Call("Prepare", )
       }
       
        return null;
    }

    // RMI Handler for accept requests
    public Response Accept(Request req) {
        // your code here
        /*
        PSEUDCODE FROM CANVAS
        26: acceptor’s accept(n, v) handler:
        27: if n >= np then
        28: np = n
        29: na = n
        30: va = v
        31: reply accept ok(n)
        32: else
        33: reply accept reject
        34: end if

         */
        return null;
    }

    // RMI Handler for decide requests
    public Response Decide(Request req) {
        // your code here
        return null;
    }

    /**
     * The application on this machine is done with
     * all instances <= seq.
     *
     * see the comments for Min() for more explanation.
     */
    public void Done(int seq) {
        // Your code here
    }

    /**
     * The application wants to know the
     * highest instance sequence known to
     * this peer.
     */
    public int Max() {
        // Your code here
        return -1;
    }

    /**
     * Min() should return one more than the minimum among z_i,
     * where z_i is the highest number ever passed
     * to Done() on peer i. A peers z_i is -1 if it has
     * never called Done().

     * Paxos is required to have forgotten all information
     * about any instances it knows that are < Min().
     * The point is to free up memory in long-running
     * Paxos-based servers.

     * Paxos peers need to exchange their highest Done()
     * arguments in order to implement Min(). These
     * exchanges can be piggybacked on ordinary Paxos
     * agreement protocol messages, so it is OK if one
     * peers Min does not reflect another Peers Done()
     * until after the next instance is agreed to.

     * The fact that Min() is defined as a minimum over
     * all Paxos peers means that Min() cannot increase until
     * all peers have been heard from. So if a peer is dead
     * or unreachable, other peers Min()s will not increase
     * even if all reachable peers call Done. The reason for
     * this is that when the unreachable peer comes back to
     * life, it will need to catch up on instances that it
     * missed -- the other peers therefore cannot forget these
     * instances.
     */
    public int Min() {
        // Your code here
        return -1;
    }

    /**
     * The application wants to know whether this
     * peer thinks an instance has been decided,
     * and if so what the agreed value is. Status()
     * should just inspect the local peer state;
     * it should not contact other Paxos peers.
     */
    public retStatus Status(int seq) {
        // Your code here
        /*
        PSEUDOCODE FROM CANVAS
        14: acceptor’s state:
        15: np (highest prepare seen)
        16: na, va (highest accept seen)
         */
        return null;
    }

    /**
     * helper class for Status() return
     */
    public class retStatus {
        public State state;
        public Object v;

        public retStatus(State state, Object v) {
            this.state = state;
            this.v = v;
        }
    }

    /**
     * Tell the peer to shut itself down.
     * For testing.
     * Please don't change these four functions.
     */
    public void Kill() {
        this.dead.getAndSet(true);
        if (this.registry != null) {
            try {
                UnicastRemoteObject.unexportObject(this.registry, true);
            } catch (Exception e) {
                System.out.println("None reference");
            }
        }
    }

    public boolean isDead() {
        return this.dead.get();
    }

    public void setUnreliable() {
        this.unreliable.getAndSet(true);
    }

    public boolean isunreliable() {
        return this.unreliable.get();
    }
}
