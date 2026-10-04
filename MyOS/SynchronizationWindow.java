import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * SynchronizationWindow - GUI for Phase 4.1: Synchronization & Resource Sharing
 */
class SynchronizationWindow extends JFrame {
    private ResourceManager resourceManager;
    private ProcessManager processManager;
    
    public SynchronizationWindow() {
        resourceManager = ResourceManager.getInstance();
        processManager = ProcessManager.getInstance();
        
        setTitle("BETA OS - Synchronization & Resource Management");
        setSize(680, 640);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(250, 250, 250));
        
        JPanel banner = new JPanel();
        banner.setBackground(new Color(60, 120, 170));
        banner.setPreferredSize(new Dimension(680, 55));
        
        JLabel bannerText = new JLabel("Synchronization & Resource Management");
        bannerText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        bannerText.setForeground(Color.WHITE);
        banner.add(bannerText);
        
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new GridLayout(7, 2, 12, 12));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        actionsPanel.setBackground(new Color(250, 250, 250));
        
        JButton btnRequestResource = makeActionButton("Request Resource");
        JButton btnReleaseResource = makeActionButton("Release Resource");
        JButton btnViewResources = makeActionButton("View All Resources");
        JButton btnEnterCS = makeActionButton("Enter Critical Section");
        JButton btnExitCS = makeActionButton("Exit Critical Section");
        JButton btnViewCS = makeActionButton("View Critical Sections");
        JButton btnAcquireSem = makeActionButton("Acquire Semaphore");
        JButton btnReleaseSem = makeActionButton("Release Semaphore");
        JButton btnViewSem = makeActionButton("View Semaphores");
        JButton btnProcessResources = makeActionButton("Process Resources");
        JButton btnAllocationLog = makeActionButton("Allocation Log");
        JButton btnResetSync = makeActionButton("Reset System");
        
        btnResetSync.setBackground(new Color(200, 100, 100));
        btnResetSync.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnResetSync.setBackground(new Color(170, 70, 70));
            }
            public void mouseExited(MouseEvent evt) {
                btnResetSync.setBackground(new Color(200, 100, 100));
            }
        });
        
        btnRequestResource.addActionListener(e -> requestResource());
        btnReleaseResource.addActionListener(e -> releaseResource());
        btnViewResources.addActionListener(e -> viewAllResources());
        btnEnterCS.addActionListener(e -> enterCriticalSection());
        btnExitCS.addActionListener(e -> exitCriticalSection());
        btnViewCS.addActionListener(e -> viewCriticalSections());
        btnAcquireSem.addActionListener(e -> acquireSemaphore());
        btnReleaseSem.addActionListener(e -> releaseSemaphore());
        btnViewSem.addActionListener(e -> viewSemaphores());
        btnProcessResources.addActionListener(e -> viewProcessResources());
        btnAllocationLog.addActionListener(e -> viewAllocationLog());
        btnResetSync.addActionListener(e -> resetSynchronization());
        
        actionsPanel.add(btnRequestResource);
        actionsPanel.add(btnReleaseResource);
        actionsPanel.add(btnViewResources);
        actionsPanel.add(btnProcessResources);
        actionsPanel.add(btnEnterCS);
        actionsPanel.add(btnExitCS);
        actionsPanel.add(btnViewCS);
        actionsPanel.add(btnAcquireSem);
        actionsPanel.add(btnReleaseSem);
        actionsPanel.add(btnViewSem);
        actionsPanel.add(btnAllocationLog);
        actionsPanel.add(btnResetSync);
        actionsPanel.add(new JLabel());
        
        wrapper.add(banner, BorderLayout.NORTH);
        wrapper.add(actionsPanel, BorderLayout.CENTER);
        
        add(wrapper);
        setVisible(true);
    }
    
    private JButton makeActionButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(new Color(120, 190, 240));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(new Color(90, 140, 220));
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(new Color(120, 190, 240));
                btn.setForeground(Color.BLACK);
            }
        });
        
        return btn;
    }
    
    // ========== RESOURCE MANAGEMENT ACTIONS ==========
    
    private void requestResource() {
        ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
        
        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No processes available!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Select process
        String[] processOptions = new String[processes.size()];
        for (int i = 0; i < processes.size(); i++) {
            ProcessControlBlock pcb = processes.get(i);
            processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
        }
        
        String selectedProcess = (String) JOptionPane.showInputDialog(
            this, "Select process:", "Request Resource",
            JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]
        );
        
        if (selectedProcess == null) return;
        int pid = Integer.parseInt(selectedProcess.split(" - ")[0]);
        
        // Select resource
        Map<String, Resource> resources = resourceManager.getResources();
        String[] resourceOptions = resources.keySet().toArray(new String[0]);
        
        String selectedResource = (String) JOptionPane.showInputDialog(
            this, "Select resource:", "Request Resource",
            JOptionPane.QUESTION_MESSAGE, null, resourceOptions, resourceOptions[0]
        );
        
        if (selectedResource == null) return;
        
        Resource resource = resources.get(selectedResource);
        
        // Get count
        String countStr = JOptionPane.showInputDialog(this,
            String.format("Enter count (Available: %d):", resource.getAvailableInstances()),
            "Request Resource", JOptionPane.PLAIN_MESSAGE);
        
        if (countStr != null && !countStr.trim().isEmpty()) {
            try {
                int count = Integer.parseInt(countStr.trim());
                boolean success = resourceManager.requestResource(pid, selectedResource, count);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        String.format("Process %d acquired %d instances of %s", 
                            pid, count, selectedResource),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Resource request failed! Insufficient resources available.",
                        "Failed", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid count!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void releaseResource() {
        ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
        
        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No processes available!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Select process
        String[] processOptions = new String[processes.size()];
        for (int i = 0; i < processes.size(); i++) {
            ProcessControlBlock pcb = processes.get(i);
            processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
        }
        
        String selectedProcess = (String) JOptionPane.showInputDialog(
            this, "Select process:", "Release Resource",
            JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]
        );
        
        if (selectedProcess == null) return;
        int pid = Integer.parseInt(selectedProcess.split(" - ")[0]);
        
        // Get process resources
        Map<String, Integer> held = resourceManager.getProcessResources(pid);
        
        if (held.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Process holds no resources!",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Select resource to release
        String[] resourceOptions = held.keySet().toArray(new String[0]);
        String selectedResource = (String) JOptionPane.showInputDialog(
            this, "Select resource to release:", "Release Resource",
            JOptionPane.QUESTION_MESSAGE, null, resourceOptions, resourceOptions[0]
        );
        
        if (selectedResource == null) return;
        
        int heldCount = held.get(selectedResource);
        
        // Get count to release
        String countStr = JOptionPane.showInputDialog(this,
            String.format("Enter count to release (Held: %d):", heldCount),
            "Release Resource", JOptionPane.PLAIN_MESSAGE);
        
        if (countStr != null && !countStr.trim().isEmpty()) {
            try {
                int count = Integer.parseInt(countStr.trim());
                resourceManager.releaseResource(pid, selectedResource, count);
                
                JOptionPane.showMessageDialog(this,
                    String.format("Process %d released %d instances of %s", 
                        pid, count, selectedResource),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid count!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewAllResources() {
        Map<String, Resource> resources = resourceManager.getResources();
        
        StringBuilder info = new StringBuilder();
        info.append("═══════════════════════════════════════════════\n");
        info.append("          SYSTEM RESOURCES STATUS\n");
        info.append("═══════════════════════════════════════════════\n\n");
        
        for (Resource resource : resources.values()) {
            info.append(resource.toString()).append("\n");
            
            Map<Integer, Integer> allocations = resource.getAllocations();
            if (!allocations.isEmpty()) {
                info.append("  Allocated to:\n");
                for (Map.Entry<Integer, Integer> entry : allocations.entrySet()) {
                    ProcessControlBlock pcb = processManager.getProcessByID(entry.getKey());
                    String name = pcb != null ? pcb.getProcessName() : "Unknown";
                    info.append(String.format("    PID %d (%s): %d instances\n",
                        entry.getKey(), name, entry.getValue()));
                }
            }
            info.append("\n");
        }
        
        JTextArea textArea = new JTextArea(info.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "All Resources", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void viewProcessResources() {
        ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
        
        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No processes available!", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Select process
        String[] processOptions = new String[processes.size()];
        for (int i = 0; i < processes.size(); i++) {
            ProcessControlBlock pcb = processes.get(i);
            processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
        }
        
        String selected = (String) JOptionPane.showInputDialog(
            this, "Select process:", "View Process Resources",
            JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]
        );
        
        if (selected == null) return;
        int pid = Integer.parseInt(selected.split(" - ")[0]);
        
        ProcessControlBlock pcb = processManager.getProcessByID(pid);
        Map<String, Integer> held = resourceManager.getProcessResources(pid);
        
        StringBuilder info = new StringBuilder();
        info.append("═══════════════════════════════════════\n");
        info.append(String.format("Process: %s (PID %d)\n", pcb.getProcessName(), pid));
        info.append("═══════════════════════════════════════\n\n");
    if (held.isEmpty()) {
        info.append("No resources held by this process.\n");
    } else {
        info.append("Resources Held:\n");
        for (Map.Entry<String, Integer> entry : held.entrySet()) {
            info.append(String.format("  %s: %d instances\n", 
                entry.getKey(), entry.getValue()));
        }
    }
    
    JOptionPane.showMessageDialog(this, info.toString(),
        "Process Resources", JOptionPane.INFORMATION_MESSAGE);
}

// ========== CRITICAL SECTION ACTIONS ==========

private void enterCriticalSection() {
    ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
    
    if (processes.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "No processes available!", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Select process
    String[] processOptions = new String[processes.size()];
    for (int i = 0; i < processes.size(); i++) {
        ProcessControlBlock pcb = processes.get(i);
        processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
    }
    
    String selectedProcess = (String) JOptionPane.showInputDialog(
        this, "Select process:", "Enter Critical Section",
        JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]
    );
    
    if (selectedProcess == null) return;
    int pid = Integer.parseInt(selectedProcess.split(" - ")[0]);
    
    // Select critical section
    Map<String, CriticalSection> sections = resourceManager.getCriticalSections();
    String[] csOptions = sections.keySet().toArray(new String[0]);
    
    String selectedCS = (String) JOptionPane.showInputDialog(
        this, "Select critical section:", "Enter Critical Section",
        JOptionPane.QUESTION_MESSAGE, null, csOptions, csOptions[0]
    );
    
    if (selectedCS == null) return;
    
    boolean entered = resourceManager.enterCriticalSection(pid, selectedCS);
    
    if (entered) {
        JOptionPane.showMessageDialog(this,
            String.format("Process %d entered critical section '%s'", pid, selectedCS),
            "Success", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this,
            String.format("Critical section '%s' is occupied!\nProcess %d added to waiting queue.", 
                selectedCS, pid),
            "Waiting", JOptionPane.WARNING_MESSAGE);
    }
}

private void exitCriticalSection() {
    Map<String, CriticalSection> sections = resourceManager.getCriticalSections();
    
    // Find occupied critical sections
    java.util.List<String> occupiedCS = new ArrayList<>();
    for (Map.Entry<String, CriticalSection> entry : sections.entrySet()) {
        if (entry.getValue().isOccupied()) {
            occupiedCS.add(entry.getKey());
        }
    }
    
    if (occupiedCS.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "No critical sections are currently occupied!",
            "Info", JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    
    String[] csOptions = occupiedCS.toArray(new String[0]);
    String selectedCS = (String) JOptionPane.showInputDialog(
        this, "Select critical section to exit:", "Exit Critical Section",
        JOptionPane.QUESTION_MESSAGE, null, csOptions, csOptions[0]
    );
    
    if (selectedCS == null) return;
    
    CriticalSection cs = sections.get(selectedCS);
    int exitingPID = cs.getCurrentOwner();
    
    Integer wokenPID = resourceManager.exitCriticalSection(selectedCS);
    
    String message = String.format("Process %d exited critical section '%s'", 
        exitingPID, selectedCS);
    
    if (wokenPID != null) {
        ProcessControlBlock wokenPCB = processManager.getProcessByID(wokenPID);
        String wokenName = wokenPCB != null ? wokenPCB.getProcessName() : "Unknown";
        message += String.format("\nProcess %d (%s) entered the critical section.", 
            wokenPID, wokenName);
    }
    
    JOptionPane.showMessageDialog(this, message,
        "Success", JOptionPane.INFORMATION_MESSAGE);
}

private void viewCriticalSections() {
    Map<String, CriticalSection> sections = resourceManager.getCriticalSections();
    
    StringBuilder info = new StringBuilder();
    info.append("═══════════════════════════════════════════════\n");
    info.append("        CRITICAL SECTIONS STATUS\n");
    info.append("═══════════════════════════════════════════════\n\n");
    
    for (CriticalSection cs : sections.values()) {
        info.append(cs.toString()).append("\n");
        
        if (cs.isOccupied()) {
            ProcessControlBlock owner = processManager.getProcessByID(cs.getCurrentOwner());
            String ownerName = owner != null ? owner.getProcessName() : "Unknown";
            info.append(String.format("  Owner: PID %d (%s)\n", 
                cs.getCurrentOwner(), ownerName));
        }
        
        java.util.List<Integer> waiting = cs.getWaitingProcesses();
        if (!waiting.isEmpty()) {
            info.append("  Waiting Queue:\n");
            for (int pid : waiting) {
                ProcessControlBlock pcb = processManager.getProcessByID(pid);
                String name = pcb != null ? pcb.getProcessName() : "Unknown";
                info.append(String.format("    PID %d (%s)\n", pid, name));
            }
        }
        info.append("\n");
    }
    
    JTextArea textArea = new JTextArea(info.toString());
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(500, 400));
    
    JOptionPane.showMessageDialog(this, scrollPane,
        "Critical Sections", JOptionPane.INFORMATION_MESSAGE);
}

// ========== SEMAPHORE ACTIONS ==========

private void acquireSemaphore() {
    ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
    
    if (processes.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "No processes available!", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Select process
    String[] processOptions = new String[processes.size()];
    for (int i = 0; i < processes.size(); i++) {
        ProcessControlBlock pcb = processes.get(i);
        processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
    }
    
    String selectedProcess = (String) JOptionPane.showInputDialog(
        this, "Select process:", "Acquire Semaphore",
        JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]
    );
    
    if (selectedProcess == null) return;
    int pid = Integer.parseInt(selectedProcess.split(" - ")[0]);
    
    // Select semaphore
    Map<String, Semaphore> semaphores = resourceManager.getSemaphores();
    String[] semOptions = semaphores.keySet().toArray(new String[0]);
    
    String selectedSem = (String) JOptionPane.showInputDialog(
        this, "Select semaphore:", "Acquire Semaphore",
        JOptionPane.QUESTION_MESSAGE, null, semOptions, semOptions[0]
    );
    
    if (selectedSem == null) return;
    
    boolean acquired = resourceManager.acquireSemaphore(pid, selectedSem);
    
    if (acquired) {
        JOptionPane.showMessageDialog(this,
            String.format("Process %d acquired semaphore '%s'", pid, selectedSem),
            "Success", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this,
            String.format("Semaphore '%s' unavailable!\nProcess %d added to waiting queue.", 
                selectedSem, pid),
            "Waiting", JOptionPane.WARNING_MESSAGE);
    }
}

private void releaseSemaphore() {
    Map<String, Semaphore> semaphores = resourceManager.getSemaphores();
    String[] semOptions = semaphores.keySet().toArray(new String[0]);
    
    String selectedSem = (String) JOptionPane.showInputDialog(
        this, "Select semaphore to release:", "Release Semaphore",
        JOptionPane.QUESTION_MESSAGE, null, semOptions, semOptions[0]
    );
    
    if (selectedSem == null) return;
    
    Integer wokenPID = resourceManager.releaseSemaphore(selectedSem);
    
    String message = String.format("Semaphore '%s' released", selectedSem);
    
    if (wokenPID != null) {
        ProcessControlBlock wokenPCB = processManager.getProcessByID(wokenPID);
        String wokenName = wokenPCB != null ? wokenPCB.getProcessName() : "Unknown";
        message += String.format("\nProcess %d (%s) woken up and acquired the semaphore.", 
            wokenPID, wokenName);
    }
    
    JOptionPane.showMessageDialog(this, message,
        "Success", JOptionPane.INFORMATION_MESSAGE);
}

private void viewSemaphores() {
    Map<String, Semaphore> semaphores = resourceManager.getSemaphores();
    
    StringBuilder info = new StringBuilder();
    info.append("═══════════════════════════════════════════════\n");
    info.append("          SEMAPHORES STATUS\n");
    info.append("═══════════════════════════════════════════════\n\n");
    
    for (Semaphore sem : semaphores.values()) {
        info.append(sem.toString()).append("\n");
        
        java.util.List<Integer> waiting = sem.getWaitingProcesses();
        if (!waiting.isEmpty()) {
            info.append("  Waiting Queue:\n");
            for (int pid : waiting) {
                ProcessControlBlock pcb = processManager.getProcessByID(pid);
                String name = pcb != null ? pcb.getProcessName() : "Unknown";
                info.append(String.format("    PID %d (%s)\n", pid, name));
            }
        }
        info.append("\n");
    }
    
    JTextArea textArea = new JTextArea(info.toString());
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(500, 400));
    
    JOptionPane.showMessageDialog(this, scrollPane,
        "Semaphores", JOptionPane.INFORMATION_MESSAGE);
}

// ========== OTHER ACTIONS ==========

private void viewAllocationLog() {
    java.util.List<ResourceAllocationEvent> log = resourceManager.getAllocationLog();
    
    if (log.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "No allocation events logged yet.",
            "Allocation Log", JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    
    StringBuilder info = new StringBuilder();
    info.append("═══════════════════════════════════════════════\n");
    info.append("      RESOURCE ALLOCATION LOG\n");
    info.append("═══════════════════════════════════════════════\n\n");
    
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
    
    for (ResourceAllocationEvent event : log) {
        ProcessControlBlock pcb = processManager.getProcessByID(event.getProcessID());
        String name = pcb != null ? pcb.getProcessName() : "Unknown";
        
        String action = event.isAllocation() ? "REQUESTED" : "RELEASED";
        String status = event.isSuccess() ? "✓" : "✗";
        
        info.append(String.format("[%s] %s PID %d (%s) %s %d × %s %s\n",
            sdf.format(new java.util.Date(event.getTimestamp())),
            status,
            event.getProcessID(),
            name,
            action,
            event.getCount(),
            event.getResourceName(),
            event.isSuccess() ? "" : "(FAILED)"));
    }
    
    JTextArea textArea = new JTextArea(info.toString());
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
    
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(600, 450));
    
    JOptionPane.showMessageDialog(this, scrollPane,
        "Allocation Log", JOptionPane.INFORMATION_MESSAGE);
}

private void resetSynchronization() {
    int confirm = JOptionPane.showConfirmDialog(this,
        "WARNING: This will reset all synchronization primitives!\n\n" +
        "All resource allocations will be cleared.\n" +
        "All critical sections will be freed.\n" +
        "All semaphores will be reset.\n\n" +
        "Continue?",
        "Confirm Reset",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);
    
    if (confirm == JOptionPane.YES_OPTION) {
        resourceManager.reset();
        
        JOptionPane.showMessageDialog(this,
            "Synchronization system reset successfully!",
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
}