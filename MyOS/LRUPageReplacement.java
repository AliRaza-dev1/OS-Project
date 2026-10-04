import java.util.*;

/**
 * LRU (Least Recently Used) Page Replacement Implementation
 * Tracks access times for each frame and replaces the least recently used one
 */
class LRUPageReplacement {
    private Map<Integer, Long> frameAccessTime;  // Frame number -> Last access timestamp
    private long currentTime;
    
    public LRUPageReplacement() {
        frameAccessTime = new HashMap<>();
        currentTime = 0;
    }
    
    /**
     * Record that a frame was accessed
     */
    public void accessFrame(int frameNumber) {
        frameAccessTime.put(frameNumber, currentTime++);
    }
    
    /**
     * Find the least recently used frame from a list of candidates
     * @param frames List of frame numbers to consider
     * @return Frame number of LRU frame, or -1 if no frames
     */
    public int findLRUFrame(List<Integer> frames) {
        if (frames == null || frames.isEmpty()) {
            return -1;
        }
        
        int lruFrame = frames.get(0);
        long oldestTime = frameAccessTime.getOrDefault(lruFrame, 0L);
        
        for (int frame : frames) {
            long accessTime = frameAccessTime.getOrDefault(frame, 0L);
            if (accessTime < oldestTime) {
                oldestTime = accessTime;
                lruFrame = frame;
            }
        }
        
        return lruFrame;
    }
    
    /**
     * Remove frame from tracking when deallocated
     */
    public void removeFrame(int frameNumber) {
        frameAccessTime.remove(frameNumber);
    }
    
    /**
     * Clear all tracking data
     */
    public void reset() {
        frameAccessTime.clear();
        currentTime = 0;
    }
    
    /**
     * Get access time for a frame (for debugging)
     */
    public long getAccessTime(int frameNumber) {
        return frameAccessTime.getOrDefault(frameNumber, -1L);
    }
    
    /**
     * Get all tracked frames
     */
    public Set<Integer> getTrackedFrames() {
        return new HashSet<>(frameAccessTime.keySet());
    }
}