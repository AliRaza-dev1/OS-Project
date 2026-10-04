import java.util.*;

/**
 * Semaphore - Classic counting semaphore for process synchronization
 */
class Semaphore {
    private int value;
    private final int maxValue;
    private Queue<Integer> waitingQueue;  // PIDs of waiting processes
    private String name;
    
    public Semaphore(String name, int initialValue) {
        this.name = name;
        this.value = initialValue;
        this.maxValue = initialValue;
        this.waitingQueue = new LinkedList<>();
    }
    
    /**
     * Wait/P operation - Acquire semaphore
     */
    public synchronized boolean acquire(int processID) {
        if (value > 0) {
            value--;
            return true;  // Successfully acquired
        } else {
            waitingQueue.offer(processID);
            return false;  // Process must wait
        }
    }
    
    /**
     * Signal/V operation - Release semaphore
     */
    public synchronized Integer release() {
        value++;
        if (value > maxValue) {
            value = maxValue;  // Cap at max value
        }
        
        // Wake up a waiting process if any
        if (!waitingQueue.isEmpty()) {
            value--;  // Immediately give to waiting process
            return waitingQueue.poll();
        }
        return null;
    }
    
    /**
     * Force release by a specific process
     */
    public synchronized Integer releaseBy(int processID) {
        // Remove from waiting queue if present
        waitingQueue.remove(processID);
        return release();
    }
    
    /**
     * Check if process is waiting
     */
    public boolean isWaiting(int processID) {
        return waitingQueue.contains(processID);
    }
    
    // Getters
    public String getName() { return name; }
    public int getValue() { return value; }
    public int getMaxValue() { return maxValue; }
    public int getWaitingCount() { return waitingQueue.size(); }
    public List<Integer> getWaitingProcesses() { 
        return new ArrayList<>(waitingQueue); 
    }
    
    @Override
    public String toString() {
        return String.format("Semaphore[%s] Value: %d/%d, Waiting: %d", 
            name, value, maxValue, waitingQueue.size());
    }
}

/**
 * BinarySemaphore - Mutex for mutual exclusion (value 0 or 1)
 */
class BinarySemaphore extends Semaphore {
    private int ownerPID;  // Process currently holding the lock
    
    public BinarySemaphore(String name) {
        super(name, 1);  // Binary semaphore initialized to 1
        this.ownerPID = -1;
    }
    
    @Override
    public synchronized boolean acquire(int processID) {
        boolean acquired = super.acquire(processID);
        if (acquired) {
            ownerPID = processID;
        }
        return acquired;
    }
    
    @Override
    public synchronized Integer release() {
        ownerPID = -1;
        return super.release();
    }
    
    public int getOwnerPID() { return ownerPID; }
    
    public boolean isLocked() { return getValue() == 0; }
}