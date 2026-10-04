import java.util.*;

class ProcessControlBlock {
    private int processID;
    private String processState;
    private String owner;
    private int priority;
    private ProcessControlBlock parentProcess;
    private ArrayList<ProcessControlBlock> childProcesses;
    private int memoryRequirements;
    private String allocatedMemoryPointer;
    private String registerSaveArea;
    private int processorRunningOn;
    private String ioStateInfo;
    private String processName;
    private int burstTime;
    private int remainingTime;
    private int arrivalTime;          // Simulation time (seconds)
    private int startTime;            // Simulation time when first dispatched
    private int completionTime;       // Simulation time when terminated
    private int waitingTime;          // Total waiting time in seconds
    private boolean hasStarted;
    
    public ProcessControlBlock(int id, String name, int burst) {
        this.processID = id;
        this.processName = name;
        this.burstTime = burst;
        this.remainingTime = burst;
        this.processState = "NEW";
        this.owner = "System";
        this.priority = 5;
        this.parentProcess = null;
        this.childProcesses = new ArrayList<>();
        this.memoryRequirements = 0;
        this.allocatedMemoryPointer = "NULL";
        this.registerSaveArea = "Empty";
        this.processorRunningOn = 0;
        this.ioStateInfo = "None";
        this.arrivalTime = 0;  // Will be set by scheduler
        this.startTime = 0;
        this.completionTime = 0;
        this.waitingTime = 0;
        this.hasStarted = false;
    }
    
    // Getters
    public int getProcessID() { return processID; }
    public String getProcessName() { return processName; }
    public String getProcessState() { return processState; }
    public String getOwner() { return owner; }
    public int getPriority() { return priority; }
    public ProcessControlBlock getParentProcess() { return parentProcess; }
    public ArrayList<ProcessControlBlock> getChildProcesses() { return childProcesses; }
    public int getMemoryRequirements() { return memoryRequirements; }
    public String getAllocatedMemoryPointer() { return allocatedMemoryPointer; }
    public String getRegisterSaveArea() { return registerSaveArea; }
    public int getProcessorRunningOn() { return processorRunningOn; }
    public String getIoStateInfo() { return ioStateInfo; }
    public int getBurstTime() { return burstTime; }
    public int getRemainingTime() { return remainingTime; }
    public int getArrivalTime() { return arrivalTime; }
    public int getStartTime() { return startTime; }
    public int getCompletionTime() { return completionTime; }
    public int getWaitingTime() { return waitingTime; }
    public boolean hasStarted() { return hasStarted; }
    
    // Calculated statistics
    public int getTurnaroundTime() {
        if (completionTime == 0) return 0;
        return completionTime - arrivalTime;
    }
    
    public int getResponseTime() {
        if (startTime == 0) return 0;
        return startTime - arrivalTime;
    }
    
    // Setters
    public void setProcessState(String state) { this.processState = state; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setParentProcess(ProcessControlBlock parent) { this.parentProcess = parent; }
    public void addChildProcess(ProcessControlBlock child) { this.childProcesses.add(child); }
    public void setRemainingTime(int time) { this.remainingTime = time; }
    public void decrementRemainingTime() { this.remainingTime--; }
    public void setMemoryRequirements(int memory) { this.memoryRequirements = memory; }
    public void setArrivalTime(int time) { this.arrivalTime = time; }
    public void setStartTime(int time) { 
        if (!hasStarted) {
            this.startTime = time;
            this.hasStarted = true;
        }
    }
    public void setCompletionTime(int time) { this.completionTime = time; }
    public void setWaitingTime(int time) { this.waitingTime = time; }
}

class ProcessManager {
    private static ProcessManager instance;
    private ArrayList<ProcessControlBlock> allProcesses;
    private int nextProcessID;
    
    private ProcessManager() {
        allProcesses = new ArrayList<>();
        nextProcessID = 1000;
    }
    
    public static ProcessManager getInstance() {
        if (instance == null) {
            instance = new ProcessManager();
        }
        return instance;
    }
    
    public ProcessControlBlock createProcess(String name, int burstTime, int memoryKB) {
        ProcessControlBlock block = new ProcessControlBlock(nextProcessID++, name, burstTime);
        block.setMemoryRequirements(memoryKB);
        allProcesses.add(block);
        return block;
    }
    
    public ArrayList<ProcessControlBlock> getAllProcesses() {
        return allProcesses;
    }
    
    public ProcessControlBlock getProcessByID(int id) {
        for (ProcessControlBlock pcb : allProcesses) {
            if (pcb.getProcessID() == id) {
                return pcb;
            }
        }
        return null;
    }
    
    public ArrayList<ProcessControlBlock> getTerminatedProcesses() {
        ArrayList<ProcessControlBlock> terminated = new ArrayList<>();
        for (ProcessControlBlock pcb : allProcesses) {
            if (pcb.getProcessState().equals("TERMINATED")) {
                terminated.add(pcb);
            }
        }
        return terminated;
    }
}

class Scheduler {
    private static Scheduler instance;
    private ArrayList<ProcessControlBlock> readyQueue;
    private ArrayList<ProcessControlBlock> blockedQueue;
    private ArrayList<ProcessControlBlock> suspendedQueue;
    private ProcessControlBlock runningProcess;
    private String algorithm;
    private Timer schedulerTimer;
    private boolean isRunning;
    private int timeQuantum;
    private int currentQuantumUsed;
    private int simulationTime;       // Current simulation time in seconds
    private int totalCpuBusyTime;     // Total time CPU was busy
    private int totalProcessesCompleted;
    
    private Scheduler() {
        readyQueue = new ArrayList<>();
        blockedQueue = new ArrayList<>();
        suspendedQueue = new ArrayList<>();
        runningProcess = null;
        algorithm = "FCFS";
        isRunning = false;
        timeQuantum = 2;
        currentQuantumUsed = 0;
        simulationTime = 0;
        totalCpuBusyTime = 0;
        totalProcessesCompleted = 0;
    }
    
    public static Scheduler getInstance() {
        if (instance == null) {
            instance = new Scheduler();
        }
        return instance;
    }
    
    public void setAlgorithm(String algo) {
        this.algorithm = algo;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public void setTimeQuantum(int quantum) {
        this.timeQuantum = quantum;
    }
    
    public int getTimeQuantum() {
        return timeQuantum;
    }
    
    public void admitProcess(ProcessControlBlock pcb) {
        pcb.setArrivalTime(simulationTime);
        pcb.setProcessState("READY");
        readyQueue.add(pcb);
    }
    
    public ProcessControlBlock dispatch() {
        if (runningProcess != null) {
            throw new IllegalStateException("Cannot dispatch: A process is already running (PID: " + 
                runningProcess.getProcessID() + "). Wait for it to complete or block it first.");
        }
        
        if (readyQueue.isEmpty()) {
            return null;
        }
        
        ProcessControlBlock pcb = readyQueue.remove(0);
        pcb.setProcessState("RUNNING");
        runningProcess = pcb;
        currentQuantumUsed = 0;
        
        // Track start time and waiting time
        if (!pcb.hasStarted()) {
            pcb.setStartTime(simulationTime);
            int waitTime = simulationTime - pcb.getArrivalTime();
            pcb.setWaitingTime(waitTime);
        }
        
        return pcb;
    }
    
    public void blockProcess(ProcessControlBlock pcb) {
        String currentState = pcb.getProcessState();
        
        if (!currentState.equals("RUNNING")) {
            throw new IllegalStateException("Cannot block process in " + currentState + 
                " state. Only RUNNING processes can be blocked.");
        }
        
        runningProcess = null;
        pcb.setProcessState("BLOCKED");
        blockedQueue.add(pcb);
    }
    
    public void wakeupProcess(ProcessControlBlock pcb) {
        String currentState = pcb.getProcessState();
        
        if (!currentState.equals("BLOCKED")) {
            throw new IllegalStateException("Cannot wakeup process in " + currentState + 
                " state. Only BLOCKED processes can be woken up.");
        }
        
        blockedQueue.remove(pcb);
        pcb.setProcessState("READY");
        readyQueue.add(pcb);
    }
    
    public void suspendProcess(ProcessControlBlock pcb) {
        String currentState = pcb.getProcessState();
        
        if (!currentState.equals("READY") && !currentState.equals("BLOCKED")) {
            throw new IllegalStateException("Cannot suspend process in " + currentState + 
                " state. Can only suspend READY or BLOCKED processes.");
        }
        
        if (currentState.equals("READY")) {
            readyQueue.remove(pcb);
        } else if (currentState.equals("BLOCKED")) {
            blockedQueue.remove(pcb);
        }
        
        pcb.setProcessState("SUSPENDED");
        suspendedQueue.add(pcb);
    }
    
    public void resumeProcess(ProcessControlBlock pcb) {
        String currentState = pcb.getProcessState();
        
        if (!currentState.equals("SUSPENDED")) {
            throw new IllegalStateException("Cannot resume process in " + currentState + 
                " state. Only SUSPENDED processes can be resumed.");
        }
        
        suspendedQueue.remove(pcb);
        pcb.setProcessState("READY");
        readyQueue.add(pcb);
    }
    
    public void terminateProcess(ProcessControlBlock pcb) {
        String currentState = pcb.getProcessState();
        
        if (currentState.equals("TERMINATED")) {
            throw new IllegalStateException("Process is already terminated.");
        }
        
        if (currentState.equals("RUNNING")) {
            runningProcess = null;
        } else if (currentState.equals("READY")) {
            readyQueue.remove(pcb);
        } else if (currentState.equals("BLOCKED")) {
            blockedQueue.remove(pcb);
        } else if (currentState.equals("SUSPENDED")) {
            suspendedQueue.remove(pcb);
        }
        
        MemoryManager.getInstance().deallocateMemory(pcb.getProcessID());
        pcb.setCompletionTime(simulationTime);
        pcb.setProcessState("TERMINATED");
        totalProcessesCompleted++;
    }
    
    public void startScheduling() {
        if (isRunning) {
            return;
        }
        
        isRunning = true;
        simulationTime = 0;
        totalCpuBusyTime = 0;
        totalProcessesCompleted = 0;
        
        schedulerTimer = new Timer();
        schedulerTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                simulationTime++; // Increment simulation time
                
                if (runningProcess != null) {
                    runningProcess.decrementRemainingTime();
                    currentQuantumUsed++;
                    totalCpuBusyTime++;
                    
                    // Check if process is done
                    if (runningProcess.getRemainingTime() <= 0) {
                        terminateProcess(runningProcess);
                        runningProcess = null;
                        currentQuantumUsed = 0;
                    }
                    // Round Robin: Check if time quantum expired
                    else if (algorithm.equals("RoundRobin") && currentQuantumUsed >= timeQuantum) {
                        ProcessControlBlock preempted = runningProcess;
                        preempted.setProcessState("READY");
                        readyQueue.add(preempted);
                        runningProcess = null;
                        currentQuantumUsed = 0;
                    }
                }
                
                // Dispatch next process if CPU is idle
                if (runningProcess == null && !readyQueue.isEmpty()) {
                    try {
                        dispatch();
                    } catch (IllegalStateException ex) {
                        // Silently handle dispatch errors
                    }
                }
            }
        }, 0, 1000);
    }
    
    public void stopScheduling() {
        if (schedulerTimer != null) {
            schedulerTimer.cancel();
            isRunning = false;
        }
    }
    
    // Statistics calculation methods
    public double getCpuUtilization() {
        if (simulationTime == 0) return 0.0;
        return (totalCpuBusyTime * 100.0) / simulationTime;
    }
    
    public double getThroughput() {
        if (simulationTime == 0) return 0.0;
        return totalProcessesCompleted / (double) simulationTime;
    }
    
    public double getAverageTurnaroundTime() {
        ArrayList<ProcessControlBlock> terminated = ProcessManager.getInstance().getTerminatedProcesses();
        if (terminated.isEmpty()) return 0.0;
        
        int total = 0;
        for (ProcessControlBlock pcb : terminated) {
            total += pcb.getTurnaroundTime();
        }
        return total / (double) terminated.size();
    }
    
    public double getAverageWaitingTime() {
        ArrayList<ProcessControlBlock> terminated = ProcessManager.getInstance().getTerminatedProcesses();
        if (terminated.isEmpty()) return 0.0;
        
        int total = 0;
        for (ProcessControlBlock pcb : terminated) {
            total += pcb.getWaitingTime();
        }
        return total / (double) terminated.size();
    }
    
    // Getters
    public ArrayList<ProcessControlBlock> getReadyQueue() { return readyQueue; }
    public ArrayList<ProcessControlBlock> getBlockedQueue() { return blockedQueue; }
    public ArrayList<ProcessControlBlock> getSuspendedQueue() { return suspendedQueue; }
    public ProcessControlBlock getRunningProcess() { return runningProcess; }
    public int getSimulationTime() { return simulationTime; }
}