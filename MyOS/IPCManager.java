import java.util.*;

/**
 * IPCManager - Manages all Inter-Process Communication mechanisms
 * Implements: Message Passing, Shared Memory, Sockets, RMI
 */
class IPCManager {
    private static IPCManager instance;
    
    // IPC Mechanisms
    private MessagePassingSystem messageSystem;
    private SharedMemorySystem sharedMemorySystem;
    private SocketSystem socketSystem;
    private RMISystem rmiSystem;
    
    private IPCManager() {
        messageSystem = new MessagePassingSystem();
        sharedMemorySystem = new SharedMemorySystem();
        socketSystem = new SocketSystem();
        rmiSystem = new RMISystem();
    }
    
    public static IPCManager getInstance() {
        if (instance == null) {
            instance = new IPCManager();
        }
        return instance;
    }
    
    // Getters for subsystems
    public MessagePassingSystem getMessageSystem() { return messageSystem; }
    public SharedMemorySystem getSharedMemorySystem() { return sharedMemorySystem; }
    public SocketSystem getSocketSystem() { return socketSystem; }
    public RMISystem getRMISystem() { return rmiSystem; }
    
    /**
     * Reset all IPC systems
     */
    public void reset() {
        messageSystem = new MessagePassingSystem();
        sharedMemorySystem = new SharedMemorySystem();
        socketSystem = new SocketSystem();
        rmiSystem = new RMISystem();
    }
}

// ==================== MESSAGE PASSING ====================

/**
 * Message - Represents an IPC message
 */
class Message {
    private int senderPID;
    private int receiverPID;
    private String content;
    private long timestamp;
    private int messageID;
    private String priority;  // "HIGH", "NORMAL", "LOW"
    
    public Message(int sender, int receiver, String content, String priority) {
        this.senderPID = sender;
        this.receiverPID = receiver;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.messageID = (int)(Math.random() * 10000);
        this.priority = priority;
    }
    
    // Getters
    public int getSenderPID() { return senderPID; }
    public int getReceiverPID() { return receiverPID; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public int getMessageID() { return messageID; }
    public String getPriority() { return priority; }
    
    @Override
    public String toString() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        return String.format("[%s] Msg#%d From PID %d → PID %d [%s]: %s",
            sdf.format(new Date(timestamp)), messageID, senderPID, receiverPID, 
            priority, content);
    }
}

/**
 * MessagePassingSystem - Direct & Indirect communication
 */
class MessagePassingSystem {
    private Map<Integer, Queue<Message>> mailboxes;  // PID → message queue
    private List<Message> messageLog;  // All messages sent
    
    public MessagePassingSystem() {
        mailboxes = new HashMap<>();
        messageLog = new ArrayList<>();
    }
    
    /**
     * Send message from one process to another
     */
    public synchronized boolean sendMessage(int senderPID, int receiverPID, 
                                           String content, String priority) {
        // Create mailbox if doesn't exist
        if (!mailboxes.containsKey(receiverPID)) {
            mailboxes.put(receiverPID, new LinkedList<>());
        }
        
        Message msg = new Message(senderPID, receiverPID, content, priority);
        mailboxes.get(receiverPID).offer(msg);
        messageLog.add(msg);
        
        return true;
    }
    
    /**
     * Receive message for a process
     */
    public synchronized Message receiveMessage(int receiverPID) {
        Queue<Message> mailbox = mailboxes.get(receiverPID);
        if (mailbox == null || mailbox.isEmpty()) {
            return null;
        }
        return mailbox.poll();
    }
    
    /**
     * Check messages without removing (peek)
     */
    public synchronized List<Message> checkMessages(int processID) {
        Queue<Message> mailbox = mailboxes.get(processID);
        if (mailbox == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(mailbox);
    }
    
    /**
     * Get message count for a process
     */
    public int getMessageCount(int processID) {
        Queue<Message> mailbox = mailboxes.get(processID);
        return mailbox == null ? 0 : mailbox.size();
    }
    
    /**
     * Broadcast message to all processes
     */
    public synchronized void broadcastMessage(int senderPID, String content, String priority) {
        for (Integer receiverPID : mailboxes.keySet()) {
            if (receiverPID != senderPID) {
                sendMessage(senderPID, receiverPID, content, priority);
            }
        }
    }
    
    /**
     * Clear mailbox for a process
     */
    public synchronized void clearMailbox(int processID) {
        mailboxes.remove(processID);
    }
    
    // Getters
    public List<Message> getMessageLog() { return new ArrayList<>(messageLog); }
    public Map<Integer, Queue<Message>> getMailboxes() { return mailboxes; }
}

// ==================== SHARED MEMORY ====================

/**
 * SharedMemorySegment - A shared memory region
 */
class SharedMemorySegment {
    private String name;
    private int segmentID;
    private String data;
    private Set<Integer> attachedProcesses;
    private int sizeKB;
    private long lastModified;
    private int modifiedByPID;
    
    public SharedMemorySegment(String name, int segmentID, int sizeKB) {
        this.name = name;
        this.segmentID = segmentID;
        this.sizeKB = sizeKB;
        this.data = "";
        this.attachedProcesses = new HashSet<>();
        this.lastModified = System.currentTimeMillis();
        this.modifiedByPID = -1;
    }
    
    /**
     * Attach a process to this segment
     */
    public synchronized boolean attach(int processID) {
        return attachedProcesses.add(processID);
    }
    
    /**
     * Detach a process from this segment
     */
    public synchronized boolean detach(int processID) {
        return attachedProcesses.remove(processID);
    }
    
    /**
     * Write data to shared memory
     */
    public synchronized boolean write(int processID, String newData) {
        if (!attachedProcesses.contains(processID)) {
            return false;
        }
        this.data = newData;
        this.lastModified = System.currentTimeMillis();
        this.modifiedByPID = processID;
        return true;
    }
    
    /**
     * Read data from shared memory
     */
    public synchronized String read(int processID) {
        if (!attachedProcesses.contains(processID)) {
            return null;
        }
        return data;
    }
    
    // Getters
    public String getName() { return name; }
    public int getSegmentID() { return segmentID; }
    public int getSizeKB() { return sizeKB; }
    public Set<Integer> getAttachedProcesses() { return new HashSet<>(attachedProcesses); }
    public int getAttachedCount() { return attachedProcesses.size(); }
    public long getLastModified() { return lastModified; }
    public int getModifiedByPID() { return modifiedByPID; }
    public String getData() { return data; }
    
    @Override
    public String toString() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        return String.format("SharedMemory[%s] ID:%d Size:%dKB Attached:%d Modified:[%s by PID %d]",
            name, segmentID, sizeKB, attachedProcesses.size(),
            sdf.format(new Date(lastModified)), modifiedByPID);
    }
}

/**
 * SharedMemorySystem - Manages shared memory segments
 */
class SharedMemorySystem {
    private Map<Integer, SharedMemorySegment> segments;
    private int nextSegmentID;
    
    public SharedMemorySystem() {
        segments = new HashMap<>();
        nextSegmentID = 1000;
    }
    
    /**
     * Create a new shared memory segment
     */
    public synchronized int createSegment(String name, int sizeKB) {
        int segmentID = nextSegmentID++;
        SharedMemorySegment segment = new SharedMemorySegment(name, segmentID, sizeKB);
        segments.put(segmentID, segment);
        return segmentID;
    }
    
    /**
     * Attach process to segment
     */
    public synchronized boolean attachToSegment(int processID, int segmentID) {
        SharedMemorySegment segment = segments.get(segmentID);
        if (segment == null) {
            return false;
        }
        return segment.attach(processID);
    }
    
    /**
     * Detach process from segment
     */
    public synchronized boolean detachFromSegment(int processID, int segmentID) {
        SharedMemorySegment segment = segments.get(segmentID);
        if (segment == null) {
            return false;
        }
        return segment.detach(processID);
    }
    
    /**
     * Write to shared memory
     */
    public synchronized boolean writeToSegment(int processID, int segmentID, String data) {
        SharedMemorySegment segment = segments.get(segmentID);
        if (segment == null) {
            return false;
        }
        return segment.write(processID, data);
    }
    
    /**
     * Read from shared memory
     */
    public synchronized String readFromSegment(int processID, int segmentID) {
        SharedMemorySegment segment = segments.get(segmentID);
        if (segment == null) {
            return null;
        }
        return segment.read(processID);
    }
    
    /**
     * Delete a segment
     */
    public synchronized boolean deleteSegment(int segmentID) {
        return segments.remove(segmentID) != null;
    }
    
    // Getters
    public Map<Integer, SharedMemorySegment> getSegments() { 
        return new HashMap<>(segments); 
    }
    
    public SharedMemorySegment getSegment(int segmentID) {
        return segments.get(segmentID);
    }
}

// ==================== SOCKETS (SIMULATED) ====================

/**
 * SocketConnection - Simulated socket connection
 */
class SocketConnection {
    private int connectionID;
    private int serverPID;
    private int clientPID;
    private int port;
    private boolean isActive;
    private List<String> dataExchangeLog;
    private long establishedTime;
    
    public SocketConnection(int connectionID, int serverPID, int clientPID, int port) {
        this.connectionID = connectionID;
        this.serverPID = serverPID;
        this.clientPID = clientPID;
        this.port = port;
        this.isActive = true;
        this.dataExchangeLog = new ArrayList<>();
        this.establishedTime = System.currentTimeMillis();
    }
    
    /**
     * Send data through socket
     */
    public synchronized boolean sendData(int senderPID, String data) {
        if (!isActive) {
            return false;
        }
        
        String direction = (senderPID == clientPID) ? "Client→Server" : "Server→Client";
        String logEntry = String.format("[%s] %s: %s", 
            new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()), 
            direction, data);
        dataExchangeLog.add(logEntry);
        return true;
    }
    
    /**
     * Close connection
     */
    public synchronized void close() {
        isActive = false;
    }
    
    // Getters
    public int getConnectionID() { return connectionID; }
    public int getServerPID() { return serverPID; }
    public int getClientPID() { return clientPID; }
    public int getPort() { return port; }
    public boolean isActive() { return isActive; }
    public List<String> getDataExchangeLog() { return new ArrayList<>(dataExchangeLog); }
    public long getEstablishedTime() { return establishedTime; }
    
    @Override
    public String toString() {
        return String.format("Socket[ID:%d] Port:%d Server:PID%d ↔ Client:PID%d Status:%s Exchanges:%d",
            connectionID, port, serverPID, clientPID, 
            isActive ? "ACTIVE" : "CLOSED", dataExchangeLog.size());
    }
}

/**
 * SocketSystem - Manages socket connections
 */
class SocketSystem {
    private Map<Integer, SocketConnection> connections;
    private Map<Integer, Integer> serverPorts;  // Port → Server PID
    private int nextConnectionID;
    
    public SocketSystem() {
        connections = new HashMap<>();
        serverPorts = new HashMap<>();
        nextConnectionID = 5000;
    }
    
    /**
     * Bind a server to a port
     */
    public synchronized boolean bindServer(int serverPID, int port) {
        if (serverPorts.containsKey(port)) {
            return false;  // Port already in use
        }
        serverPorts.put(port, serverPID);
        return true;
    }
    
    /**
     * Connect client to server
     */
    public synchronized Integer connectToServer(int clientPID, int port) {
        Integer serverPID = serverPorts.get(port);
        if (serverPID == null) {
            return null;  // No server on this port
        }
        
        int connectionID = nextConnectionID++;
        SocketConnection conn = new SocketConnection(connectionID, serverPID, clientPID, port);
        connections.put(connectionID, conn);
        return connectionID;
    }
    
    /**
     * Send data through connection
     */
    public synchronized boolean sendData(int connectionID, int senderPID, String data) {
        SocketConnection conn = connections.get(connectionID);
        if (conn == null) {
            return false;
        }
        return conn.sendData(senderPID, data);
    }
    
    /**
     * Close connection
     */
    public synchronized boolean closeConnection(int connectionID) {
        SocketConnection conn = connections.get(connectionID);
        if (conn == null) {
            return false;
        }
        conn.close();
        return true;
    }
    
    /**
     * Unbind server from port
     */
    public synchronized boolean unbindServer(int port) {
        return serverPorts.remove(port) != null;
    }
    
    // Getters
    public Map<Integer, SocketConnection> getConnections() { 
        return new HashMap<>(connections); 
    }
    
    public SocketConnection getConnection(int connectionID) {
        return connections.get(connectionID);
    }
    
    public Map<Integer, Integer> getServerPorts() {
        return new HashMap<>(serverPorts);
    }
}

// ==================== RMI (SIMULATED) ====================

/**
 * RemoteMethod - Represents a remote method that can be invoked
 */
class RemoteMethod {
    private String methodName;
    private int ownerPID;
    private String returnType;
    private List<String> parameterTypes;
   
    
    public RemoteMethod(String methodName, int ownerPID, String returnType, List<String> paramTypes) {
        this.methodName = methodName;
        this.ownerPID = ownerPID;
        this.returnType = returnType;
        this.parameterTypes = paramTypes != null ? new ArrayList<>(paramTypes) : new ArrayList<>();
      
    }
    
    // Getters
    public String getMethodName() { return methodName; }
    public int getOwnerPID() { return ownerPID; }
    public String getReturnType() { return returnType; }
    public List<String> getParameterTypes() { return new ArrayList<>(parameterTypes); }
    
    @Override
    public String toString() {
        return String.format("%s %s(%s) @ PID %d",
            returnType, methodName, String.join(", ", parameterTypes), ownerPID);
    }
}

/**
 * RMIInvocation - Log of an RMI call
 */
class RMIInvocation {
    private int callerPID;
    private int targetPID;
    private String methodName;
    private List<String> parameters;
    private String result;
    private long timestamp;
    private boolean success;
    
    public RMIInvocation(int callerPID, int targetPID, String methodName, 
                        List<String> parameters, String result, boolean success) {
        this.callerPID = callerPID;
        this.targetPID = targetPID;
        this.methodName = methodName;
        this.parameters = parameters != null ? new ArrayList<>(parameters) : new ArrayList<>();
        this.result = result;
        this.timestamp = System.currentTimeMillis();
        this.success = success;
    }
    
    // Getters
    public int getCallerPID() { return callerPID; }
    public int getTargetPID() { return targetPID; }
    public String getMethodName() { return methodName; }
    public List<String> getParameters() { return new ArrayList<>(parameters); }
    public String getResult() { return result; }
    public long getTimestamp() { return timestamp; }
    public boolean isSuccess() { return success; }
    
    @Override
    public String toString() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        String status = success ? "✓" : "✗";
        return String.format("[%s] %s PID %d → PID %d: %s(%s) = %s",
            sdf.format(new Date(timestamp)), status, callerPID, targetPID,
            methodName, String.join(", ", parameters), result);
    }
}

/**
 * RMISystem - Remote Method Invocation system
 */
class RMISystem {
    private Map<Integer, Map<String, RemoteMethod>> remoteMethods;  // PID → Methods
    private List<RMIInvocation> invocationLog;
    
    public RMISystem() {
        remoteMethods = new HashMap<>();
        invocationLog = new ArrayList<>();
    }
    
    /**
     * Register a remote method for a process
     */
    public synchronized boolean registerMethod(int processID, String methodName, 
                                              String returnType, List<String> paramTypes) {
        if (!remoteMethods.containsKey(processID)) {
            remoteMethods.put(processID, new HashMap<>());
        }
        
        RemoteMethod method = new RemoteMethod(methodName, processID, returnType, paramTypes);
        remoteMethods.get(processID).put(methodName, method);
        return true;
    }
    
    /**
     * Invoke a remote method (simulated)
     */
   public synchronized String invokeMethod(int callerPID, int targetPID, 
                                       String methodName, List<String> params) {
    Map<String, RemoteMethod> methods = remoteMethods.get(targetPID);
    
    if (methods == null || !methods.containsKey(methodName)) {
        RMIInvocation invocation = new RMIInvocation(callerPID, targetPID, 
            methodName, params, "ERROR: Method not found", false);
        invocationLog.add(invocation);
        return null;
    }
    
    // Execute method and calculate real result
    RemoteMethod method = methods.get(methodName);
    String result = executeMethod(method, params);  // ← CHANGED THIS LINE
    
    RMIInvocation invocation = new RMIInvocation(callerPID, targetPID, 
        methodName, params, result, true);
    invocationLog.add(invocation);
    
    return result;
}
/**
 * Execute the actual calculation based on method name
 */
private String executeMethod(RemoteMethod method, List<String> params) {
    String methodName = method.getMethodName().toLowerCase();
    
    try {
        // If method expects 2 integer parameters
        if (params != null && params.size() >= 2) {
            int a = Integer.parseInt(params.get(0).trim());
            int b = Integer.parseInt(params.get(1).trim());
            
            int result = 0;
            
            // Perform calculation based on method name
            if (methodName.contains("add") || methodName.contains("plus") || methodName.contains("sum")) {
                result = a + b;
            } else if (methodName.contains("subtract") || methodName.contains("minus") || methodName.contains("sub")) {
                result = a - b;
            } else if (methodName.contains("multiply") || methodName.contains("mult") || methodName.contains("times")) {
                result = a * b;
            } else if (methodName.contains("divide") || methodName.contains("div")) {
                if (b == 0) {
                    return "ERROR: Division by zero";
                }
                result = a / b;
            } else if (methodName.contains("mod") || methodName.contains("modulo") || methodName.contains("remainder")) {
                result = a % b;
            } else if (methodName.contains("power") || methodName.contains("pow")) {
                result = (int) Math.pow(a, b);
            } else if (methodName.contains("max") || methodName.contains("maximum")) {
                result = Math.max(a, b);
            } else if (methodName.contains("min") || methodName.contains("minimum")) {
                result = Math.min(a, b);
            } else {
                // Default: addition
                result = a + b;
            }
            
            return String.valueOf(result);
        } else {
            // Not enough parameters
            return String.format("Result<%s>", method.getReturnType());
        }
    } catch (NumberFormatException e) {
        return "ERROR: Invalid parameters (expected numbers)";
    }
}
    
    /**
     * Unregister all methods for a process
     */
    public synchronized void unregisterProcess(int processID) {
        remoteMethods.remove(processID);
    }
    
    /**
     * Get all methods for a process
     */
    public synchronized Map<String, RemoteMethod> getMethodsForProcess(int processID) {
        Map<String, RemoteMethod> methods = remoteMethods.get(processID);
        return methods != null ? new HashMap<>(methods) : new HashMap<>();
    }
    
    // Getters
    public List<RMIInvocation> getInvocationLog() { 
        return new ArrayList<>(invocationLog); 
    }
    
    public Map<Integer, Map<String, RemoteMethod>> getAllRemoteMethods() {
        return new HashMap<>(remoteMethods);
    }
}