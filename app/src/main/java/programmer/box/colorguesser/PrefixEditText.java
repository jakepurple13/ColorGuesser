package programmer.box.colorguesser;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Jacob on 12/7/17.
 */

public class PrefixEditText extends android.support.v7.widget.AppCompatEditText {

    float mOriginalLeftPadding = -1;
    float mOriginalRightPadding = -1;

    private String prefix = "";
    private String suffix = "";

    boolean useCounter = false;

    private int counter = 0;
    int maxCount = 0;

    OnTextChanges textChanges = new OnTextChanges() {
        @Override
        public void afterTextChanged(Editable text) {

        }

        @Override
        public void onTextChanged(String text) {

        }

        @Override
        public void beforeTextChanged(String text) {

        }
    };

    public PrefixEditText(Context context) {
        super(context);
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textChanges.beforeTextChanged(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textChanges.onTextChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                textChanges.afterTextChanged(editable);
            }
        });
    }

    public PrefixEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributes(context, attrs, 0);
    }

    public PrefixEditText(Context context, AttributeSet attrs,
                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculatePrefix();
    }

    private void calculatePrefix() {
        if (mOriginalLeftPadding == -1) {
            String prefix = this.prefix;
            float[] widths = new float[prefix.length()];
            getPaint().getTextWidths(prefix, widths);
            float textWidth = 0;
            for (float w : widths) {
                textWidth += w;
            }

            mOriginalLeftPadding = getCompoundPaddingLeft();

            setPadding((int) (textWidth + mOriginalLeftPadding),
                    getPaddingRight(), getPaddingTop(),
                    getPaddingBottom());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(prefix, mOriginalLeftPadding, getLineBounds(0, null), getPaint());
        int xPos = (canvas.getWidth() / 2)+(canvas.getWidth() / 4);
        int yPos = (int) ((canvas.getHeight() / 2) - ((getPaint().descent() + getPaint().ascent()) / 2)) ;
        //canvas.drawText(counter + "/" + maxCount, getCompoundPaddingRight(), getLineBounds(0, null), getPaint());
        canvas.drawText(suffix, xPos, yPos, getPaint());
    }

    private void getAttributes(Context context, AttributeSet attrs, int defStyleAttr) {

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textChanges.beforeTextChanged(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textChanges.onTextChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                textChanges.afterTextChanged(editable);
            }
        });

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PrefixEditText, defStyleAttr, 0);
        if(a != null) {
            suffix = a.getString(R.styleable.PrefixEditText_suffix_text);
            if(suffix == null) {
                suffix = "";
            }
            prefix = a.getString(R.styleable.PrefixEditText_prefix_text);
            if(prefix == null) {
                prefix = "";
            }
        }
        a.recycle();
    }

    public void setTextChanges(OnTextChanges textChanges) {
        this.textChanges = textChanges;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public interface OnTextChanges {
        public void afterTextChanged(Editable text);
        public void onTextChanged(String text);
        public void beforeTextChanged(String text);
    }

}