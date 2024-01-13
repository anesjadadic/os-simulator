package os;

public class FreePartition {
    private int startLocation;  // Starting location in memory
    private int size;           // Size of the free partition
    private FreePartition next; // Reference to the next free partition

    // Constructor
    public FreePartition(int startLocation, int size) {
        this.startLocation = startLocation;
        this.size = size;
        this.next = null; // Initially, there's no next partition
    }

    // Getters and setters for the fields

    public int getStartLocation() {
        return startLocation;
    }

    public int getSize() {
        return size;
    }

    public FreePartition getNext() {
        return next;
    }

    public void setNext(FreePartition next) {
        this.next = next;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
}

