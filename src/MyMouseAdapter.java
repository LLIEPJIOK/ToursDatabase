import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

class MyMouseAdapter extends MouseAdapter {
    // staff for changing label text
    final String enteredText;
    final String exitedText = "Press Alt+x to exit";
    final JLabel label;

    // constructor
    MyMouseAdapter(String str, JLabel label) {
        this.enteredText = str;
        this.label = label;
    }

    // handle entering mouse
    @Override
    public void mouseEntered(MouseEvent e) {
        label.setText(enteredText);
    }

    // handle exiting mouse
    @Override
    public void mouseExited(MouseEvent e) {
        label.setText(exitedText);
    }

    // handle releasing mouse button
    @Override
    public void mouseReleased(MouseEvent e) {
        label.setText(exitedText);
    }
}
