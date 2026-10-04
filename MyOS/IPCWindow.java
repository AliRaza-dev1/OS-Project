import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Queue;

/**
 * IPCWindow - GUI for Phase 4.2: Inter-Process Communication
 */
class IPCWindow extends JFrame {
    private IPCManager ipcManager;
    private ProcessManager processManager;
    
    public IPCWindow() {
        ipcManager = IPCManager.getInstance();
        processManager = ProcessManager.getInstance();
        
        setTitle("BETA OS - Inter-Process Communication");
        setSize(680, 640);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(250, 250, 250));
        
        JPanel banner = new JPanel();
        banner.setBackground(new Color(60, 120, 170));
        banner.setPreferredSize(new Dimension(680, 55));
        
        JLabel bannerText = new JLabel("Inter-Process Communication (IPC)");
        bannerText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        bannerText.setForeground(Color.WHITE);
        banner.add(bannerText);
        
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new GridLayout(7, 2, 12, 12));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        actionsPanel.setBackground(new Color(250, 250, 250));
        
        // Message Passing buttons
        JButton btnSendMessage = makeActionButton("Send Message");
        JButton btnReceiveMessage = makeActionButton("Receive Message");
        JButton btnViewMessages = makeActionButton("View Mailboxes");
        
        // Shared Memory buttons
        JButton btnCreateSharedMem = makeActionButton("Create Shared Memory");
        JButton btnAttachSharedMem = makeActionButton("Attach to Shared Mem");
        JButton btnWriteSharedMem = makeActionButton("Write Shared Memory");
        JButton btnReadSharedMem = makeActionButton("Read Shared Memory");
        JButton btnViewSharedMem = makeActionButton("View Shared Memory");
        
        // Socket buttons
        JButton btnBindServer = makeActionButton("Bind Server Socket");
        JButton btnConnectSocket = makeActionButton("Connect Socket");
        JButton btnSendSocket = makeActionButton("Send via Socket");
        JButton btnViewSockets = makeActionButton("View Sockets");
        
        // RMI buttons
        JButton btnRegisterMethod = makeActionButton("Register RMI Method");
        JButton btnInvokeMethod = makeActionButton("Invoke RMI Method");
        JButton btnViewRMI = makeActionButton("View RMI Methods");
        
        btnSendMessage.addActionListener(e -> sendMessage());
        btnReceiveMessage.addActionListener(e -> receiveMessage());
        btnViewMessages.addActionListener(e -> viewMailboxes());
        
        btnCreateSharedMem.addActionListener(e -> createSharedMemory());
        btnAttachSharedMem.addActionListener(e -> attachSharedMemory());
        btnWriteSharedMem.addActionListener(e -> writeSharedMemory());
        btnReadSharedMem.addActionListener(e -> readSharedMemory());
        btnViewSharedMem.addActionListener(e -> viewSharedMemory());
        
        btnBindServer.addActionListener(e -> bindServer());
        btnConnectSocket.addActionListener(e -> connectSocket());
        btnSendSocket.addActionListener(e -> sendViaSocket());
        btnViewSockets.addActionListener(e -> viewSockets());
        
        btnRegisterMethod.addActionListener(e -> registerMethod());
        btnInvokeMethod.addActionListener(e -> invokeMethod());
        btnViewRMI.addActionListener(e -> viewRMI());
        
        actionsPanel.add(btnSendMessage);
        actionsPanel.add(btnReceiveMessage);
        actionsPanel.add(btnViewMessages);
        actionsPanel.add(btnCreateSharedMem);
        actionsPanel.add(btnAttachSharedMem);
        actionsPanel.add(btnWriteSharedMem);
        actionsPanel.add(btnReadSharedMem);
        actionsPanel.add(btnViewSharedMem);
        actionsPanel.add(btnBindServer);
        actionsPanel.add(btnConnectSocket);
        actionsPanel.add(btnSendSocket);
        actionsPanel.add(btnViewSockets);
        actionsPanel.add(btnRegisterMethod);
        actionsPanel.add(btnInvokeMethod);
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
    
    // ========== MESSAGE PASSING ==========
    
    private void sendMessage() {
        ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
        if (processes.size() < 2) {
            JOptionPane.showMessageDialog(this, "Need at least 2 processes!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] processOptions= new String[processes.size()];
for (int i = 0; i < processes.size(); i++) {
ProcessControlBlock pcb = processes.get(i);
processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
}
String sender = (String) JOptionPane.showInputDialog(this, "Select sender:", 
        "Send Message", JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]);
    if (sender == null) return;
    int senderPID = Integer.parseInt(sender.split(" - ")[0]);
    
    String receiver = (String) JOptionPane.showInputDialog(this, "Select receiver:", 
        "Send Message", JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]);
    if (receiver == null) return;
    int receiverPID = Integer.parseInt(receiver.split(" - ")[0]);
    
    String content = JOptionPane.showInputDialog(this, "Enter message content:", 
        "Send Message", JOptionPane.PLAIN_MESSAGE);
    if (content == null || content.trim().isEmpty()) return;
    
    String[] priorities = {"NORMAL", "HIGH", "LOW"};
    String priority = (String) JOptionPane.showInputDialog(this, "Select priority:", 
        "Send Message", JOptionPane.QUESTION_MESSAGE, null, priorities, priorities[0]);
    if (priority == null) priority = "NORMAL";
    
    boolean success = ipcManager.getMessageSystem().sendMessage(senderPID, receiverPID, content, priority);
    
    if (success) {
        JOptionPane.showMessageDialog(this, 
            String.format("Message sent from PID %d to PID %d", senderPID, receiverPID),
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}

private void receiveMessage() {
    ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
    if (processes.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No processes available!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String[] processOptions = new String[processes.size()];
    for (int i = 0; i < processes.size(); i++) {
        ProcessControlBlock pcb = processes.get(i);
        int msgCount = ipcManager.getMessageSystem().getMessageCount(pcb.getProcessID());
        processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName() + 
            " (" + msgCount + " messages)";
    }
    
    String selected = (String) JOptionPane.showInputDialog(this, "Select process:", 
        "Receive Message", JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]);
    if (selected == null) return;
    int pid = Integer.parseInt(selected.split(" - ")[0]);
    
    Message msg = ipcManager.getMessageSystem().receiveMessage(pid);
    
    if (msg == null) {
        JOptionPane.showMessageDialog(this, "No messages in mailbox!", 
            "Info", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, msg.toString(), 
            "Message Received", JOptionPane.INFORMATION_MESSAGE);
    }
}

private void viewMailboxes() {
    MessagePassingSystem msgSystem = ipcManager.getMessageSystem();
    
    StringBuilder info = new StringBuilder();
    info.append("═══════════════════════════════════════════════\n");
    info.append("          MESSAGE MAILBOXES\n");
    info.append("═══════════════════════════════════════════════\n\n");
    
    Map<Integer, Queue<Message>> mailboxes = msgSystem.getMailboxes();
    
    if (mailboxes.isEmpty()) {
        info.append("No mailboxes created yet.\n");
    } else {
        for (Map.Entry<Integer, Queue<Message>> entry : mailboxes.entrySet()) {
            int pid = entry.getKey();
            ProcessControlBlock pcb = processManager.getProcessByID(pid);
            String name = pcb != null ? pcb.getProcessName() : "Unknown";
            
            info.append(String.format("PID %d (%s): %d messages\n", 
                pid, name, entry.getValue().size()));
            
            for (Message msg : entry.getValue()) {
                info.append("  " + msg.toString() + "\n");
            }
            info.append("\n");
        }
    }
    
    JTextArea textArea = new JTextArea(info.toString());
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(600, 400));
    
    JOptionPane.showMessageDialog(this, scrollPane, 
        "Mailboxes", JOptionPane.INFORMATION_MESSAGE);
}

// ========== SHARED MEMORY ==========

private void createSharedMemory() {
    String name = JOptionPane.showInputDialog(this, "Enter shared memory name:", 
        "Create Shared Memory", JOptionPane.PLAIN_MESSAGE);
    if (name == null || name.trim().isEmpty()) return;
    
    String sizeStr = JOptionPane.showInputDialog(this, "Enter size (KB):", 
        "Create Shared Memory", JOptionPane.PLAIN_MESSAGE);
    if (sizeStr == null || sizeStr.trim().isEmpty()) return;
    
    try {
        int sizeKB = Integer.parseInt(sizeStr.trim());
        int segmentID = ipcManager.getSharedMemorySystem().createSegment(name, sizeKB);
        
        JOptionPane.showMessageDialog(this, 
            String.format("Shared memory '%s' created with ID: %d", name, segmentID),
            "Success", JOptionPane.INFORMATION_MESSAGE);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Invalid size!", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void attachSharedMemory() {
    SharedMemorySystem shmSystem = ipcManager.getSharedMemorySystem();
    Map<Integer, SharedMemorySegment> segments = shmSystem.getSegments();
    
    if (segments.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No shared memory segments available!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
    if (processes.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No processes available!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String[] processOptions = new String[processes.size()];
    for (int i = 0; i < processes.size(); i++) {
        ProcessControlBlock pcb = processes.get(i);
        processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
    }
    
    String selectedProc = (String) JOptionPane.showInputDialog(this, "Select process:", 
        "Attach Shared Memory", JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]);
    if (selectedProc == null) return;
    int pid = Integer.parseInt(selectedProc.split(" - ")[0]);
    
    String[] segmentOptions = new String[segments.size()];
    int idx = 0;
    for (SharedMemorySegment seg : segments.values()) {
        segmentOptions[idx++] = seg.getSegmentID() + " - " + seg.getName() + 
            " (" + seg.getSizeKB() + " KB)";
    }
    
    String selectedSeg = (String) JOptionPane.showInputDialog(this, "Select segment:", 
        "Attach Shared Memory", JOptionPane.QUESTION_MESSAGE, null, segmentOptions, segmentOptions[0]);
    if (selectedSeg == null) return;
    int segmentID = Integer.parseInt(selectedSeg.split(" - ")[0]);
    
    boolean success = shmSystem.attachToSegment(pid, segmentID);
    
    if (success) {
        JOptionPane.showMessageDialog(this, 
            String.format("Process %d attached to segment %d", pid, segmentID),
            "Success", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, "Failed to attach!", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void writeSharedMemory() {
    SharedMemorySystem shmSystem = ipcManager.getSharedMemorySystem();
    Map<Integer, SharedMemorySegment> segments = shmSystem.getSegments();
    
    if (segments.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No shared memory segments available!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
    if (processes.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No processes available!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String[] processOptions = new String[processes.size()];
    for (int i = 0; i < processes.size(); i++) {
        ProcessControlBlock pcb = processes.get(i);
        processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
    }
    
    String selectedProc = (String) JOptionPane.showInputDialog(this, "Select process:", 
        "Write Shared Memory", JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]);
    if (selectedProc == null) return;
    int pid = Integer.parseInt(selectedProc.split(" - ")[0]);
    
    String[] segmentOptions = new String[segments.size()];
    int idx = 0;
    for (SharedMemorySegment seg : segments.values()) {
        segmentOptions[idx++] = seg.getSegmentID() + " - " + seg.getName();
    }
    
    String selectedSeg = (String) JOptionPane.showInputDialog(this, "Select segment:", 
        "Write Shared Memory", JOptionPane.QUESTION_MESSAGE, null, segmentOptions, segmentOptions[0]);
    if (selectedSeg == null) return;
    int segmentID = Integer.parseInt(selectedSeg.split(" - ")[0]);
    
    String data = JOptionPane.showInputDialog(this, "Enter data to write:", 
        "Write Shared Memory", JOptionPane.PLAIN_MESSAGE);
    if (data == null) return;
    
    boolean success = shmSystem.writeToSegment(pid, segmentID, data);
    
    if (success) {
        JOptionPane.showMessageDialog(this, 
            String.format("Process %d wrote to segment %d", pid, segmentID),
            "Success", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, 
            "Failed! Process not attached to this segment.", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void readSharedMemory() {
    SharedMemorySystem shmSystem = ipcManager.getSharedMemorySystem();
    Map<Integer, SharedMemorySegment> segments = shmSystem.getSegments();
    
    if (segments.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No shared memory segments available!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
    if (processes.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No processes available!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String[] processOptions = new String[processes.size()];
    for (int i = 0; i < processes.size(); i++) {
        ProcessControlBlock pcb = processes.get(i);
        processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
    }
    
    String selectedProc = (String) JOptionPane.showInputDialog(this, "Select process:", 
        "Read Shared Memory", JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]);
    if (selectedProc == null) return;
    int pid = Integer.parseInt(selectedProc.split(" - ")[0]);
    
    String[] segmentOptions = new String[segments.size()];
    int idx = 0;
    for (SharedMemorySegment seg : segments.values()) {
        segmentOptions[idx++] = seg.getSegmentID() + " - " + seg.getName();
    }
    
    String selectedSeg = (String) JOptionPane.showInputDialog(this, "Select segment:", 
        "Read Shared Memory", JOptionPane.QUESTION_MESSAGE, null, segmentOptions, segmentOptions[0]);
    if (selectedSeg == null) return;
    int segmentID = Integer.parseInt(selectedSeg.split(" - ")[0]);
    
    String data = shmSystem.readFromSegment(pid, segmentID);
    
    if (data != null) {
        JOptionPane.showMessageDialog(this, 
            "Data: " + (data.isEmpty() ? "(empty)" : data),
            "Shared Memory Data", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, 
            "Failed! Process not attached to this segment.", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void viewSharedMemory() {
    SharedMemorySystem shmSystem = ipcManager.getSharedMemorySystem();
    Map<Integer, SharedMemorySegment> segments = shmSystem.getSegments();
    
    StringBuilder info = new StringBuilder();
    info.append("═══════════════════════════════════════════════\n");
    info.append("        SHARED MEMORY SEGMENTS\n");
    info.append("═══════════════════════════════════════════════\n\n");
    
    if (segments.isEmpty()) {
        info.append("No shared memory segments created.\n");
    } else {
        for (SharedMemorySegment seg : segments.values()) {
            info.append(seg.toString()).append("\n");
            info.append("  Data: ").append(seg.getData().isEmpty() ? "(empty)" : seg.getData()).append("\n");
            info.append("  Attached Processes: ");
            for (int pid : seg.getAttachedProcesses()) {
                ProcessControlBlock pcb = processManager.getProcessByID(pid);
                String name = pcb != null ? pcb.getProcessName() : "Unknown";
                info.append(String.format("PID %d (%s) ", pid, name));
            }
            info.append("\n\n");
        }
    }
    
    JTextArea textArea = new JTextArea(info.toString());
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(600, 400));
    
    JOptionPane.showMessageDialog(this, scrollPane, 
        "Shared Memory", JOptionPane.INFORMATION_MESSAGE);
}

// ========== SOCKETS ==========

private void bindServer() {
    ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
    if (processes.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No processes available!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String[] processOptions = new String[processes.size()];
    for (int i = 0; i < processes.size(); i++) {
        ProcessControlBlock pcb = processes.get(i);
        processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
    }
    
    String selected = (String) JOptionPane.showInputDialog(this, "Select server process:", 
        "Bind Server", JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]);
    if (selected == null) return;
    int serverPID = Integer.parseInt(selected.split(" - ")[0]);
    
    String portStr = JOptionPane.showInputDialog(this, "Enter port number (1024-65535):", 
        "Bind Server", JOptionPane.PLAIN_MESSAGE);
    if (portStr == null || portStr.trim().isEmpty()) return;
    
    try {
        int port = Integer.parseInt(portStr.trim());
        boolean success = ipcManager.getSocketSystem().bindServer(serverPID, port);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                String.format("Server PID %d bound to port %d", serverPID, port),
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Port already in use!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Invalid port number!", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void connectSocket() {
    SocketSystem socketSystem = ipcManager.getSocketSystem();
    Map<Integer, Integer> serverPorts = socketSystem.getServerPorts();
    
    if (serverPorts.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No servers available!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
    if (processes.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No processes available!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String[] processOptions = new String[processes.size()];
    for (int i = 0; i < processes.size(); i++) {
        ProcessControlBlock pcb = processes.get(i);
        processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
    }
    
    String selected = (String) JOptionPane.showInputDialog(this, "Select client process:", 
        "Connect Socket", JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]);
    if (selected == null) return;
    int clientPID = Integer.parseInt(selected.split(" - ")[0]);
    
    String[] portOptions = new String[serverPorts.size()];
    int idx = 0;
    for (Map.Entry<Integer, Integer> entry : serverPorts.entrySet()) {
        ProcessControlBlock serverPCB = processManager.getProcessByID(entry.getValue());
        String serverName = serverPCB != null ? serverPCB.getProcessName() : "Unknown";
        portOptions[idx++] = "Port " + entry.getKey() + " (Server: PID " + entry.getValue() + " - " + serverName + ")";
    }
    
    String selectedPort = (String) JOptionPane.showInputDialog(this, "Select server port:", 
        "Connect Socket", JOptionPane.QUESTION_MESSAGE, null, portOptions, portOptions[0]);
    if (selectedPort == null) return;
    int port = Integer.parseInt(selectedPort.split(" ")[1]);
    
    Integer connectionID = socketSystem.connectToServer(clientPID, port);
    
    if (connectionID != null) {
        JOptionPane.showMessageDialog(this, 
            String.format("Client PID %d connected to port %d\nConnection ID: %d", 
                clientPID, port, connectionID),
            "Success", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, "Connection failed!", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void sendViaSocket() {
    SocketSystem socketSystem = ipcManager.getSocketSystem();
    Map<Integer, SocketConnection> connections = socketSystem.getConnections();
    
    if (connections.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No active connections!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String[] connOptions = new String[connections.size()];
    int idx = 0;
    for (SocketConnection conn : connections.values()) {
        connOptions[idx++] = String.format("Conn %d: Port %d (Server:%d ↔ Client:%d)",
            conn.getConnectionID(), conn.getPort(), conn.getServerPID(), conn.getClientPID());
    }
    
    String selected = (String) JOptionPane.showInputDialog(this, "Select connection:", 
        "Send via Socket", JOptionPane.QUESTION_MESSAGE, null, connOptions, connOptions[0]);
    if (selected == null) return;
    int connID = Integer.parseInt(selected.split(" ")[1].replace(":", ""));
    
    SocketConnection conn = connections.get(connID);
    
    String[] senderOptions = {
        "Server (PID " + conn.getServerPID() + ")",
        "Client (PID " + conn.getClientPID() + ")"
    };
    
    String selectedSender = (String) JOptionPane.showInputDialog(this, "Select sender:", 
        "Send via Socket", JOptionPane.QUESTION_MESSAGE, null, senderOptions, senderOptions[0]);
    if (selectedSender == null) return;
    
    int senderPID = selectedSender.contains("Server") ? conn.getServerPID() : conn.getClientPID();
    
    String data = JOptionPane.showInputDialog(this, "Enter data to send:", 
        "Send via Socket", JOptionPane.PLAIN_MESSAGE);
    if (data == null || data.trim().isEmpty()) return;
    
    boolean success = socketSystem.sendData(connID, senderPID, data);
    
    if (success) {
        JOptionPane.showMessageDialog(this, "Data sent successfully!", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, "Failed to send data!", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void viewSockets() {
    SocketSystem socketSystem = ipcManager.getSocketSystem();
    Map<Integer, SocketConnection> connections = socketSystem.getConnections();
    Map<Integer, Integer> serverPorts = socketSystem.getServerPorts();
    
    StringBuilder info = new StringBuilder();
    info.append("═══════════════════════════════════════════════\n");
    info.append("          SOCKET CONNECTIONS\n");
    info.append("═══════════════════════════════════════════════\n\n");
    
    info.append("Bound Servers:\n");
    if (serverPorts.isEmpty()) {
        info.append("  None\n");
    } else {
        for (Map.Entry<Integer, Integer> entry : serverPorts.entrySet()) {
            ProcessControlBlock pcb = processManager.getProcessByID(entry.getValue());
            String name = pcb != null ? pcb.getProcessName() : "Unknown";
            info.append(String.format("  Port %d → PID %d (%s)\n", 
                entry.getKey(), entry.getValue(), name));
        }
    }
    
    info.append("\nActive Connections:\n");
    if (connections.isEmpty()) {
        info.append("  None\n");
    } else {
        for (SocketConnection conn : connections.values()) {
            info.append("  ").append(conn.toString()).append("\n");
            
            if (!conn.getDataExchangeLog().isEmpty()) {
                info.append("    Recent exchanges:\n");
                java.util.List<String> log = conn.getDataExchangeLog();
                int start = Math.max(0, log.size() - 3);  // Last 3 entries
                for (int i = start; i < log.size(); i++) {
                    info.append("      ").append(log.get(i)).append("\n");
                }
            }
            info.append("\n");
        }
    }
    
    JTextArea textArea = new JTextArea(info.toString());
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(600, 400));
    
    JOptionPane.showMessageDialog(this, scrollPane, 
        "Sockets", JOptionPane.INFORMATION_MESSAGE);
}

// ========== RMI ==========

private void registerMethod() {
    ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
    if (processes.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No processes available!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String[] processOptions = new String[processes.size()];
    for (int i = 0; i < processes.size(); i++) {
        ProcessControlBlock pcb = processes.get(i);
        processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
    }
    
    String selected = (String) JOptionPane.showInputDialog(this, "Select process:", 
        "Register Method", JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]);
    if (selected == null) return;
    int pid = Integer.parseInt(selected.split(" - ")[0]);
    
    String methodName = JOptionPane.showInputDialog(this, "Enter method name:", 
        "Register Method", JOptionPane.PLAIN_MESSAGE);
    if (methodName == null || methodName.trim().isEmpty()) return;
    
    String returnType = JOptionPane.showInputDialog(this, "Enter return type (e.g., int, String, void):", 
        "Register Method", JOptionPane.PLAIN_MESSAGE);
    if (returnType == null || returnType.trim().isEmpty()) returnType = "void";
    
    String params = JOptionPane.showInputDialog(this, "Enter parameters (comma-separated, e.g., int,String):", 
        "Register Method", JOptionPane.PLAIN_MESSAGE);
    
    java.util.List<String> paramList = new ArrayList<>();
    if (params != null && !params.trim().isEmpty()) {
        for (String param : params.split(",")) {
            paramList.add(param.trim());
        }
    }
    
    boolean success = ipcManager.getRMISystem().registerMethod(pid, methodName, returnType, paramList);
    
    if (success) {
        JOptionPane.showMessageDialog(this, 
            String.format("Method '%s' registered for PID %d", methodName, pid),
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}

private void invokeMethod() {
    RMISystem rmiSystem = ipcManager.getRMISystem();
    Map<Integer, Map<String, RemoteMethod>> allMethods = rmiSystem.getAllRemoteMethods();
    
    if (allMethods.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No remote methods registered!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    ArrayList<ProcessControlBlock> processes = processManager.getAllProcesses();
    if (processes.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No processes available!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String[] processOptions = new String[processes.size()];
    for (int i = 0; i < processes.size(); i++) {
        ProcessControlBlock pcb = processes.get(i);
        processOptions[i] = pcb.getProcessID() + " - " + pcb.getProcessName();
    }
    
    String caller = (String) JOptionPane.showInputDialog(this, "Select caller process:", 
        "Invoke Method", JOptionPane.QUESTION_MESSAGE, null, processOptions, processOptions[0]);
    if (caller == null) return;
    int callerPID = Integer.parseInt(caller.split(" - ")[0]);
    
    // Get processes with registered methods
    java.util.List<String> targetOptions = new ArrayList<>();
    for (Map.Entry<Integer, Map<String, RemoteMethod>> entry : allMethods.entrySet()) {
        if (!entry.getValue().isEmpty()) {
            ProcessControlBlock pcb = processManager.getProcessByID(entry.getKey());
            String name = pcb != null ? pcb.getProcessName() : "Unknown";
            targetOptions.add(entry.getKey() + " - " + name);
        }
    }
    
    if (targetOptions.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No processes with registered methods!", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String target = (String) JOptionPane.showInputDialog(this, "Select target process:", 
        "Invoke Method", JOptionPane.QUESTION_MESSAGE, null, 
        targetOptions.toArray(new String[0]), targetOptions.get(0));
    if (target == null) return;
    int targetPID = Integer.parseInt(target.split(" - ")[0]);
    
    Map<String, RemoteMethod> methods = allMethods.get(targetPID);
    String[] methodOptions = new String[methods.size()];
    int idx = 0;
    for (RemoteMethod method : methods.values()) {
        methodOptions[idx++] = method.toString();
    }
    
    String selectedMethod = (String) JOptionPane.showInputDialog(this, "Select method:", 
        "Invoke Method", JOptionPane.QUESTION_MESSAGE, null, methodOptions, methodOptions[0]);
    if (selectedMethod == null) return;
    
    String methodName = selectedMethod.split(" ")[1].split("\\(")[0];
    
    String params = JOptionPane.showInputDialog(this, "Enter parameters (comma-separated):", 
        "Invoke Method", JOptionPane.PLAIN_MESSAGE);
    
    java.util.List<String> paramList = new ArrayList<>();
    if (params != null && !params.trim().isEmpty()) {
        for (String param : params.split(",")) {
            paramList.add(param.trim());
        }
    }
    
   String result = rmiSystem.invokeMethod(callerPID, targetPID, methodName, paramList);

if (result != null) {
    JOptionPane.showMessageDialog(this, 
        "Method invoked successfully!\n\nResult: " + result,  // ← Added extra \n for spacing
        "Success", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, "Method invocation failed!", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void viewRMI() {
    RMISystem rmiSystem = ipcManager.getRMISystem();
    Map<Integer, Map<String, RemoteMethod>> allMethods = rmiSystem.getAllRemoteMethods();
    java.util.List<RMIInvocation> invocations = rmiSystem.getInvocationLog();
    
    StringBuilder info = new StringBuilder();
    info.append("═══════════════════════════════════════════════\n");
    info.append("     REMOTE METHODINVOCATION (RMI)\n");
info.append("═══════════════════════════════════════════════\n\n");
   info.append("Registered Methods:\n");
    if (allMethods.isEmpty()) {
        info.append("  None\n");
    } else {
        for (Map.Entry<Integer, Map<String, RemoteMethod>> entry : allMethods.entrySet()) {
            ProcessControlBlock pcb = processManager.getProcessByID(entry.getKey());
            String name = pcb != null ? pcb.getProcessName() : "Unknown";
            info.append(String.format("  PID %d (%s):\n", entry.getKey(), name));
            
            for (RemoteMethod method : entry.getValue().values()) {
                info.append("    ").append(method.toString()).append("\n");
            }
        }
    }
    
    info.append("\nInvocation History:\n");
    if (invocations.isEmpty()) {
        info.append("  No invocations yet\n");
    } else {
        int start = Math.max(0, invocations.size() - 10);  // Last 10 invocations
        for (int i = start; i < invocations.size(); i++) {
            info.append("  ").append(invocations.get(i).toString()).append("\n");
        }
    }
    
    JTextArea textArea = new JTextArea(info.toString());
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
    
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(650, 450));
    
    JOptionPane.showMessageDialog(this, scrollPane, 
        "RMI System", JOptionPane.INFORMATION_MESSAGE);
}
}