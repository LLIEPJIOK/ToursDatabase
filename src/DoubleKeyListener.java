import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

// listener for text field so user can write only real number
class DoubleKeyListener extends KeyAdapter {
    // pattern string
    final String patternString = "^(([1-9]\\d{0,5})|(0))(\\.\\d{0,2})?$";

    // handle input and writing only real number
    @Override
    public void keyPressed(KeyEvent e) {
        JTextField tf = (JTextField) e.getSource();
        char ch = (char) e.getKeyChar();
        if (Character.isISOControl(ch) || (tf.getText() + ch).matches(patternString)) {
            tf.setEditable(true);
        } else {
            tf.setEditable(false);
        }
    }
}