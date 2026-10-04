import java.util.*;

/**
 * Resource - Represents a system resource
 */
class Resource {
    private String name;
    private int totalInstances;
    private int availableInstances;
    private Map<Integer, Integer> allocation;  // PID → count allocated
    private Semaphore semaphore;
    
    public Resource(String name, int instances) {
        this.name = name;
        this.totalInstances = instances;
        this.availableInstances = instances;
        this.allocation = new HashMap<>();
        this.semaphore = new Semaphore(name, instances);
    }
    
    /**
     * Request resource instances
     */
    public synchronized boolean request(int processID, int count) {
        if (count <= 0 || count > availableInstances) {
            return false;
        }
        
        // Try to acquire semaphore
        boolean acquired = true;
        for (int i = 0; i < count; i++) {
            if (!semaphore.acquire(processID)) {
                acquired = false;
                // Rollback
                for (int j = 0; j < i; j++) {
                    semaphore.release();
                }
                break;
            }
        }
        
        if (acquired) {
            availableInstances -= count;
            allocation.put(processID, allocation.getOrDefault(processID, 0) + count);
        }
        
        return acquired;
    }
    
    /**
     * Release resource instances
     */
    public synchronized void release(int processID, int count) {
        int allocated = allocation.getOrDefault(processID, 0);
        
        if (allocated < count) {
            count = allocated;  // Can't release more than allocated
        }
        
        availableInstances += count;
        allocation.put(processID, allocated - count);
        
        if (allocation.get(processID) == 0) {
            allocation.remove(processID);
        }
        
        // Release semaphore
        for (int i = 0; i < count; i++) {
            semaphore.release();
        }
    }
    
    /**
     * Release all resources held by a process
     */
    public synchronized void releaseAll(int processID) {
        int allocated = allocation.getOrDefault(processID, 0);
        if (allocated > 0) {
            release(processID, allocated);
        }
    }
    
    // Getters
    public String getName() { return name; }
    public int getTotalInstances() { return totalInstances; }
    public int getAvailableInstances() { return availableInstances; }
    public int getAllocated(int processID) { 
        return allocation.getOrDefault(processID, 0); 
    }
    public Map<Integer, Integer> getAllocations() { 
        return new HashMap<>(allocation); 
    }
    public double getUtilization() {
        return ((totalInstances - availableInstances) * 100.0) / totalInstances;
    }
    
    @Override
    public String toString() {
        return String.format("Resource[%s] Total: %d, Available: %d, Used: %d (%.1f%%)",
            name, totalInstances, availableInstances, 
            totalInstances - availableInstances, getUtilization());
    }
}

/**
 * ResourceManager - Manages all system resources and synchronization
 */
class ResourceManager {
    private static ResourceManager instance;
    private Map<String, Resource> resources;
    private Map<String, CriticalSection> criticalSections;
    private Map<String, Semaphore> semaphores;
    private List<ResourceAllocationEvent> allocationLog;
    
    private ResourceManager() {
        resources = new HashMap<>();
        criticalSections = new HashMap<>();
        semaphores = new HashMap<>();
        allocationLog = new ArrayList<>();
        
        // Initialize default resources
        initializeDefaultResources();
    }
    
    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }
    
    /**
     * Initialize default system resources
     */
    private void initializeDefaultResources() {
        addResource("Printer", 2);
        addResource("Scanner", 1);
        addResource("Disk", 3);
        addResource("Network", 5);
        
        addCriticalSection("FileSystem");
        addCriticalSection("Database");
        
        addSemaphore("IOController", 3);
    }
    
    // ========== RESOURCE MANAGEMENT ==========
    
    public void addResource(String name, int instances) {
        resources.put(name, new Resource(name, instances));
    }
    
    public boolean requestResource(int processID, String resourceName, int count) {
        Resource resource = resources.get(resourceName);
        if (resource == null) {
            return false;
        }
        
        boolean success = resource.request(processID, count);
        
        // Log the allocation
        logAllocation(processID, resourceName, count, success);
        
        return success;
    }
    
    public void releaseResource(int processID, String resourceName, int count) {
        Resource resource = resources.get(resourceName);
        if (resource != null) {
            resource.release(processID, count);
            logRelease(processID, resourceName, count);
        }
    }
    
    public void releaseAllResources(int processID) {
        for (Resource resource : resources.values()) {
            resource.releaseAll(processID);
        }
    }
    
    // ========== CRITICAL SECTION MANAGEMENT ==========
    
    public void addCriticalSection(String name) {
        criticalSections.put(name, new CriticalSection(name));
    }
    
    public boolean enterCriticalSection(int processID, String csName) {
        CriticalSection cs = criticalSections.get(csName);
        if (cs == null) {
            return false;
        }
        return cs.enter(processID);
    }
    
    public Integer exitCriticalSection(String csName) {
        CriticalSection cs = criticalSections.get(csName);
        if (cs == null) {
            return null;
        }
        return cs.exit();
    }
    
    // ========== SEMAPHORE MANAGEMENT ==========
    
    public void addSemaphore(String name, int initialValue) {
        semaphores.put(name, new Semaphore(name, initialValue));
    }
    
    public boolean acquireSemaphore(int processID, String semName) {
        Semaphore sem = semaphores.get(semName);
        if (sem == null) {
            return false;
        }
        return sem.acquire(processID);
    }
    
    public Integer releaseSemaphore(String semName) {
        Semaphore sem = semaphores.get(semName);
        if (sem == null) {
            return null;
        }
        return sem.release();
    }
    
    // ========== LOGGING ==========
    
    private void logAllocation(int processID, String resourceName, int count, boolean success) {
        allocationLog.add(new ResourceAllocationEvent(
            processID, resourceName, count, success, true, System.currentTimeMillis()
        ));
    }
    
    private void logRelease(int processID, String resourceName, int count) {
        allocationLog.add(new ResourceAllocationEvent(
            processID, resourceName, count, true, false, System.currentTimeMillis()
        ));
    }
    
    // ========== GETTERS ==========
    
    public Map<String, Resource> getResources() { return new HashMap<>(resources); }
    public Map<String, CriticalSection> getCriticalSections() { 
        return new HashMap<>(criticalSections); 
    }
    public Map<String, Semaphore> getSemaphores() { return new HashMap<>(semaphores); }
    public List<ResourceAllocationEvent> getAllocationLog() { 
        return new ArrayList<>(allocationLog); 
    }
    
    public Resource getResource(String name) { return resources.get(name); }
    public CriticalSection getCriticalSection(String name) { 
        return criticalSections.get(name); 
    }
    public Semaphore getSemaphore(String name) { return semaphores.get(name); }
    
    /**
     * Get all resources held by a process
     */
    public Map<String, Integer> getProcessResources(int processID) {
        Map<String, Integer> held = new HashMap<>();
        for (Map.Entry<String, Resource> entry : resources.entrySet()) {
            int count = entry.getValue().getAllocated(processID);
            if (count > 0) {
                held.put(entry.getKey(), count);
            }
        }
        return held;
    }
    
    /**
     * Reset all resources and synchronization primitives
     */
    public void reset() {
        resources.clear();
        criticalSections.clear();
        semaphores.clear();
        allocationLog.clear();
        initializeDefaultResources();
    }
}

/**
 * ResourceAllocationEvent - Log entry for resource allocation/release
 */
class ResourceAllocationEvent {
    private int processID;
    private String resourceName;
    private int count;
    private boolean success;
    private boolean isAllocation;  // true = allocation, false = release
    private long timestamp;
    
    public ResourceAllocationEvent(int processID, String resourceName, int count, 
                                   boolean success, boolean isAllocation, long timestamp) {
        this.processID = processID;
        this.resourceName = resourceName;
        this.count = count;
        this.success = success;
        this.isAllocation = isAllocation;
        this.timestamp = timestamp;
    }
    
    public int getProcessID() { return processID; }
    public String getResourceName() { return resourceName; }
    public int getCount() { return count; }
    public boolean isSuccess() { return success; }
    public boolean isAllocation() { return isAllocation; }
    public long getTimestamp() { return timestamp; }
}