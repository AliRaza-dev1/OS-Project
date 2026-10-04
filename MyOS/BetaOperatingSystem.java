import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;

public class BetaOperatingSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainControlPanel();
        });
    }
}

class MainControlPanel extends JFrame {
    
    public MainControlPanel() {
        setTitle("BETA OS - Kernel Simulator");
        setSize(580, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.setBackground(new Color(230, 240, 250));
        
        JPanel topSection = new JPanel();
        topSection.setBackground(new Color(60, 120, 170));
        topSection.setPreferredSize(new Dimension(580, 75));
        
        JLabel heading = new JLabel("BETA OS Control Panel");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heading.setForeground(Color.WHITE);
        topSection.add(heading);
        
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(4, 1, 12, 12));
        optionsPanel.setBackground(new Color(230, 240, 250));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(35, 75, 35, 75));
        
        JButton btnProcess = makeButton("Process Management");
        JButton btnMemory = makeButton("Memory Management");
        JButton btnIO = makeButton("Synchronization and Resource Sharing ");
        JButton btnIPC = makeButton("Inter-Process Communication");
        
        btnProcess.addActionListener(e -> launchProcessWindow());
        btnMemory.addActionListener(e -> launchMemoryWindow());
          btnIO.addActionListener(e -> launchSynchronizationWindow());
          btnIPC.addActionListener(e -> launchIPCWindow());
        
        optionsPanel.add(btnProcess);
        optionsPanel.add(btnMemory);
        optionsPanel.add(btnIO);
        optionsPanel.add(btnIPC);
        
        container.add(topSection, BorderLayout.NORTH);
        container.add(optionsPanel, BorderLayout.CENTER);
        
        add(container);
        setVisible(true);
    }
    private void launchSynchronizationWindow() {
    new SynchronizationWindow();
}
private void launchIPCWindow() {
    new IPCWindow();
}
    
    private JButton makeButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setBackground(new Color(90, 140, 220));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(50, 100, 200));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(90, 140, 220));
            }
        });
        
        return button;
    }
    
    private void launchProcessWindow() {
        new ProcessManagementWindow();
    }
    private void launchMemoryWindow() {
    new MemoryManagementWindow();
}

}

class ProcessManagementWindow extends JFrame {
    private ProcessManager manager;
    private Scheduler scheduler;
    
    public ProcessManagementWindow() {
        manager = ProcessManager.getInstance();

        scheduler = Scheduler.getInstance();
        
        setTitle("BETA OS - Process Control");
        setSize(680, 640);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(250, 250, 250));
        
        JPanel banner = new JPanel();
        banner.setBackground(new Color(60, 120, 170));
        banner.setPreferredSize(new Dimension(680, 55));
        
        JLabel bannerText = new JLabel("Process Management");
        bannerText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        bannerText.setForeground(Color.WHITE);
        banner.add(bannerText);
        
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new GridLayout(7, 2, 12, 12));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        actionsPanel.setBackground(new Color(250, 250, 250));
        
        JButton btnNew = makeActionButton("Create Process");
        JButton btnRemove = makeActionButton("Destroy Process");
        JButton btnPause = makeActionButton("Suspend Process");
        JButton btnContinue = makeActionButton("Resume Process");
        JButton btnHalt = makeActionButton("Block Process");
        JButton btnWake = makeActionButton("Wakeup Process");
        JButton btnRun = makeActionButton("Dispatch Process");
        JButton btnModifyPriority = makeActionButton("Change Priority");
        JButton btnInteract = makeActionButton("Process Communication");
        JButton btnSettings = makeActionButton("Configuration");
        JButton btnViewQueues = makeActionButton("View Queues");
        JButton btnStartScheduler = makeActionButton("Start Scheduler");
        JButton btnGanttChart = makeActionButton("View Gantt Chart");
        JButton btnViewStats = makeActionButton("View Statistics");
        JButton btnViewPCB = makeActionButton("View Process PCB");

        btnViewStats.setBackground(new Color(80, 180, 100));
        btnViewStats.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnViewStats.setBackground(new Color(50, 150, 70));
                btnViewStats.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent evt) {
                btnViewStats.setBackground(new Color(80, 180, 100));
                btnViewStats.setForeground(Color.BLACK);
            }
        });
        
        btnNew.addActionListener(e -> initiateProcess());
        btnRemove.addActionListener(e -> destroyProcess());
        btnPause.addActionListener(e -> suspendProcess());
        btnContinue.addActionListener(e -> resumeProcess());
        btnHalt.addActionListener(e -> blockProcess());
        btnWake.addActionListener(e -> wakeupProcess());
        btnRun.addActionListener(e -> dispatchProcess());
        btnModifyPriority.addActionListener(e -> changePriority());
        btnInteract.addActionListener(e -> openProcessCommunication());
        btnSettings.addActionListener(e -> launchSettings());
        btnViewQueues.addActionListener(e -> viewQueues());
        btnStartScheduler.addActionListener(e -> startScheduler());
        btnGanttChart.addActionListener(e -> viewGanttChart());
        btnViewStats.addActionListener(e -> viewStatistics());
        btnViewPCB.addActionListener(e -> viewProcessPCB());
        
        actionsPanel.add(btnNew);
        actionsPanel.add(btnRemove);
        actionsPanel.add(btnPause);
        actionsPanel.add(btnContinue);
        actionsPanel.add(btnHalt);
        actionsPanel.add(btnWake);
        actionsPanel.add(btnRun);
        actionsPanel.add(btnModifyPriority);
        actionsPanel.add(btnInteract);
        actionsPanel.add(btnSettings);
        actionsPanel.add(btnViewQueues);
        actionsPanel.add(btnStartScheduler);
        actionsPanel.add(btnGanttChart);
        actionsPanel.add(btnViewStats);
        actionsPanel.add(btnViewPCB);
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
  private void initiateProcess() {
    String name = JOptionPane.showInputDialog(this, 
        "Enter process name:", 
        "New Process", 
        JOptionPane.PLAIN_MESSAGE);
    
    if (name != null && !name.trim().isEmpty()) {
        String burstStr = JOptionPane.showInputDialog(this, 
            "Enter burst time (seconds):", 
            "Burst Time", 
            JOptionPane.PLAIN_MESSAGE);
        
        if (burstStr != null && !burstStr.trim().isEmpty()) {
            try {
                int burstTime = Integer.parseInt(burstStr.trim());
                if (burstTime <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Burst time must be positive!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // NEW: Ask for memory requirement
                String memoryStr = JOptionPane.showInputDialog(this, 
                    "Enter memory requirement (KB):", 
                    "Memory Requirement", 
                    JOptionPane.PLAIN_MESSAGE);
                
                if (memoryStr != null && !memoryStr.trim().isEmpty()) {
                    try {
                        int memoryKB = Integer.parseInt(memoryStr.trim());
                        if (memoryKB <= 0) {
                            JOptionPane.showMessageDialog(this, 
                                "Memory must be positive!", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        ProcessControlBlock block = manager.createProcess(name.trim(), burstTime, memoryKB);
                        scheduler.admitProcess(block);
                        new PCBDetailsWindow(block);
                        
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Invalid memory value!", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid burst time!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
    private void destroyProcess() {
        ArrayList<ProcessControlBlock> availableProcesses = new ArrayList<>();
        for (ProcessControlBlock pcb : manager.getAllProcesses()) {
            if (!pcb.getProcessState().equals("TERMINATED")) {
                availableProcesses.add(pcb);
            }
        }
        
        if (availableProcesses.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No processes available to destroy.", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] processOptions = new String[availableProcesses.size()];
        for (int i = 0; i < availableProcesses.size(); i++) {
            ProcessControlBlock pcb = availableProcesses.get(i);
            processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName() + " (" + pcb.getProcessState() + ")";
        }
        
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select process to destroy:",
            "Destroy Process",
            JOptionPane.QUESTION_MESSAGE,
            null,
            processOptions,
            processOptions[0]
        );
        
        if (selected != null) {
            int pid = Integer.parseInt(selected.split(" - ")[0]);
            ProcessControlBlock pcb = manager.getProcessByID(pid);
            
            try {
                scheduler.terminateProcess(pcb);
                JOptionPane.showMessageDialog(this, 
                    "Process " + pid + " terminated!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), 
                    "Invalid Operation", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void suspendProcess() {
        ArrayList<ProcessControlBlock> availableProcesses = new ArrayList<>();
        for (ProcessControlBlock pcb : manager.getAllProcesses()) {
            String state = pcb.getProcessState();
            if (state.equals("READY") || state.equals("BLOCKED")) {
                availableProcesses.add(pcb);
            }
        }
        
        if (availableProcesses.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No READY or BLOCKED processes available to suspend.", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] processOptions = new String[availableProcesses.size()];
        for (int i = 0; i < availableProcesses.size(); i++) {
            ProcessControlBlock pcb = availableProcesses.get(i);
            processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName() + " (" + pcb.getProcessState() + ")";
        }
        
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select process to suspend:",
            "Suspend Process",
            JOptionPane.QUESTION_MESSAGE,
            null,
            processOptions,
            processOptions[0]
        );
        
        if (selected != null) {
            int pid = Integer.parseInt(selected.split(" - ")[0]);
            ProcessControlBlock pcb = manager.getProcessByID(pid);
            
            try {
                scheduler.suspendProcess(pcb);
                JOptionPane.showMessageDialog(this, 
                    "Process " + pid + " suspended!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), 
                    "Invalid Operation", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void resumeProcess() {
        ArrayList<ProcessControlBlock> availableProcesses = new ArrayList<>();
        for (ProcessControlBlock pcb : manager.getAllProcesses()) {
            if (pcb.getProcessState().equals("SUSPENDED")) {
                availableProcesses.add(pcb);
            }
        }
        
        if (availableProcesses.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No SUSPENDED processes available to resume.", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] processOptions = new String[availableProcesses.size()];
        for (int i = 0; i < availableProcesses.size(); i++) {
            ProcessControlBlock pcb = availableProcesses.get(i);
            processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName() + " (" + pcb.getProcessState() + ")";
        }
        
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select process to resume:",
            "Resume Process",
            JOptionPane.QUESTION_MESSAGE,
            null,
            processOptions,
            processOptions[0]
        );
        
        if (selected != null) {
            int pid = Integer.parseInt(selected.split(" - ")[0]);
            ProcessControlBlock pcb = manager.getProcessByID(pid);
            
            try {
                scheduler.resumeProcess(pcb);
                JOptionPane.showMessageDialog(this, 
                    "Process " + pid + " resumed!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), 
                    "Invalid Operation", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void blockProcess() {
        ProcessControlBlock runningProc = scheduler.getRunningProcess();
        
        if (runningProc == null) {
            JOptionPane.showMessageDialog(this, 
                "No RUNNING process available to block.", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String processInfo = runningProc.getProcessID() + " - " + runningProc.getProcessName() + " (" + runningProc.getProcessState() + ")";
        
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Block this process?\n\n" + processInfo,
            "Block Process",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                scheduler.blockProcess(runningProc);
                JOptionPane.showMessageDialog(this, 
                    "Process " + runningProc.getProcessID() + " blocked!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), 
                    "Invalid Operation", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void wakeupProcess() {
        ArrayList<ProcessControlBlock> availableProcesses = new ArrayList<>();
        for (ProcessControlBlock pcb : scheduler.getBlockedQueue()) {
            availableProcesses.add(pcb);
        }
        
        if (availableProcesses.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No BLOCKED processes available to wakeup.", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] processOptions = new String[availableProcesses.size()];
        for (int i = 0; i < availableProcesses.size(); i++) {
            ProcessControlBlock pcb = availableProcesses.get(i);
            processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName() + " (" + pcb.getProcessState() + ")";
        }
        
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select process to wakeup:",
            "Wakeup Process",
            JOptionPane.QUESTION_MESSAGE,
            null,
            processOptions,
            processOptions[0]
        );
        
        if (selected != null) {
            int pid = Integer.parseInt(selected.split(" - ")[0]);
            ProcessControlBlock pcb = manager.getProcessByID(pid);
            
            try {
                scheduler.wakeupProcess(pcb);
                JOptionPane.showMessageDialog(this, 
                    "Process " + pid + " woken up!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), 
                    "Invalid Operation", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void dispatchProcess() {
        try {
            ProcessControlBlock pcb = scheduler.dispatch();
            if (pcb != null) {
                JOptionPane.showMessageDialog(this, 
                    "Process " + pcb.getProcessID() + " dispatched to CPU!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No process in ready queue!", 
                    "Info", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, 
                ex.getMessage(), 
                "Invalid Operation", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void changePriority() {
        ArrayList<ProcessControlBlock> availableProcesses = new ArrayList<>();
        for (ProcessControlBlock pcb : manager.getAllProcesses()) {
            if (!pcb.getProcessState().equals("TERMINATED")) {
                availableProcesses.add(pcb);
            }
        }
        
        if (availableProcesses.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No processes available.", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] processOptions = new String[availableProcesses.size()];
        for (int i = 0; i < availableProcesses.size(); i++) {
            ProcessControlBlock pcb = availableProcesses.get(i);
            processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName() + " (Priority: " + pcb.getPriority() + ")";
        }
        
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select process to change priority:",
            "Change Priority",
            JOptionPane.QUESTION_MESSAGE,
            null,
            processOptions,
            processOptions[0]
        );
        
        if (selected != null) {
            int pid = Integer.parseInt(selected.split(" - ")[0]);
            ProcessControlBlock pcb = manager.getProcessByID(pid);
            
            String prioStr = JOptionPane.showInputDialog(this, 
                "Enter new priority (1-10) for " + pcb.getProcessName() + ":", 
                "New Priority", 
                JOptionPane.PLAIN_MESSAGE);
            
            if (prioStr != null && !prioStr.trim().isEmpty()) {
                try {
                    int newPriority = Integer.parseInt(prioStr.trim());
                    if (newPriority >= 1 && newPriority <= 10) {
                        pcb.setPriority(newPriority);
                        JOptionPane.showMessageDialog(this, 
                            "Priority of " + pcb.getProcessName() + " changed to " + newPriority, 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Priority must be 1-10!", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid priority value!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    private void openProcessCommunication() {
    new ProcessCommunicationWindow();
}
    private void viewQueues() {
        new QueueVisualizationWindow();
    }
    
    private void startScheduler() {
    Scheduler scheduler = Scheduler.getInstance();
    String currentAlgo = scheduler.getAlgorithm();
    
    // Check if algorithm is set
    if (currentAlgo.equals("FCFS")) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Start FCFS Scheduler?",
            "Confirm",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            scheduler.startScheduling();
            JOptionPane.showMessageDialog(this, 
                "FCFS Scheduler started!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    } else if (currentAlgo.equals("RoundRobin")) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Start Round Robin Scheduler?\nTime Quantum: " + 
            scheduler.getTimeQuantum() + " seconds",
            "Confirm",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            scheduler.startScheduling();
            JOptionPane.showMessageDialog(this, 
                "Round Robin Scheduler started!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
    private void launchSettings() {
        new ConfigurationWindow();
    }
    
    private void viewGanttChart() {
    new GanttChartWindow();
}
    private void viewStatistics() {
        new StatisticsWindow();
    }
    private void viewProcessPCB() {
    ArrayList<ProcessControlBlock> processes = manager.getAllProcesses();
    
    if (processes.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "No processes available!", 
            "Info", 
            JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    
    String[] processOptions = new String[processes.size()];
    for (int i = 0; i < processes.size(); i++) {
        ProcessControlBlock pcb = processes.get(i);
        processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName() + " (" + pcb.getProcessState() + ")";
    }
    
    String selected = (String) JOptionPane.showInputDialog(
        this,
        "Select process to view PCB:",
        "View PCB",
        JOptionPane.QUESTION_MESSAGE,
        null,
        processOptions,
        processOptions[0]
    );
    
    if (selected != null) {
        int pid = Integer.parseInt(selected.split(" - ")[0]);
        ProcessControlBlock pcb = manager.getProcessByID(pid);
        new PCBDetailsWindow(pcb);
    }
}
}

class QueueVisualizationWindow extends JFrame {
    private Scheduler scheduler;
    private JTextArea readyArea;
    private JTextArea blockedArea;
    private JTextArea suspendedArea;
    private JTextArea runningArea;
    private Timer refreshTimer;
    
    public QueueVisualizationWindow() {
        scheduler = Scheduler.getInstance();
        
        setTitle("BETA OS - Queue Visualization");
        setSize(720, 580);
        setLocationRelativeTo(null);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel heading = new JLabel("Process Scheduling Queues");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JPanel queuesPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        queuesPanel.setBackground(Color.WHITE);
        
        JPanel runningPanel = createQueuePanel("Running Process");
        runningArea = new JTextArea();
        runningArea.setEditable(false);
        runningArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane runningScroll = new JScrollPane(runningArea);
        runningPanel.add(runningScroll, BorderLayout.CENTER);
        
        JPanel readyPanel = createQueuePanel("Ready Queue");
        readyArea = new JTextArea();
        readyArea.setEditable(false);
        readyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane readyScroll = new JScrollPane(readyArea);
        readyPanel.add(readyScroll, BorderLayout.CENTER);
        
        JPanel blockedPanel = createQueuePanel("Blocked Queue");
        blockedArea = new JTextArea();
        blockedArea.setEditable(false);
        blockedArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane blockedScroll = new JScrollPane(blockedArea);
        blockedPanel.add(blockedScroll, BorderLayout.CENTER);
        
        JPanel suspendedPanel = createQueuePanel("Suspended Queue");
        suspendedArea = new JTextArea();
        suspendedArea.setEditable(false);
        suspendedArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane suspendedScroll = new JScrollPane(suspendedArea);
        suspendedPanel.add(suspendedScroll, BorderLayout.CENTER);
        
        queuesPanel.add(runningPanel);
        queuesPanel.add(readyPanel);
        queuesPanel.add(blockedPanel);
        queuesPanel.add(suspendedPanel);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(90, 140, 220));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> {
            refreshTimer.cancel();
            dispose();
        });
        
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        btnPanel.add(closeBtn);
        
        wrapper.add(heading, BorderLayout.NORTH);
        wrapper.add(queuesPanel, BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);
        
        add(wrapper);
        
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateQueues();
            }
        }, 0, 500);
        
        setVisible(true);
    }
    
    private JPanel createQueuePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(90, 140, 220), 2),
            title,
            0,
            0,
            new Font("Segoe UI", Font.BOLD, 13)
        ));
        return panel;
    }
   
    private void updateQueues() {
        SwingUtilities.invokeLater(() -> {
            ProcessControlBlock running = scheduler.getRunningProcess();
            if (running != null) {
                runningArea.setText(String.format("PID: %d | Name: %s | Time Left: %d sec\n", 
                    running.getProcessID(), 
                    running.getProcessName(), 
                    running.getRemainingTime()));
            } else {
                runningArea.setText("No process running");
            }
            
            StringBuilder readyText = new StringBuilder();
            for (ProcessControlBlock pcb : scheduler.getReadyQueue()) {
                readyText.append(String.format("PID: %d | %s | Priority: %d | Burst: %d\n", 
                    pcb.getProcessID(), 
                    pcb.getProcessName(), 
                    pcb.getPriority(),
                    pcb.getBurstTime()));
            }
            readyArea.setText(readyText.length() > 0 ? readyText.toString() : "Queue is empty");
            
            StringBuilder blockedText = new StringBuilder();
            for (ProcessControlBlock pcb : scheduler.getBlockedQueue()) {
                blockedText.append(String.format("PID: %d | %s\n", 
                    pcb.getProcessID(), 
                    pcb.getProcessName()));
            }
            blockedArea.setText(blockedText.length() > 0 ? blockedText.toString() : "Queue is empty");
            
            StringBuilder suspendedText = new StringBuilder();
            for (ProcessControlBlock pcb : scheduler.getSuspendedQueue()) {
                suspendedText.append(String.format("PID: %d | %s\n", 
                    pcb.getProcessID(), 
                    pcb.getProcessName()));
            }
            suspendedArea.setText(suspendedText.length() > 0 ? suspendedText.toString() : "Queue is empty");
        });
    }
}

class PCBDetailsWindow extends JFrame {
    
    public PCBDetailsWindow(ProcessControlBlock block) {
        setTitle("PCB Details - ID: " + block.getProcessID());
        setSize(480, 620);
        setLocationRelativeTo(null);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        
        JLabel heading = new JLabel("Process Control Block Information");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 17));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        
        JPanel infoPanel = new JPanel(new GridLayout(14, 2, 8, 10));
        infoPanel.setBackground(Color.WHITE);
        
        insertRow(infoPanel, "Process ID:", String.valueOf(block.getProcessID()));
        insertRow(infoPanel, "Process Name:", block.getProcessName());
        insertRow(infoPanel, "Current State:", block.getProcessState());
        insertRow(infoPanel, "Owner:", block.getOwner());
        insertRow(infoPanel, "Priority:", String.valueOf(block.getPriority()));
        insertRow(infoPanel, "Burst Time:", block.getBurstTime() + " sec");
        insertRow(infoPanel, "Remaining Time:", block.getRemainingTime() + " sec");
        insertRow(infoPanel, "Parent Process:", 
            block.getParentProcess() == null ? "None" : String.valueOf(block.getParentProcess().getProcessID()));
        insertRow(infoPanel, "Child Processes:", String.valueOf(block.getChildProcesses().size()));
        insertRow(infoPanel, "Memory Requirements:", block.getMemoryRequirements() + " KB");
        insertRow(infoPanel, "Memory Pointer:", block.getAllocatedMemoryPointer());
        insertRow(infoPanel, "Register Save Area:", block.getRegisterSaveArea());
        insertRow(infoPanel, "Processor Running On:", String.valueOf(block.getProcessorRunningOn()));
        insertRow(infoPanel, "I/O State Info:", block.getIoStateInfo());
        
        JButton closeBtn = new JButton("OK");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        closeBtn.setBackground(new Color(90, 140, 220));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());
        
        JPanel btnArea = new JPanel();
        btnArea.setBackground(Color.WHITE);
        btnArea.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));
        btnArea.add(closeBtn);
        
        wrapper.add(heading, BorderLayout.NORTH);
        wrapper.add(infoPanel, BorderLayout.CENTER);
        wrapper.add(btnArea, BorderLayout.SOUTH);
        
        add(wrapper);
        setVisible(true);
    }
    
    private void insertRow(JPanel area, String labelTxt, String valueTxt) {
        JLabel lbl = new JLabel(labelTxt);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JLabel val = new JLabel(valueTxt);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        area.add(lbl);
        area.add(val);
    }
}

class GanttChartWindow extends JFrame {
    private ProcessManager processManager;
    private Scheduler scheduler;
    private JPanel chartPanel;
    private Timer refreshTimer;
    
    public GanttChartWindow() {
        processManager = ProcessManager.getInstance();
        scheduler = Scheduler.getInstance();
        
        setTitle("BETA OS - Gantt Chart Visualization");
        setSize(900, 500);
        setLocationRelativeTo(null);
        
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel heading = new JLabel("Process Scheduling Gantt Chart");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setForeground(new Color(60, 120, 170));
        
        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGanttChart(g);
            }
        };
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(850, 300));
        chartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(90, 140, 220), 2),
            "Timeline",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createTitledBorder("Legend"));
        
        String[] states = {"RUNNING", "READY", "BLOCKED", "TERMINATED"};
        Color[] colors = {
            new Color(100, 200, 100),  // Green for RUNNING
            new Color(255, 200, 100),  // Orange for READY
            new Color(255, 150, 150),  // Red for BLOCKED
            new Color(180, 180, 180)   // Gray for TERMINATED
        };
        
        for (int i = 0; i < states.length; i++) {
            JPanel colorBox = new JPanel();
            colorBox.setBackground(colors[i]);
            colorBox.setPreferredSize(new Dimension(30, 20));
            colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            
            JLabel label = new JLabel(states[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            legendPanel.add(colorBox);
            legendPanel.add(label);
        }
        
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        closeBtn.setBackground(new Color(200, 80, 80));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> {
            if (refreshTimer != null) {
                refreshTimer.cancel();
            }
            dispose();
        });
        
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(closeBtn);
        
        wrapper.add(heading, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(chartPanel), BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(legendPanel, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        wrapper.add(bottomPanel, BorderLayout.SOUTH);
        
        add(wrapper);
        
        // Auto-refresh every second
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> chartPanel.repaint());
            }
        }, 0, 1000);
        
        setVisible(true);
    }
    
    private void drawGanttChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        ArrayList<ProcessControlBlock> allProcesses = processManager.getAllProcesses();
        
        if (allProcesses.isEmpty()) {
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2d.setColor(Color.GRAY);
            g2d.drawString("No processes to display", 350, 150);
            return;
        }
        
        int startX = 50;
        int startY = 50;
        int rowHeight = 40;
        int timeUnit = 30; // pixels per second
        
        // Draw process names
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        for (int i = 0; i < allProcesses.size(); i++) {
            ProcessControlBlock pcb = allProcesses.get(i);
            g2d.setColor(Color.BLACK);
            g2d.drawString("P" + pcb.getProcessID(), 10, startY + i * rowHeight + 25);
        }
        
        // Find max time
int maxTime = scheduler.getSimulationTime();
for (ProcessControlBlock pcb : allProcesses) {
    if (pcb.getProcessState().equals("TERMINATED") && pcb.getCompletionTime() > maxTime) {
        maxTime = pcb.getCompletionTime();
    }
}
        if (maxTime == 0) maxTime = 10; // Default scale
        
        // Draw time axis
        g2d.setColor(Color.BLACK);
        g2d.drawLine(startX, startY + allProcesses.size() * rowHeight + 10, 
                     startX + maxTime * timeUnit, startY + allProcesses.size() * rowHeight + 10);
        
        for (int t = 0; t <= maxTime; t++) {
            int x = startX + t * timeUnit;
            g2d.drawLine(x, startY + allProcesses.size() * rowHeight + 10, 
                        x, startY + allProcesses.size() * rowHeight + 15);
            g2d.drawString(String.valueOf(t), x - 5, startY + allProcesses.size() * rowHeight + 30);
        }
        
        // Draw process bars
        // Draw process bars
for (int i = 0; i < allProcesses.size(); i++) {
    ProcessControlBlock pcb = allProcesses.get(i);
    int y = startY + i * rowHeight;
    
    // Use actual start and completion times
    int processStart = pcb.getStartTime();
    int processEnd = pcb.getProcessState().equals("TERMINATED") ? 
                     pcb.getCompletionTime() : scheduler.getSimulationTime();
    int processDuration = processEnd - processStart;
    
    if (pcb.hasStarted() && processDuration > 0) {
        int barStartX = startX + processStart * timeUnit;
        int barWidth = processDuration * timeUnit;
        
        // Choose color based on state
        Color barColor;
        String state = pcb.getProcessState();
        switch (state) {
                    case "RUNNING":
                        barColor = new Color(100, 200, 100);
                        break;
                    case "READY":
                        barColor = new Color(255, 200, 100);
                        break;
                    case "BLOCKED":
                        barColor = new Color(255, 150, 150);
                        break;
                    case "TERMINATED":
                        barColor = new Color(180, 180, 180);
                        break;
                    default:
                        barColor = new Color(200, 200, 200);
                }
                
               g2d.setColor(barColor);
               g2d.fillRect(barStartX, y, barWidth, rowHeight - 10);

               g2d.setColor(Color.BLACK);
               g2d.drawRect(barStartX, y, barWidth, rowHeight - 10);
                
                // Draw state text
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2d.drawString(state, startX + 5, y + 20);
            }
        }
        
        // Show current running process with indicator
      // Show current running process with indicator
               ProcessControlBlock running = scheduler.getRunningProcess();
               if (running != null) {
               int index = allProcesses.indexOf(running);
                if (index != -1) {
                int y = startY + index * rowHeight;
                int currentTime = scheduler.getSimulationTime();
                int x = startX + currentTime * timeUnit;
                // Draw arrow
                g2d.setColor(Color.RED);
                g2d.setStroke(new java.awt.BasicStroke(2));
                int[] xPoints = {x, x - 8, x - 8};
                int[] yPoints = {y + 15, y + 5, y + 25};
                g2d.fillPolygon(xPoints, yPoints, 3);
            }
        }
    }
}
class ProcessCommunicationWindow extends JFrame {
    private ProcessManager processManager;
    private JTextArea messageArea;
    
    public ProcessCommunicationWindow() {
        processManager = ProcessManager.getInstance();
        
        setTitle("BETA OS - Process Communication");
        setSize(650, 550);
        setLocationRelativeTo(null);
        
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel heading = new JLabel("Inter-Process Communication");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setForeground(new Color(60, 120, 170));
        
        JPanel controlPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(90, 140, 220), 2),
            "Send Message",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        
        JLabel lblSender = new JLabel("From Process:");
        lblSender.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JComboBox<String> cmbSender = new JComboBox<>();
        updateProcessList(cmbSender);
        
        JLabel lblReceiver = new JLabel("To Process:");
        lblReceiver.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JComboBox<String> cmbReceiver = new JComboBox<>();
        updateProcessList(cmbReceiver);
        
        controlPanel.add(lblSender);
        controlPanel.add(cmbSender);
        controlPanel.add(lblReceiver);
        controlPanel.add(cmbReceiver);
        
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(90, 140, 220), 2),
            "Message",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        
        JTextField txtMessage = new JTextField();
        txtMessage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMessage.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        messagePanel.add(new JLabel("Enter message:"), BorderLayout.NORTH);
        messagePanel.add(txtMessage, BorderLayout.CENTER);
        
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(Color.WHITE);
        logPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(90, 140, 220), 2),
            "Communication Log",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        
        messageArea = new JTextArea(10, 50);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        messageArea.setBackground(new Color(245, 250, 255));
        messageArea.setText("=== Process Communication Log ===\n\n");
        
        JScrollPane scrollPane = new JScrollPane(messageArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnSend = new JButton("Send Message");
        btnSend.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSend.setBackground(new Color(90, 140, 220));
        btnSend.setForeground(Color.WHITE);
        btnSend.setFocusPainted(false);
        btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnClear = new JButton("Clear Log");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClear.setBackground(new Color(220, 140, 90));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClose.setBackground(new Color(200, 80, 80));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnSend.addActionListener(e -> {
            String senderStr = (String) cmbSender.getSelectedItem();
            String receiverStr = (String) cmbReceiver.getSelectedItem();
            String message = txtMessage.getText().trim();
            
            if (senderStr == null || receiverStr == null) {
                JOptionPane.showMessageDialog(this, 
                    "Please select both sender and receiver processes!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a message!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (senderStr.equals(receiverStr)) {
                JOptionPane.showMessageDialog(this, 
                    "Sender and receiver cannot be the same!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int senderPID = Integer.parseInt(senderStr.split(" - ")[0]);
            int receiverPID = Integer.parseInt(receiverStr.split(" - ")[0]);
            
            ProcessControlBlock sender = processManager.getProcessByID(senderPID);
            ProcessControlBlock receiver = processManager.getProcessByID(receiverPID);
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
            String timestamp = sdf.format(new java.util.Date());
            
            String logEntry = String.format("[%s] PID %d (%s) → PID %d (%s): \"%s\"\n",
                timestamp,
                sender.getProcessID(),
                sender.getProcessName(),
                receiver.getProcessID(),
                receiver.getProcessName(),
                message
            );
            
            messageArea.append(logEntry);
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
            
            txtMessage.setText("");
            
            JOptionPane.showMessageDialog(this, 
                "Message sent successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnClear.addActionListener(e -> {
            messageArea.setText("=== Process Communication Log ===\n\n");
        });
        
        btnClose.addActionListener(e -> dispose());
        
        buttonPanel.add(btnSend);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnClose);
        
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(messagePanel, BorderLayout.CENTER);
        
        wrapper.add(heading, BorderLayout.NORTH);
        wrapper.add(topPanel, BorderLayout.NORTH);
        wrapper.add(logPanel, BorderLayout.CENTER);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);
        
        add(wrapper);
        setVisible(true);
    }
    
    private void updateProcessList(JComboBox<String> combo) {
        combo.removeAllItems();
        ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
        
        for (ProcessControlBlock pcb : processes) {
            if (!pcb.getProcessState().equals("TERMINATED")) {
                combo.addItem(pcb.getProcessID() + " - " + pcb.getProcessName() + " (" + pcb.getProcessState() + ")");
            }
        }
        
        if (combo.getItemCount() == 0) {
            combo.addItem("No processes available");
        }
    }
}
class ConfigurationWindow extends JFrame {
    
    public ConfigurationWindow() {
        setTitle("BETA OS - System Configuration");
        setSize(430, 450);  // Made window taller
        setLocationRelativeTo(null);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        
        JLabel heading = new JLabel("Kernel Configuration Settings");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        
        JPanel settingsArea = new JPanel(new GridLayout(6, 2, 8, 12));  // Changed from 5 to 6 rows
        settingsArea.setBackground(Color.WHITE);
        
        JLabel lblMaxProc = new JLabel("Max Processes:");
        JTextField txtMaxProc = new JTextField("100");
        
        JLabel lblSlice = new JLabel("Time Quantum (sec):");
        JTextField txtSlice = new JTextField("2");
        
        JLabel lblMem = new JLabel("Total Memory (MB):");
        JTextField txtMem = new JTextField("1024");
        
        JLabel lblScheduler = new JLabel("Scheduler Type:");
        String[] options = {"FCFS","Round Robin"};
        JComboBox<String> cmbScheduler = new JComboBox<>(options);
        
        JLabel lblCPU = new JLabel("Number of CPUs:");
        JTextField txtCPU = new JTextField("4");
        
        // NEW: Add Page Size configuration
        JLabel lblPageSize = new JLabel("Page Size (KB):");
        JTextField txtPageSize = new JTextField(String.valueOf(PhysicalMemory.getInstance().getPageSizeKB()));
        
        settingsArea.add(lblMaxProc);
        settingsArea.add(txtMaxProc);
        settingsArea.add(lblSlice);
        settingsArea.add(txtSlice);
        settingsArea.add(lblMem);
        settingsArea.add(txtMem);
        settingsArea.add(lblScheduler);
        settingsArea.add(cmbScheduler);
        settingsArea.add(lblCPU);
        settingsArea.add(txtCPU);
        settingsArea.add(lblPageSize);    
        settingsArea.add(txtPageSize);      
        
        JPanel btnArea = new JPanel();
        btnArea.setBackground(Color.WHITE);
        btnArea.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));
        
   JButton btnSave = new JButton("Save");
btnSave.setBackground(new Color(90, 140, 220));
btnSave.setForeground(Color.WHITE);
btnSave.setFocusPainted(false);
btnSave.addActionListener(e -> {
    // Get selected algorithm
    String selectedAlgo = (String) cmbScheduler.getSelectedItem();
    
    // Get time quantum
    try {
        int quantum = Integer.parseInt(txtSlice.getText().trim());
        
        // Apply to scheduler
        Scheduler scheduler = Scheduler.getInstance();
        
        if (selectedAlgo.equals("Round Robin")) {
            scheduler.setAlgorithm("RoundRobin");
            scheduler.setTimeQuantum(quantum);
        } else {
            scheduler.setAlgorithm("FCFS");
        }
        
        // NEW: Handle page size change
        try {
            int newPageSize = Integer.parseInt(txtPageSize.getText().trim());
            
            if (newPageSize != PhysicalMemory.getInstance().getPageSizeKB()) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "WARNING: Changing page size will RESET the entire memory system!\n\n" +
                    "All memory allocations will be cleared.\n" +
                    "All processes will lose their memory.\n\n" +
                    "New Page Size: " + newPageSize + " KB\n" +
                    "Total Frames: " + (1024 / newPageSize) + "\n\n" +
                    "Continue?",
                    "Confirm Page Size Change",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = PhysicalMemory.getInstance().setPageSize(newPageSize);
                    
                    if (!success) {
                        JOptionPane.showMessageDialog(this, 
                            "Invalid page size!\n\n" +
                            "Page size must:\n" +
                            "- Be greater than 0\n" +
                            "- Be less than or equal to 1024 KB\n" +
                            "- Evenly divide 1024 KB\n\n" +
                            "Valid sizes: 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Clear memory from all processes
                    MemoryManager memManager = MemoryManager.getInstance();
                    ProcessManager procManager = ProcessManager.getInstance();
                    
                    for (ProcessControlBlock pcb : procManager.getAllProcesses()) {
                        memManager.deallocateMemory(pcb.getProcessID());
                        pcb.setMemoryRequirements(0);
                    }
                    
                    JOptionPane.showMessageDialog(this, 
                        "Settings saved!\n\n" +
                        "Algorithm: " + selectedAlgo + 
                        (selectedAlgo.equals("Round Robin") ? "\nTime Quantum: " + quantum + " seconds" : "") +
                        "\n\nPage Size: " + newPageSize + " KB" +
                        "\nTotal Frames: " + (1024 / newPageSize) +
                        "\n\nMemory system has been reset!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    return; // User cancelled
                }
            } else {
                // No page size change
                JOptionPane.showMessageDialog(this, 
                    "Settings saved!\nAlgorithm: " + selectedAlgo + 
                    (selectedAlgo.equals("Round Robin") ? "\nTime Quantum: " + quantum + " seconds" : ""), 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (NumberFormatException ex2) {
            JOptionPane.showMessageDialog(this, 
                "Invalid page size value!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, 
            "Invalid time slice value!", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    dispose();
});
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dispose());
        
        btnArea.add(btnSave);
        btnArea.add(btnCancel);
        
        wrapper.add(heading, BorderLayout.NORTH);
        wrapper.add(settingsArea, BorderLayout.CENTER);
        wrapper.add(btnArea, BorderLayout.SOUTH);
        
        add(wrapper);
        setVisible(true);
    }
}

class StatisticsWindow extends JFrame {
    private ProcessManager manager;
    private Scheduler scheduler;
    private JTextArea summaryArea;
    private JTable processTable;
    
    public StatisticsWindow() {
        manager = ProcessManager.getInstance();
        scheduler = Scheduler.getInstance();
        
        setTitle("BETA OS - Performance Statistics");
        setSize(850, 600);
        setLocationRelativeTo(null);
        
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel heading = new JLabel("System Performance Metrics");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setForeground(new Color(60, 120, 170));
        
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(90, 140, 220), 2),
            "Overall Statistics",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        summaryPanel.setBackground(Color.WHITE);
        
        summaryArea = new JTextArea(6, 50);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        summaryArea.setBackground(new Color(245, 250, 255));
        summaryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        updateSummaryStatistics();
        
        summaryPanel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(90, 140, 220), 2),
            "Completed Processes Details",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        tablePanel.setBackground(Color.WHITE);
        
        String[] columns = {"PID", "Name", "Burst (sec)", "Start Time", 
                           "Completion Time", "Turnaround (sec)", "Waiting (sec)"};
        
        ArrayList<ProcessControlBlock> terminated = manager.getTerminatedProcesses();
        Object[][] data = new Object[terminated.size()][7];
        
        for (int i = 0; i < terminated.size(); i++) {
            ProcessControlBlock pcb = terminated.get(i);
            data[i][0] = pcb.getProcessID();
            data[i][1] = pcb.getProcessName();
            data[i][2] = pcb.getBurstTime();
            data[i][3] = formatTime(pcb.getStartTime());
            data[i][4] = formatTime(pcb.getCompletionTime());
            data[i][5] = pcb.getTurnaroundTime();
            data[i][6] = pcb.getWaitingTime();
        }
        
        processTable = new JTable(data, columns);
        processTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        processTable.setRowHeight(25);
        processTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        processTable.getTableHeader().setBackground(new Color(90, 140, 220));
        processTable.getTableHeader().setForeground(Color.WHITE);
        processTable.setEnabled(false);
        
        JScrollPane tableScroll = new JScrollPane(processTable);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshBtn.setBackground(new Color(90, 140, 220));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> refreshStatistics());
        
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        closeBtn.setBackground(new Color(200, 80, 80));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(closeBtn);
        
        wrapper.add(heading, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(summaryPanel, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        wrapper.add(centerPanel, BorderLayout.CENTER);
        
        wrapper.add(buttonPanel, BorderLayout.SOUTH);
        
        add(wrapper);
        setVisible(true);
    }
    
    private void updateSummaryStatistics() {
        ArrayList<ProcessControlBlock> terminated = manager.getTerminatedProcesses();
        
        StringBuilder stats = new StringBuilder();
        stats.append("═══════════════════════════════════════════════════════════\n");
        stats.append(String.format("  Total Processes Completed: %d\n", terminated.size()));
        stats.append(String.format("  CPU Utilization: %.2f%%\n", scheduler.getCpuUtilization()));
        stats.append(String.format("  Throughput: %.4f processes/second\n", scheduler.getThroughput()));
        stats.append(String.format("  Average Turnaround Time: %.2f seconds\n", scheduler.getAverageTurnaroundTime()));
        stats.append(String.format("  Average Waiting Time: %.2f seconds\n", scheduler.getAverageWaitingTime()));
        stats.append("═══════════════════════════════════════════════════════════");
        
        summaryArea.setText(stats.toString());
    }
    
    private void refreshStatistics() {
        updateSummaryStatistics();
        
        ArrayList<ProcessControlBlock> terminated = manager.getTerminatedProcesses();
        Object[][] newData = new Object[terminated.size()][7];
        
        for (int i = 0; i < terminated.size(); i++) {
            ProcessControlBlock pcb = terminated.get(i);
            newData[i][0] = pcb.getProcessID();
            newData[i][1] = pcb.getProcessName();
            newData[i][2] = pcb.getBurstTime();
            newData[i][3] = formatTime(pcb.getStartTime());
            newData[i][4] = formatTime(pcb.getCompletionTime());
            newData[i][5] = pcb.getTurnaroundTime();
            newData[i][6] = pcb.getWaitingTime();
        }
        
        String[] columns = {"PID", "Name", "Burst (sec)", "Start Time", 
                           "Completion Time", "Turnaround (sec)", "Waiting (sec)"};
        
        processTable.setModel(new DefaultTableModel(newData, columns));
        processTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        processTable.getTableHeader().setBackground(new Color(90, 140, 220));
        processTable.getTableHeader().setForeground(Color.WHITE);
        
        JOptionPane.showMessageDialog(this, 
            "Statistics refreshed!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
        private String formatTime(int simTime) {
        return simTime + " sec";
  }
 }
// ADD THIS ENTIRE CLASS TO THE END OF BetaOperatingSystem.java

class MemoryManagementWindow extends JFrame {
    private MemoryManager memoryManager;
    private ProcessManager processManager;
    
    public MemoryManagementWindow() {
        memoryManager = MemoryManager.getInstance();
        processManager = ProcessManager.getInstance();
        
        setTitle("BETA OS - Memory Control");
        setSize(680, 640);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(250, 250, 250));
        
        JPanel banner = new JPanel();
        banner.setBackground(new Color(60, 120, 170));
        banner.setPreferredSize(new Dimension(680, 55));
        
        JLabel bannerText = new JLabel("Memory Management (Paging System)");
        bannerText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        bannerText.setForeground(Color.WHITE);
        banner.add(bannerText);
        
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new GridLayout(7, 2, 12, 12));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        actionsPanel.setBackground(new Color(250, 250, 250));
        
        JButton btnAllocate = makeActionButton("Allocate Memory");
        JButton btnDeallocate = makeActionButton("Deallocate Memory");
        JButton btnViewProcess = makeActionButton("View Process Memory");
        JButton btnViewPageTable = makeActionButton("View Page Table");
        JButton btnViewFrames = makeActionButton("View Memory Frames");
        JButton btnMemoryStats = makeActionButton("Memory Statistics");
        JButton btnCompact = makeActionButton("Memory Compaction Info");
        JButton btnReset = makeActionButton("Reset Memory System");
        
        btnReset.setBackground(new Color(200, 100, 100));
        btnReset.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnReset.setBackground(new Color(170, 70, 70));
            }
            public void mouseExited(MouseEvent evt) {
                btnReset.setBackground(new Color(200, 100, 100));
            }
        });
        
        btnAllocate.addActionListener(e -> allocateMemoryToProcess());
        btnDeallocate.addActionListener(e -> deallocateMemoryFromProcess());
        btnViewProcess.addActionListener(e -> viewProcessMemoryDetails());
        btnViewPageTable.addActionListener(e -> viewPageTableWindow());
        btnViewFrames.addActionListener(e -> viewMemoryFramesWindow());
        btnMemoryStats.addActionListener(e -> viewMemoryStatistics());
        btnCompact.addActionListener(e -> showCompactionInfo());
        btnReset.addActionListener(e -> resetMemorySystem());
        
        actionsPanel.add(btnAllocate);
        actionsPanel.add(btnDeallocate);
        actionsPanel.add(btnViewProcess);
        actionsPanel.add(btnViewPageTable);
        actionsPanel.add(btnViewFrames);
        actionsPanel.add(btnMemoryStats);
        actionsPanel.add(btnCompact);
        actionsPanel.add(btnReset);
        actionsPanel.add(new JLabel());
        actionsPanel.add(new JLabel());
        actionsPanel.add(new JLabel());
        actionsPanel.add(new JLabel());
        actionsPanel.add(new JLabel());
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
    
    // ACTION: Allocate Memory to Process
    private void allocateMemoryToProcess() {
        ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
        ArrayList<ProcessControlBlock> availableProcesses = new ArrayList<>();
        
        // Only show processes without memory
        for (ProcessControlBlock pcb : processes) {
            if (!pcb.getProcessState().equals("TERMINATED") && 
                !memoryManager.hasMemory(pcb.getProcessID())) {
                availableProcesses.add(pcb);
            }
        }
        
        if (availableProcesses.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No processes available for memory allocation!\n" +
                "Either all processes have memory or no processes exist.", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] processOptions = new String[availableProcesses.size()];
        for (int i = 0; i < availableProcesses.size(); i++) {
            ProcessControlBlock pcb = availableProcesses.get(i);
            processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName() + " (" + pcb.getProcessState() + ")";
        }
        
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select process to allocate memory:",
            "Allocate Memory",
            JOptionPane.QUESTION_MESSAGE,
            null,
            processOptions,
            processOptions[0]
        );
        
        if (selected != null) {
            int pid = Integer.parseInt(selected.split(" - ")[0]);
            ProcessControlBlock pcb = processManager.getProcessByID(pid);
            
            String memoryStr = JOptionPane.showInputDialog(this, 
                "Enter memory requirement (KB):\n" +
                "Page Size: 4 KB\n" +
                "Available Frames: " + PhysicalMemory.getInstance().getFreeFrameCount(), 
                "Memory Requirement", 
                JOptionPane.PLAIN_MESSAGE);
            
            if (memoryStr != null && !memoryStr.trim().isEmpty()) {
                try {
                    int memoryKB = Integer.parseInt(memoryStr.trim());
                    if (memoryKB <= 0) {
                        JOptionPane.showMessageDialog(this, 
                            "Memory must be positive!", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    boolean success = memoryManager.allocateMemory(pid, memoryKB);
                    
                    if (success) {
                        pcb.setMemoryRequirements(memoryKB);
                        JOptionPane.showMessageDialog(this, 
                            "Memory allocated successfully!\n\n" +
                            "Process: " + pcb.getProcessName() + " (PID " + pid + ")\n" +
                            "Memory: " + memoryKB + " KB\n" +
                            "Pages: " + memoryManager.getPageTable(pid).getTotalPages(), 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Memory allocation failed!\n\n" +
                            "Insufficient memory available.\n" +
                            "Free Frames: " + PhysicalMemory.getInstance().getFreeFrameCount() + "\n" +
                            "Required: " + (int)Math.ceil(memoryKB / 4.0) + " frames", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid memory value!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    // ACTION: Deallocate Memory from Process
    private void deallocateMemoryFromProcess() {
        ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
        ArrayList<ProcessControlBlock> availableProcesses = new ArrayList<>();
        
        // Only show processes with memory
        for (ProcessControlBlock pcb : processes) {
            if (memoryManager.hasMemory(pcb.getProcessID())) {
                availableProcesses.add(pcb);
            }
        }
        
        if (availableProcesses.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No processes with allocated memory!", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] processOptions = new String[availableProcesses.size()];
        for (int i = 0; i < availableProcesses.size(); i++) {
            ProcessControlBlock pcb = availableProcesses.get(i);
            PageTable pt = memoryManager.getPageTable(pcb.getProcessID());
            processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName() + 
                " (" + pt.getAllocatedPages() + " pages, " + 
                (pt.getAllocatedPages() * 4) + " KB)";
        }
        
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select process to deallocate memory:",
            "Deallocate Memory",
            JOptionPane.QUESTION_MESSAGE,
            null,
            processOptions,
            processOptions[0]
        );
        
        if (selected != null) {
            int pid = Integer.parseInt(selected.split(" - ")[0]);
            ProcessControlBlock pcb = processManager.getProcessByID(pid);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Deallocate memory from process?\n\n" +
                "Process: " + pcb.getProcessName() + " (PID " + pid + ")\n" +
                "Memory: " + pcb.getMemoryRequirements() + " KB\n\n" +
                "This will free all allocated frames.",
                "Confirm Deallocation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                memoryManager.deallocateMemory(pid);
                pcb.setMemoryRequirements(0);
                
                JOptionPane.showMessageDialog(this, 
                    "Memory deallocated successfully!\n" +
                    "Frames returned to free pool.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    // ACTION: View Process Memory Details
    private void viewProcessMemoryDetails() {
        ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
        
        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No processes available!", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] processOptions = new String[processes.size()];
        for (int i = 0; i < processes.size(); i++) {
            ProcessControlBlock pcb = processes.get(i);
            processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
        }
        
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select process to view memory:",
            "View Process Memory",
            JOptionPane.QUESTION_MESSAGE,
            null,
            processOptions,
            processOptions[0]
        );
        
        if (selected != null) {
            int pid = Integer.parseInt(selected.split(" - ")[0]);
            ProcessControlBlock pcb = processManager.getProcessByID(pid);
            PageTable pt = memoryManager.getPageTable(pid);
            
            StringBuilder info = new StringBuilder();
            info.append("═══════════════════════════════════════\n");
            info.append("Process ID: ").append(pid).append("\n");
            info.append("Process Name: ").append(pcb.getProcessName()).append("\n");
            info.append("State: ").append(pcb.getProcessState()).append("\n");
            info.append("═══════════════════════════════════════\n\n");
            
            if (pt == null) {
                info.append("No memory allocated to this process.");
            } else {
                info.append("Memory Requirement: ").append(pcb.getMemoryRequirements()).append(" KB\n");
                info.append("Total Pages Required: ").append(pt.getTotalPages()).append("\n");
                info.append("Allocated Pages: ").append(pt.getAllocatedPages()).append("\n");
                info.append("Memory Allocated: ").append(pt.getAllocatedPages() * 4).append(" KB\n\n");
                
                info.append("Page Table Mappings:\n");
                info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                
                Map<Integer, Integer> mappings = pt.getPageMappings();
                if (mappings.isEmpty()) {
                    info.append("No page mappings found.\n");
                } else {
                    for (Map.Entry<Integer, Integer> entry : mappings.entrySet()) {
                        info.append(String.format("Virtual Page %2d → Physical Frame %3d\n", 
                            entry.getKey(), entry.getValue()));
                    }
                }
            }
            
            JTextArea textArea = new JTextArea(info.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(450, 350));
            
            JOptionPane.showMessageDialog(this, 
                scrollPane, 
                "Memory Details - " + pcb.getProcessName(), 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // ACTION: View Page Table Window
    private void viewPageTableWindow() {
        new PageTableViewWindow();
    }
    
    // ACTION: View Memory Frames Window
    private void viewMemoryFramesWindow() {
        new MemoryFramesWindow();
    }
    
    // ACTION: View Memory Statistics
    private void viewMemoryStatistics() {
        PhysicalMemory pm = PhysicalMemory.getInstance();
        
        StringBuilder stats = new StringBuilder();
        stats.append("═══════════════════════════════════════════════\n");
        stats.append("          MEMORY SYSTEM STATISTICS\n");
        stats.append("═══════════════════════════════════════════════\n\n");
        stats.append(String.format("Total Physical Memory: %d KB (1 MB)\n", pm.getTotalMemoryKB()));
        stats.append(String.format("Page/Frame Size: %d KB\n", pm.getPageSizeKB()));
        stats.append(String.format("Total Frames: %d\n\n", pm.getTotalFrames()));
        
        stats.append("Frame Status:\n");
        stats.append(String.format("  Used Frames: %d\n", pm.getUsedFrameCount()));
        stats.append(String.format("  Free Frames: %d\n", pm.getFreeFrameCount()));
        stats.append(String.format("  Memory Utilization: %.2f%%\n\n", pm.getMemoryUtilization()));
    stats.append("Allocation Statistics:\n");
stats.append(String.format("  Total Allocations: %d\n", pm.getTotalAllocations()));
stats.append(String.format("  Total Deallocations: %d\n", pm.getTotalDeallocations()));
stats.append(String.format("  Page Faults: %d\n", pm.getPageFaults()));
stats.append(String.format("  LRU Replacements: %d\n", pm.getLRUReplacements()));
stats.append(String.format("  Replacement Algorithm: %s\n\n", pm.getReplacementAlgorithm()));
        
        stats.append("Process Memory Usage:\n");
        Map<Integer, PageTable> tables = memoryManager.getProcessTables();
        if (tables.isEmpty()) {
            stats.append("  No processes with allocated memory\n");
        } else {
            for (Map.Entry<Integer, PageTable> entry : tables.entrySet()) {
                int pid = entry.getKey();
                PageTable pt = entry.getValue();
                ProcessControlBlock pcb = processManager.getProcessByID(pid);
                String name = pcb != null ? pcb.getProcessName() : "Unknown";
                stats.append(String.format("  PID %d (%s): %d pages, %d KB\n", 
                    pid, name, pt.getAllocatedPages(), pt.getAllocatedPages() * 4));
            }
        }
        
        stats.append("\n═══════════════════════════════════════════════");
        
        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, 
            scrollPane, 
            "Memory Statistics", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ACTION: Show Compaction Info
   private void showCompactionInfo() {
    PhysicalMemory pm = PhysicalMemory.getInstance();
    JOptionPane.showMessageDialog(this, 
        "Memory Compaction Information\n\n" +
        "In a paging system, external fragmentation is minimal\n" +
        "because memory is divided into fixed-size pages/frames.\n\n" +
        "Internal fragmentation may occur within the last page\n" +
        "of each process allocation.\n\n" +
        "Current Page Replacement Algorithm: " + pm.getReplacementAlgorithm() + "\n" +
        "(Replaces the page that has not been used for the longest time)\n\n" +
        "LRU Replacements Performed: " + pm.getLRUReplacements(),
            "Compaction Info", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ACTION: Reset Memory System
    private void resetMemorySystem() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "WARNING: This will reset the entire memory system!\n\n" +
            "All memory allocations will be cleared.\n" +
            "All processes will lose their memory.\n\n" +
            "Continue?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            PhysicalMemory.getInstance().reset();
            
            // Clear memory requirements from all processes
            for (ProcessControlBlock pcb : processManager.getAllProcesses()) {
                memoryManager.deallocateMemory(pcb.getProcessID());
                pcb.setMemoryRequirements(0);
            }
            
            JOptionPane.showMessageDialog(this, 
                "Memory system reset successfully!\n\n" +
                "All frames are now free.\n" +
                "All processes have zero memory.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

// Separate window for Page Table visualization
class PageTableViewWindow extends JFrame {
    private JTable pageTableView;
    
    public PageTableViewWindow() {
        setTitle("BETA OS - Page Table View");
        setSize(700, 500);
        setLocationRelativeTo(null);
        
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel("Process Page Tables");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        String[] columns = {"PID", "Process Name", "Virtual Page", "Physical Frame", "Memory (KB)"};
        pageTableView = new JTable(new Object[0][5], columns);
        pageTableView.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pageTableView.setRowHeight(25);
        pageTableView.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        pageTableView.getTableHeader().setBackground(new Color(90, 140, 220));
        pageTableView.getTableHeader().setForeground(Color.WHITE);
        
        updatePageTable();
        
        JScrollPane scrollPane = new JScrollPane(pageTableView);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(90, 140, 220));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());
        
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(closeBtn);
        
        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);
        
        add(wrapper);
        setVisible(true);
    }
    
    private void updatePageTable() {
        MemoryManager memoryManager = MemoryManager.getInstance();
        ProcessManager processManager = ProcessManager.getInstance();
        PhysicalMemory physicalMemory = PhysicalMemory.getInstance();
        
        Map<Integer, PageTable> tables = memoryManager.getProcessTables();
        List<Object[]> rows = new ArrayList<>();
        
        for (Map.Entry<Integer, PageTable> entry : tables.entrySet()) {
            int pid = entry.getKey();
            PageTable pt = entry.getValue();
            
            ProcessControlBlock pcb = processManager.getProcessByID(pid);
            String processName = pcb != null ? pcb.getProcessName() : "Unknown";
            
            Map<Integer, Integer> mappings = pt.getPageMappings();
            
            if (mappings.isEmpty()) {
                rows.add(new Object[]{pid, processName, "-", "-", "0 KB"});
            } else {
                for (Map.Entry<Integer, Integer> mapping : mappings.entrySet()) {
                    int vPage = mapping.getKey();
                    int pFrame = mapping.getValue();
                    int memKB = physicalMemory.getPageSizeKB();
                    
                    rows.add(new Object[]{pid, processName, vPage, pFrame, memKB + " KB"});
                }
            }
        }
        
        if (rows.isEmpty()) {
            rows.add(new Object[]{"—", "No allocations", "—", "—", "—"});
        }
        
        Object[][] data = rows.toArray(new Object[0][5]);
        String[] columns = {"PID", "Process Name", "Virtual Page", "Physical Frame", "Memory (KB)"};
        
        pageTableView.setModel(new javax.swing.table.DefaultTableModel(data, columns));
        pageTableView.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        pageTableView.getTableHeader().setBackground(new Color(90, 140, 220));
        pageTableView.getTableHeader().setForeground(Color.WHITE);
    }
}

// Separate window for Memory Frames visualization
class MemoryFramesWindow extends JFrame {
    private JPanel frameGridPanel;
    private Timer refreshTimer;
    
    public MemoryFramesWindow() {
        setTitle("BETA OS - Physical Memory Frames");
        setSize(800, 650);
        setLocationRelativeTo(null);
        
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel("Physical Memory Frames");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legendPanel.setBackground(Color.WHITE);
        
        JLabel freeLegend = new JLabel("  FREE  ");
        freeLegend.setOpaque(true);
        freeLegend.setBackground(new Color(220, 255, 220));
        freeLegend.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JLabel usedLegend = new JLabel("  USED  ");
        usedLegend.setOpaque(true);
        usedLegend.setBackground(new Color(255, 200, 200));
        usedLegend.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        legendPanel.add(new JLabel("Legend:"));
        legendPanel.add(freeLegend);
        legendPanel.add(usedLegend);
        
        frameGridPanel = new JPanel(new GridLayout(16, 16, 2, 2));
        frameGridPanel.setBackground(Color.WHITE);
        updateFrameVisualization();
        
        JScrollPane scrollPane = new JScrollPane(frameGridPanel);
        scrollPane.setPreferredSize(new Dimension(760, 500));
        
        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(90, 140, 220));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e-> {
refreshTimer.cancel();
dispose();
});
JPanel btnPanel = new JPanel();
    btnPanel.setBackground(Color.WHITE);
    btnPanel.add(closeBtn);
    
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBackground(Color.WHITE);
    topPanel.add(title, BorderLayout.NORTH);
    topPanel.add(legendPanel, BorderLayout.SOUTH);
    
    wrapper.add(topPanel, BorderLayout.NORTH);
    wrapper.add(scrollPane, BorderLayout.CENTER);
    wrapper.add(btnPanel, BorderLayout.SOUTH);
    
    add(wrapper);
    
    // Auto-refresh every 1 second
    refreshTimer = new Timer();
    refreshTimer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            SwingUtilities.invokeLater(() -> updateFrameVisualization());
        }
    }, 1000, 1000);
    
    setVisible(true);
}

private void updateFrameVisualization() {
    frameGridPanel.removeAll();
    
    PhysicalMemory physicalMemory = PhysicalMemory.getInstance();
    ProcessManager processManager = ProcessManager.getInstance();
    
    boolean[] frameTable = physicalMemory.getFrameTable();
    int[] frameOwner = physicalMemory.getFrameOwner();
    
    // Color map for different processes
    Map<Integer, Color> processColors = new HashMap<>();
    processColors.put(-1, new Color(220, 255, 220)); // Free = light green
    
    // Assign colors to processes
    Color[] colors = {
        new Color(255, 200, 200), // Light red
        new Color(200, 200, 255), // Light blue
        new Color(255, 255, 200), // Light yellow
        new Color(255, 220, 255), // Light purple
        new Color(200, 255, 255), // Light cyan
        new Color(255, 200, 255), // Light magenta
    };
    
    int colorIndex = 0;
    for (int owner : frameOwner) {
        if (owner != -1 && !processColors.containsKey(owner)) {
            processColors.put(owner, colors[colorIndex % colors.length]);
            colorIndex++;
        }
    }
    
    for (int i = 0; i < frameTable.length; i++) {
        JLabel frameLabel = new JLabel(String.valueOf(i), SwingConstants.CENTER);
        frameLabel.setOpaque(true);
        frameLabel.setFont(new Font("Monospaced", Font.PLAIN, 9));
        frameLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        if (frameTable[i]) {
            frameLabel.setBackground(processColors.get(frameOwner[i]));
            ProcessControlBlock pcb = processManager.getProcessByID(frameOwner[i]);
            String name = pcb != null ? pcb.getProcessName() : "Unknown";
            frameLabel.setToolTipText("Frame " + i + " - PID: " + frameOwner[i] + " (" + name + ")");
        } else {
            frameLabel.setBackground(processColors.get(-1));
            frameLabel.setToolTipText("Frame " + i + " - FREE");
        }
        
        frameGridPanel.add(frameLabel);
    }
    
    frameGridPanel.revalidate();
    frameGridPanel.repaint();
}
}