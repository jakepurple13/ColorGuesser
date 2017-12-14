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
    private boolean isEnabled = true;

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

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setListener(CompoundButton.OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
