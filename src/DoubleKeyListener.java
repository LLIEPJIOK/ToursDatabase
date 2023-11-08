import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;

// listener for text field so user can write only real number
class DoubleKeyListener implements KeyListener {
    // useless interface method
    @Override
    public void keyTyped(KeyEvent e) {
    }

    // handle input and writing only real number
    @Override
    public void keyPressed(KeyEvent e) {
        JTextField tf = (JTextField) e.getSource();
        char ch = (char) e.getKeyChar();
        if (Character.isISOControl(ch)
                || (tf.getText() + ch).matches("^(([1-9]\\d{0,5})|(0))(\\.\\d{0,2})?$")) {
            tf.setEditable(true);
        } else {
            tf.setEditable(false);
        }
    }

    // useless interface method
    @Override
    public void keyReleased(KeyEvent e) {
    }
}