import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class project {

    private JFrame frame;
    private JTextField titleInputField;
    private JTextField dateInputField;
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JButton addButton, deleteButton, completeButton;
    private JLabel successLabel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                project window = new project();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public project() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("✨ Simple To-Do Application ✨");
        frame.setBounds(100, 100, 600, 450);
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
        taskLabel.setForeground(new Color(60, 60, 120));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        inputPanel.add(taskLabel, gbc);

        titleInputField = new JTextField();
        titleInputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.75;
        inputPanel.add(titleInputField, gbc);

        JLabel dueDateLabel = new JLabel("Due Date (YYYY-MM-DD):");
        dueDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dueDateLabel.setForeground(new Color(60, 60, 120));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.25;
        inputPanel.add(dueDateLabel, gbc);

        dateInputField = new JTextField();
        dateInputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.75;
        inputPanel.add(dateInputField, gbc);

        addButton = new JButton("🎯 Add New Task");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        addButton.setBackground(new Color(100, 149, 237));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        inputPanel.add(addButton, gbc);

        successLabel = new JLabel("", JLabel.CENTER);
        successLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        successLabel.setForeground(new Color(34, 139, 34));
        gbc.gridy = 3;
        inputPanel.add(successLabel, gbc);

        frame.add(inputPanel, BorderLayout.NORTH);

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setCellRenderer(new TaskCellRenderer());
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 150)),
                "Tasks", 0, 0, new Font("Segoe UI", Font.BOLD, 18), new Color(50, 50, 100)));
        scrollPane.setBackground(Color.WHITE);

        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(new Color(245, 245, 250));
        completeButton = new JButton("✅ Mark Completed");
        completeButton.setEnabled(false);
        completeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        completeButton.setBackground(new Color(60, 179, 113));
        completeButton.setForeground(Color.WHITE);
        completeButton.setFocusPainted(false);
        completeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        deleteButton = new JButton("🗑️ Delete Task");
        deleteButton.setEnabled(false);
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        actionPanel.add(completeButton);
        actionPanel.add(deleteButton);
        frame.add(actionPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            if (addTask()) {
                successLabel.setText("🎉 Task added successfully!");
            } else {
                successLabel.setText("");
            }
        });

        taskList.addListSelectionListener(e -> {
            boolean selected = !taskList.isSelectionEmpty();
            completeButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
        });

        completeButton.addActionListener(e -> {
            markTaskCompleted();
        });

        deleteButton.addActionListener(e -> {
            deleteTask();
        });
    }

    private boolean addTask() {
        String title = titleInputField.getText().trim();
        String dueDateStr = dateInputField.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Task title cannot be empty.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateStr, dateFormatter);
            if (dueDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(frame, "Due date cannot be in the past.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid date format. Please use YYYY-MM-DD.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Task newTask = new Task(title, dueDate);
        taskListModel.addElement(newTask);
        sortTasks();

        titleInputField.setText("");
        dateInputField.setText("");
        return true;
    }

    private void markTaskCompleted() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex >= 0) {
            Task task = taskListModel.get(selectedIndex);
            if (!task.isCompleted()) {
                task.setCompleted(true);
                taskList.repaint();
                JOptionPane.showMessageDialog(frame, "✅ Task marked as completed!");
                sortTasks();
            } else {
                JOptionPane.showMessageDialog(frame, "Task is already completed.");
            }
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex >= 0) {
            taskListModel.remove(selectedIndex);
        }
    }

    private void sortTasks() {
        java.util.List<Task> tasks = java.util.Collections.list(taskListModel.elements());
        tasks.sort((t1, t2) -> {
            if (t1.isCompleted() && !t2.isCompleted()) return 1;
            if (!t1.isCompleted() && t2.isCompleted()) return -1;
            return t1.getDueDate().compareTo(t2.getDueDate());
        });
        taskListModel.clear();
        for (Task t : tasks) {
            taskListModel.addElement(t);
        }
    }

    private void confirmExit() {
        int option = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            frame.dispose();
            System.exit(0);
        }
    }
}
