package programmer.box.colorguesser;

import android.widget.CompoundButton;

/**
 * Created by Jacob on 12/12/17.
 */

public class CheckPowerMenuItem {

    private String title;
    private int textColor;
    private int backgroundColor;
    private CompoundButton.OnCheckedChangeListener listener;
    private boolean isChecked;

    public CheckPowerMenuItem(String title, int textColor, int backgroundColor, boolean isChecked, CompoundButton.OnCheckedChangeListener listener) {
        this.title = title;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.listener = listener;
        this.isChecked = isChecked;
    }
    // --- skipped setter and getter methods


    public String getTitle() {
        return title;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public CompoundButton.OnCheckedChangeListener getListener() {
        return listener;
    }
}
