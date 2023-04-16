package paxos;

public class SequenceState {
    int highestPrepare;
    int highestAccept; 
    Object acceptedValue;
    int sequence;
    State state;
    public SequenceState(int seq){
        highestPrepare = -1;
        highestAccept = -1;
        acceptedValue = null;
        state = State.Pending;
        this.sequence = seq;
    }
}
