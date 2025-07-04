import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException
import java.util.*;

public class project {

    private JFrame frame;
    private JTextField titleInputField;
    private JTextField dateInputField;
    private JComboBox<String> priorityComboBox;
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JButton addButton, completeButton, deleteButton;
    private JLabel successLabel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                project window = new project();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Unexpected error occurred:\n" + e.getMessage(),
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public project() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("✨ To-Do Application with Priority ✨");
        frame.setBounds(100, 100, 650, 500);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(new BorderLayout(15, 15));
        frame.getContentPane().setBackground(new Color(245, 245, 250));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        JLabel titleLabel = new JLabel("My To-Do List", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 100));
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(230, 230, 250));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                BorderFactory.createLineBorder(new Color(150, 150, 220), 2, true)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel taskLabel = new JLabel("Task Title:");
        taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(taskLabel, gbc);

        titleInputField = new JTextField();
        titleInputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputPanel.add(titleInputField, gbc);

        JLabel dueDateLabel = new JLabel("Due Date (YYYY-MM-DD):");
        dueDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        inputPanel.add(dueDateLabel, gbc);

        dateInputField = new JTextField();
        dateInputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(dateInputField, gbc);

        JLabel priorityLabel = new JLabel("Priority:");
        priorityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        inputPanel.add(priorityLabel, gbc);

        priorityComboBox = new JComboBox<>(new String[]{"High", "Medium", "Low"});
        priorityComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        inputPanel.add(priorityComboBox, gbc);

        addButton = new JButton("🎯 Add Task");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        addButton.setBackground(new Color(100, 149, 237));
        addButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        inputPanel.add(addButton, gbc);

        successLabel = new JLabel("", JLabel.CENTER);
        successLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        successLabel.setForeground(new Color(34, 139, 34));
        gbc.gridy = 4;
        inputPanel.add(successLabel, gbc);

        frame.add(inputPanel, BorderLayout.NORTH);

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setCellRenderer(new TaskCellRenderer());
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 150)),
                "Tasks", 0, 0, new Font("Segoe UI", Font.BOLD, 18), new Color(50, 50, 100)));
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(new Color(245, 245, 250));

        completeButton = new JButton("✅ Complete");
        completeButton.setEnabled(false);
        completeButton.setBackground(new Color(60, 179, 113));
        completeButton.setForeground(Color.WHITE);

        deleteButton = new JButton("🗑️ Delete");
        deleteButton.setEnabled(false);
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);

        actionPanel.add(completeButton);
        actionPanel.add(deleteButton);
        frame.add(actionPanel, BorderLayout.SOUTH);

        // Listeners
        addButton.addActionListener(e -> {
            if (addTask()) {
                successLabel.setText("🎉 Task added!");
            }
        });

        taskList.addListSelectionListener(e -> {
            boolean selected = !taskList.isSelectionEmpty();
            completeButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
            successLabel.setText("");
        });

        completeButton.addActionListener(e -> markTaskCompleted());
        deleteButton.addActionListener(e -> deleteTask());
    }

    private boolean addTask() {
        String title = titleInputField.getText().trim();
        String dateStr = dateInputField.getText().trim();
        String priority = (String) priorityComboBox.getSelectedItem();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Title cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dateStr, dateFormatter);
            if (dueDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(frame, "Due date cannot be in the past.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(frame, "Invalid date format (YYYY-MM-DD).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        taskListModel.addElement(new Task(title, dueDate, priority));
        sortTasks();

        titleInputField.setText("");
        dateInputField.setText("");
        priorityComboBox.setSelectedIndex(0);
        return true;
    }

    private void markTaskCompleted() {
        int index = taskList.getSelectedIndex();
        if (index >= 0) {
            Task task = taskListModel.get(index);
            if (!task.isCompleted()) {
                int confirm = JOptionPane.showConfirmDialog(frame, "Mark this task as completed?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    task.setCompleted(true);
                    taskList.repaint();
                    sortTasks();
                    successLabel.setText("✅ Task completed.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Already completed.");
            }
        }
    }

    private void deleteTask() {
        int index = taskList.getSelectedIndex();
        if (index >= 0) {
            int confirm = JOptionPane.showConfirmDialog(frame, "Delete selected task?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                taskListModel.remove(index);
                successLabel.setText("🗑️ Task deleted.");
            }
        }
    }

    private void sortTasks() {
        List<Task> tasks = Collections.list(taskListModel.elements());
        tasks.sort(Comparator
                .comparing(Task::isCompleted)
                .thenComparing(Task::getPriorityLevel)
                .thenComparing(Task::getDueDate));

        taskListModel.clear();
        for (Task task : tasks) {
            taskListModel.addElement(task);
        }
    }

    private void confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            frame.dispose();
        }
    }

    // Task class with priority
    static class Task {
        private String title;
        private LocalDate dueDate;
        private boolean completed;
        private String priority;

        public Task(String title, LocalDate dueDate, String priority) {
            this.title = title;
            this.dueDate = dueDate;
            this.priority = priority;
            this.completed = false;
        }

        public String getTitle() {
            return title;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public String getPriority() {
            return priority;
        }

        public int getPriorityLevel() {
            switch (priority) {
                case "High": return 1;
                case "Medium": return 2;
                default: return 3;
            }
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        @Override
        public String toString() {
            return title + " | Due: " + dueDate + " | Priority: " + priority + (completed ? " ✅" : "");
        }
    }

    // Custom cell renderer
    static class TaskCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Task task) {
                label.setText(task.toString());
                if (task.isCompleted()) {
                    label.setForeground(Color.GRAY);
                    label.setFont(label.getFont().deriveFont(Font.ITALIC));
                } else {
                    label.setForeground(Color.BLACK);
                    label.setFont(label.getFont().deriveFont(Font.PLAIN));
                }
            }
            return label;
        }
    }
}
