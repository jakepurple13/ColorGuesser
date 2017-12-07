package programmer.box.colorguesser;

import android.graphics.drawable.Drawable;

public class IconPowerMenuItem {

    private String title;
    private int textColor;
    private int backgroundColor;

    public IconPowerMenuItem(String title, int textColor, int backgroundColor) {
        this.title = title;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
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
}
