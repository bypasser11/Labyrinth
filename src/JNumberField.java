
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;

/**
 * JNumberField.java
 *
 * JTextField für die Eingabe von Ganz- oder Gleitkommazahlen.
 * 
 * @author Prof. Dr. C. Hentschel
 * @version 1.2 (2015-10-02)
 */
public class JNumberField extends JTextField implements KeyListener {
    // Property Variablen (können in Netbeans-Properties gesetzt werden)
    protected boolean ganzzahl;
    protected boolean unsigned;

    public JNumberField() {
        super();
        
        // Verarbeiten der KeyEvents!
        addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        char c = ke.getKeyChar();
        boolean okay = false; // Annahme: falsche Taste

        // Wurde Enter oder Backspace gedrückt?
        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_ENTER) {
            return;
        }
        
        // Ziffern und der Punkt (bei Gleitkommazahlen) sind erlaubt
        if (c >= '0' && c <= '9' || (ganzzahl == false && c == '.')) {
            okay = true;
        }
        
        // Ein Vorzeichen ist nur bei signed feldern erlaubt
        // Und dann nur am Anfang des Feldes!
        if (unsigned == false && (c == '+' || c == '-')) {
            if (getCaretPosition() == 0) {
                okay = true;
            }
            
            if (getSelectedText() != null && getSelectionStart() == 0) {
                okay = true;
            }
        }
        
        // Ein Punkt darf nur einmal im feld auftauchen
        if (okay && c == '.' && getText().contains(".")) {
            if (getSelectedText() == null || !getSelectedText().contains(".")) {
                okay = false;
            }
        }
        
        // Wenn nicht okay, dann Taste ignorieren und hupen
        if (!okay) {
            Toolkit.getDefaultToolkit().beep();
            ke.consume();
        }
        
        // Wenn zuviele zeichen im Feld, dann Taste ignorieren und hupen
        if (getCaretPosition() >= getColumns()) {
            Toolkit.getDefaultToolkit().beep();
            ke.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        return;
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        return;
    }

    /**
     * @return Ist das Feld nur für Ganzzahlen zu nutzen?
     */
    public boolean isGanzzahl() {
        return ganzzahl;
    }

    /**
     * @param Das Feld soll nur für Ganzzahlen genutzt werden
     */
    public void setGanzzahl(boolean ganzzahl) {
        this.ganzzahl = ganzzahl;
    }

    /**
     * @return Soll das Feld nur für positive Zahlen (ohne Vorzeichen) genutzt werden?
     */
    public boolean isUnsigned() {
        return unsigned;
    }

    /**
     * @param Das Feld soll nur für positive Zahlen (ohne Vorzeichen) genutzt werden
     */
    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }


}
