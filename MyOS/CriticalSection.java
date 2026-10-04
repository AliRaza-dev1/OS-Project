import java.util.*;

/**
 * CriticalSection - Manages a critical section with mutual exclusion
 */
class CriticalSection {
    private String name;
    private BinarySemaphore mutex;
    private int entryCount;
    private List<CriticalSectionAccess> accessHistory;
    
    public CriticalSection(String name) {
        this.name = name;
        this.mutex = new BinarySemaphore("CS_" + name);
        this.entryCount = 0;
        this.accessHistory = new ArrayList<>();
    }
    
    /**
     * Enter critical section
     */
    public boolean enter(int processID) {
        boolean entered = mutex.acquire(processID);
        
        if (entered) {
            entryCount++;
            CriticalSectionAccess access = new CriticalSectionAccess(
                processID, System.currentTimeMillis(), true
            );
            accessHistory.add(access);
        }
        
        return entered;
    }
    
    /**
     * Exit critical section
     */
    public Integer exit() {
        Integer wokenPID = mutex.release();
        
        if (wokenPID != null) {
            // A waiting process was woken up
            CriticalSectionAccess access = new CriticalSectionAccess(
                wokenPID, System.currentTimeMillis(), true
            );
            accessHistory.add(access);
        }
        
        return wokenPID;
    }
    
    /**
     * Check if process is in critical section
     */
    public boolean isProcessInside(int processID) {
        return mutex.getOwnerPID() == processID;
    }
    
    /**
     * Check if critical section is occupied
     */
    public boolean isOccupied() {
        return mutex.isLocked();
    }
    
    // Getters
    public String getName() { return name; }
    public int getEntryCount() { return entryCount; }
    public int getCurrentOwner() { return mutex.getOwnerPID(); }
    public List<Integer> getWaitingProcesses() { 
        return mutex.getWaitingProcesses(); 
    }
    public List<CriticalSectionAccess> getAccessHistory() { 
        return new ArrayList<>(accessHistory); 
    }
    
    @Override
    public String toString() {
        return String.format("CriticalSection[%s] Occupied: %s, Waiting: %d, Total Entries: %d",
            name, isOccupied() ? "Yes (PID " + getCurrentOwner() + ")" : "No", 
            mutex.getWaitingCount(), entryCount);
    }
}

/**
 * Record of a critical section access
 */
class CriticalSectionAccess {
    private int processID;
    private long timestamp;
    private boolean entered;
    
    public CriticalSectionAccess(int processID, long timestamp, boolean entered) {
        this.processID = processID;
        this.timestamp = timestamp;
        this.entered = entered;
    }
    
    public int getProcessID() { return processID; }
    public long getTimestamp() { return timestamp; }
    public boolean isEntered() { return entered; }
}