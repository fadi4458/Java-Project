import javax.swing.*;
import java.awt.*;

public class TaskCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Task) {
            Task task = (Task) value;
            String text = task.getTitle() + " (Due: " + task.getDueDate() + ")";
            if (task.isCompleted()) {
                text += " ✔";
                setForeground(Color.GRAY);
            } else {
                setForeground(Color.BLACK);
            }
            setText(text);
        }
        return comp;
    }
}
