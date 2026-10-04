import java.util.*;

/**
 * PhysicalMemory - Manages physical memory frames using paging with LRU replacement
 */
class PhysicalMemory {
    private static PhysicalMemory instance;
    private final int TOTAL_MEMORY_KB = 1024;  // 1 MB total memory
    private int PAGE_SIZE_KB = 4;               // 4 KB per page/frame (configurable)
    private int TOTAL_FRAMES;
    
    private boolean[] frameTable;               // true = occupied, false = free
    private int[] frameOwner;                   // Process ID that owns each frame
    private Queue<Integer> freeFrames;          // Queue of free frame numbers
    private LRUPageReplacement lruReplacement;  // LRU algorithm for page replacement
    private int lastVictimProcessID;            // Track last victim process ID for LRU
    
    // Statistics
    private int pageFaults;
    private int totalAllocations;
    private int totalDeallocations;
    private int lruReplacements;
    
    private PhysicalMemory() {
        // Read page size from config file
        readPageSizeFromFile();
        
        TOTAL_FRAMES = TOTAL_MEMORY_KB / PAGE_SIZE_KB;  // Calculate frames based on page size
        frameTable = new boolean[TOTAL_FRAMES];
        frameOwner = new int[TOTAL_FRAMES];
        freeFrames = new LinkedList<>();
        lruReplacement = new LRUPageReplacement();
        lastVictimProcessID = -1;
        
        // Initialize all frames as free
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            frameTable[i] = false;
            frameOwner[i] = -1;
            freeFrames.offer(i);
        }
        
        pageFaults = 0;
        totalAllocations = 0;
        totalDeallocations = 0;
        lruReplacements = 0;
    }
    
    public static PhysicalMemory getInstance() {
        if (instance == null) {
            instance = new PhysicalMemory();
        }
        return instance;
    }
    
    /**
     * Allocate a frame for a process
     * @param processID The process requesting the frame
     * @return Frame number, or -1 if no frames available
     */
    public synchronized int allocateFrame(int processID) {
        if (freeFrames.isEmpty()) {
            pageFaults++;
            return -1;  // No free frames available
        }
        
        int frameNumber = freeFrames.poll();
        frameTable[frameNumber] = true;
        frameOwner[frameNumber] = processID;
        lruReplacement.accessFrame(frameNumber);  // Track access for LRU
        totalAllocations++;
        
        return frameNumber;
    }
    
    /**
     * Deallocate a specific frame
     */
    public synchronized void deallocateFrame(int frameNumber) {
        if (frameNumber >= 0 && frameNumber < TOTAL_FRAMES && frameTable[frameNumber]) {
            frameTable[frameNumber] = false;
            frameOwner[frameNumber] = -1;
            
            // Add freed frame back in sorted order to maintain frame numbering
            List<Integer> tempList = new ArrayList<>(freeFrames);
            tempList.add(frameNumber);
            Collections.sort(tempList);
            freeFrames.clear();
            freeFrames.addAll(tempList);
            
            lruReplacement.removeFrame(frameNumber);
            totalDeallocations++;
        }
    }
    
    /**
     * Deallocate all frames owned by a process
     */
    public synchronized void deallocateProcess(int processID) {
        int framesFreed = 0;
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            if (frameTable[i] && frameOwner[i] == processID) {
                frameTable[i] = false;
                frameOwner[i] = -1;
                
                // Add freed frame back in sorted order
                List<Integer> tempList = new ArrayList<>(freeFrames);
                tempList.add(i);
                Collections.sort(tempList);
                freeFrames.clear();
                freeFrames.addAll(tempList);
                
                lruReplacement.removeFrame(i);
                framesFreed++;
            }
        }
        totalDeallocations += framesFreed;
    }
    
    /**
     * LRU Page Replacement - Replace least recently used frame
     * @param newProcessID The process that needs a frame
     * @return Frame number that was replaced, or -1 if replacement failed
     */
    public synchronized int replacePageLRU(int newProcessID) {
        // Get all allocated frames
        List<Integer> allocatedFrames = new ArrayList<>();
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            if (frameTable[i]) {
                allocatedFrames.add(i);
            }
        }
        
        if (allocatedFrames.isEmpty()) {
            return -1;
        }
        
        // Find LRU frame using LRU algorithm
        int victimFrame = lruReplacement.findLRUFrame(allocatedFrames);
        
        if (victimFrame == -1) {
            return -1;
        }
        
        // Get the victim process ID BEFORE replacing
        lastVictimProcessID = frameOwner[victimFrame];
        
        // Replace the victim frame
        frameOwner[victimFrame] = newProcessID;
        lruReplacement.accessFrame(victimFrame);
        pageFaults++;
        lruReplacements++;
        
        return victimFrame;
    }
    
    /**
     * Access a frame (updates LRU tracking)
     */
    public synchronized void accessFrame(int frameNumber) {
        if (frameNumber >= 0 && frameNumber < TOTAL_FRAMES && frameTable[frameNumber]) {
            lruReplacement.accessFrame(frameNumber);
        }
    }
    
    // Getters for statistics and info
    public int getTotalFrames() { return TOTAL_FRAMES; }
    public int getPageSizeKB() { return PAGE_SIZE_KB; }
    public int getTotalMemoryKB() { return TOTAL_MEMORY_KB; }
    public int getFreeFrameCount() { return freeFrames.size(); }
    public int getUsedFrameCount() { return TOTAL_FRAMES - freeFrames.size(); }
    public double getMemoryUtilization() { 
        return (getUsedFrameCount() * 100.0) / TOTAL_FRAMES; 
    }
    public int getPageFaults() { return pageFaults; }
    public int getTotalAllocations() { return totalAllocations; }
    public int getTotalDeallocations() { return totalDeallocations; }
    public int getLRUReplacements() { return lruReplacements; }
    public boolean[] getFrameTable() { return frameTable.clone(); }
    public int[] getFrameOwner() { return frameOwner.clone(); }
    public int getLastVictimProcessID() { return lastVictimProcessID; }
    
    /**
     * Get frames owned by a specific process
     */
    public List<Integer> getProcessFrames(int processID) {
        List<Integer> frames = new ArrayList<>();
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            if (frameTable[i] && frameOwner[i] == processID) {
                frames.add(i);
            }
        }
        return frames;
    }
    
    /**
     * Get page replacement algorithm name
     */
    public String getReplacementAlgorithm() {
        return "LRU (Least Recently Used)";
    }
    
    /**
     * Reset memory system
     */
    public synchronized void reset() {
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            frameTable[i] = false;
            frameOwner[i] = -1;
        }
        freeFrames.clear();
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            freeFrames.offer(i);
        }
        lruReplacement.reset();
        lastVictimProcessID = -1;
        pageFaults = 0;
        totalAllocations = 0;
        totalDeallocations = 0;
        lruReplacements = 0;
    }
    
    /**
     * Read page size from configuration file
     */
    private void readPageSizeFromFile() {
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.FileReader("config.txt")
            );
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                int size = Integer.parseInt(line.trim());
                if (size > 0 && size <= TOTAL_MEMORY_KB) {
                    PAGE_SIZE_KB = size;
                }
            }
            reader.close();
        } catch (Exception e) {
            // If file doesn't exist or error, use default 4 KB
            PAGE_SIZE_KB = 4;
        }
    }
    
    /**
     * Set new page size and save to configuration file
     * WARNING: This will reset the entire memory system!
     */
    public synchronized boolean setPageSize(int newPageSize) {
        if (newPageSize <= 0 || newPageSize > TOTAL_MEMORY_KB) {
            return false;
        }
        
        // Must be a divisor of total memory for clean division
        if (TOTAL_MEMORY_KB % newPageSize != 0) {
            return false;
        }
        
        // Save to file
        try {
            java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.FileWriter("config.txt")
            );
            writer.write(String.valueOf(newPageSize));
            writer.close();
            
            // Update page size
            PAGE_SIZE_KB = newPageSize;
            
            // Recalculate frames
            int newTotalFrames = TOTAL_MEMORY_KB / PAGE_SIZE_KB;
            
            // Reset memory system with new frame count
            frameTable = new boolean[newTotalFrames];
            frameOwner = new int[newTotalFrames];
            freeFrames.clear();
            
            for (int i = 0; i < newTotalFrames; i++) {
                frameTable[i] = false;
                frameOwner[i] = -1;
                freeFrames.offer(i);
            }
            
            TOTAL_FRAMES = newTotalFrames;
            
            // Reset statistics
            pageFaults = 0;
            totalAllocations = 0;
            totalDeallocations = 0;
            lruReplacements = 0;
            lastVictimProcessID = -1;
            lruReplacement.reset();
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
}

/**
 * PageTable - Maps virtual pages to physical frames for a process
 */
class PageTable {
    private int processID;
    private Map<Integer, Integer> pageToFrame;  // Virtual Page → Physical Frame
    private int totalPages;
    
    public PageTable(int processID, int memoryRequiredKB) {
        this.processID = processID;
        this.pageToFrame = new HashMap<>();
        this.totalPages = (int) Math.ceil(memoryRequiredKB / (double) PhysicalMemory.getInstance().getPageSizeKB());
    }
    
    /**
     * Add a page-to-frame mapping
     */
    public void addMapping(int virtualPage, int physicalFrame) {
        pageToFrame.put(virtualPage, physicalFrame);
    }
    
    /**
     * Get physical frame for a virtual page
     */
    public Integer getFrame(int virtualPage) {
        return pageToFrame.get(virtualPage);
    }
    
    /**
     * Remove a page mapping
     */
    public void removePage(int virtualPage) {
        pageToFrame.remove(virtualPage);
    }
    
    /**
     * Get all virtual pages
     */
    public Set<Integer> getVirtualPages() {
        return pageToFrame.keySet();
    }
    
    public int getTotalPages() { return totalPages; }
    public int getAllocatedPages() { return pageToFrame.size(); }
    public int getProcessID() { return processID; }
    public Map<Integer, Integer> getPageMappings() { return new HashMap<>(pageToFrame); }
    
    /**
     * Clear all mappings
     */
    public void clear() {
        pageToFrame.clear();
    }
}

/**
 * MemoryManager - Coordinates memory allocation for processes
 */
class MemoryManager {
    private static MemoryManager instance;
    private PhysicalMemory physicalMemory;
    private Map<Integer, PageTable> processTables;  // ProcessID → PageTable
    
    private MemoryManager() {
        physicalMemory = PhysicalMemory.getInstance();
        processTables = new HashMap<>();
    }
    
    public static MemoryManager getInstance() {
        if (instance == null) {
            instance = new MemoryManager();
        }
        return instance;
    }
    
    /**
     * Allocate memory for a process
     * @return true if successful, false if insufficient memory
     */
    public boolean allocateMemory(int processID, int memoryRequiredKB) {
        if (memoryRequiredKB <= 0) {
            return false;
        }
        
        PageTable pageTable = new PageTable(processID, memoryRequiredKB);
        int pagesNeeded = pageTable.getTotalPages();
        
        // Try to allocate frames
        List<Integer> allocatedFrames = new ArrayList<>();
        
        for (int page = 0; page < pagesNeeded; page++) {
            int frame = physicalMemory.allocateFrame(processID);
            
            if (frame == -1) {
                // Try LRU page replacement
                frame = physicalMemory.replacePageLRU(processID);
                
                if (frame == -1) {
                    // Still no space - rollback and fail
                    for (int allocFrame : allocatedFrames) {
                        physicalMemory.deallocateFrame(allocFrame);
                    }
                    return false;
                }
                
                // CRITICAL FIX: Remove this frame from victim process's page table
                int victimProcessID = physicalMemory.getLastVictimProcessID();
                if (victimProcessID != processID && victimProcessID != -1 && processTables.containsKey(victimProcessID)) {
                    PageTable victimPageTable = processTables.get(victimProcessID);
                    // Find and remove the mapping for this frame
                    Map<Integer, Integer> mappings = victimPageTable.getPageMappings();
                    for (Map.Entry<Integer, Integer> entry : mappings.entrySet()) {
                        if (entry.getValue() == frame) {
                            victimPageTable.removePage(entry.getKey());
                            break;
                        }
                    }
                }
            }
            
            allocatedFrames.add(frame);
            pageTable.addMapping(page, frame);
        }
        
        processTables.put(processID, pageTable);
        return true;
    }
    
    /**
     * Deallocate memory for a process
     */
    public void deallocateMemory(int processID) {
        PageTable pageTable = processTables.get(processID);
        if (pageTable == null) {
            return;
        }
        
        // Free all frames
        for (int frame : pageTable.getPageMappings().values()) {
            physicalMemory.deallocateFrame(frame);
        }
        
        pageTable.clear();
        processTables.remove(processID);
    }
    
    /**
     * Get page table for a process
     */
    public PageTable getPageTable(int processID) {
        return processTables.get(processID);
    }
    
    /**
     * Check if process has memory allocated
     */
    public boolean hasMemory(int processID) {
        return processTables.containsKey(processID);
    }
    
    /**
     * Get memory statistics for a process
     */
    public String getProcessMemoryInfo(int processID) {
        PageTable pageTable = processTables.get(processID);
        if (pageTable == null) {
            return "No memory allocated";
        }
        
        int allocated = pageTable.getAllocatedPages();
        int total = pageTable.getTotalPages();
        int memoryKB = allocated * physicalMemory.getPageSizeKB();
        
        return String.format("%d/%d pages (%d KB)", allocated, total, memoryKB);
    }
    
    public PhysicalMemory getPhysicalMemory() { return physicalMemory; }
    public Map<Integer, PageTable> getProcessTables() { return new HashMap<>(processTables); }
}