package paxos;

import java.io.Serializable;

/**
 * Please fill in the data structure you use to represent the request message for each RMI call.
 * Hint: You may need the sequence number for each paxos instance and also you may need proposal number and value.
 * Hint: Make it more generic such that you can use it for each RMI call.
 * Hint: It is easier to make each variable public
 */
public class Request implements Serializable {
    static final long serialVersionUID = 1L;
    // Your data here
    int sequenceNum;
    int proposalNum;
    Object value;
    int[] peerDone;

    // Your constructor and methods here
    public Request(int sequenceNum, int proposalNum, Object value, int[] peerDone){
        this.sequenceNum = sequenceNum;
        this.proposalNum = proposalNum;
        this.value = value;
        this.peerDone = peerDone;
    }

    public Request(int sequenceNum, int proposalNum, int[] peerDone){
        this.sequenceNum = sequenceNum;
        this.proposalNum = proposalNum;
        this.peerDone = peerDone;
    }
}
