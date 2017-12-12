package programmer.box.colorguesser;

import android.app.Dialog;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.majeur.cling.Cling;
import com.majeur.cling.ClingManager;
import com.majeur.cling.ViewTarget;
import com.rtugeek.android.colorseekbar.ColorSeekBar;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.utils.SpotlightSequence;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;
import org.ankit.perfectdialog.EasyDialog;
import org.ankit.perfectdialog.EasyDialogListener;
import org.ankit.perfectdialog.Icon;

import java.util.Random;

import io.kimo.konamicode.KonamiCode;
import io.kimo.konamicode.KonamiCodeLayout;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import programmer.box.utilityhelper.UtilDevice;
import programmer.box.utilityhelper.UtilImage;
import programmer.box.utilityhelper.UtilLog;
import programmer.box.utilityhelper.UtilNotification;
import programmer.box.utilityhelper.UtilPreferences;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {

    //the guess button
    Button guess;
    //R
    EditText rValue;
    //G
    EditText gValue;
    //B
    EditText bValue;
    //Hex
    PrefixEditText hexValue;
    //C
    EditText cValue;
    //M
    EditText mValue;
    //Y
    EditText yValue;
    //K
    EditText kValue;
    //A button to clear cmyk fields
    ImageButton cmykClear;
    //A button to clear hex field
    ImageButton hexClear;
    //A button to clear rgb fields
    ImageButton rgbClear;
    //the score
    TextView score;
    //the background
    RelativeLayout layout;
    //background for cmyk
    RelativeLayout cmyk;
    //background for rgb
    RelativeLayout rgb;
    //background for hex
    RelativeLayout hex;
    //settings button
    FloatingActionButton fab;
    //random generator
    Random gen = new Random();
    //the current color
    int currentColor;
    //the current score
    int currentScore = 0;
    //configuration for tutorial spotlight
    SpotlightConfig config;
    //CONFETTI!
    KonfettiView konfettiView;
    //RGB max value
    final static int RGB_MAX = 255;
    //CMYK max value
    final static int CMYK_MAX = 100;
    //cheat view
    TextView cheat;
    //preset colors
    Spinner presetColor;
    //the actual color that is being guessed
    CircleImageView colorToGuess;
    //a seek bar for many colors
    ColorSeekBar colorSeekBar;
    //super cheat variable that makes score halved
    boolean superCheatMode = false;
    //custom settings menu
    CustomPowerMenu settingsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        //initialize UtilPreferences
        UtilPreferences.init(this);

        //Cheat code setup--------------
        new KonamiCode.Installer(this)
                .on(this)
                .callback(new KonamiCodeLayout.Callback() {
                    @Override
                    public void onFinish() {
                        //whatever
                        cheat.setVisibility(View.VISIBLE);
                        UtilNotification.showToast(MainActivity.this, "Super Color Mode Activated!", UtilNotification.Lengths.Short);
                        //tutorial if its the first time
                        SpotlightSequence sequence = SpotlightSequence.getInstance(MainActivity.this, config);
                        sequence.addSpotlight(cheat, "Cheat Mode", "You found my easter egg!\n\nGood job!\n\nThis allows you to see the answer! But they might not be exactly what it shows. Good luck!", "cheat", false);
                        sequence.startSequence();
                        //set cheat mode
                        UtilPreferences.put("cheat_mode", true);
                    }
                })
                .install();
        //custom directions
        KonamiCodeLayout.Direction direction[] = new KonamiCodeLayout.Direction[]{KonamiCodeLayout.Direction.UP,
                KonamiCodeLayout.Direction.DOWN, KonamiCodeLayout.Direction.LEFT, KonamiCodeLayout.Direction.RIGHT};
        //custom buttons
        KonamiCodeLayout.Button buttons[] = new KonamiCodeLayout.Button[]{KonamiCodeLayout.Button.NONE};

        new KonamiCode.Installer(this)
                .on(this)
                .callback(new KonamiCodeLayout.Callback() {
                    @Override
                    public void onFinish() {
                        colorSeekBar.setVisibility(View.VISIBLE);
                        superCheatMode = true;
                        UtilNotification.showToast(MainActivity.this, "Custom Mode Activated!", UtilNotification.Lengths.Short);

                        SpotlightSequence sequence = SpotlightSequence.getInstance(MainActivity.this, config);
                        sequence.addSpotlight(colorSeekBar, "Custom Mode", "You found my easter egg!\n\nGood job!\n\nThis mode allows you to change the color to your choosing!", "colorseekbar", false);
                        sequence.startSequence();

                        UtilPreferences.put("custom_mode", true);

                        new KonamiCode.Installer(MainActivity.this)
                                .on(MainActivity.this)
                                .callback(new KonamiCodeLayout.Callback() {
                                    @Override
                                    public void onFinish() {
                                        UtilNotification.showToast(MainActivity.this, "Super Custom Mode Activated!", UtilNotification.Lengths.Short);
                                        superCheatMode = true;
                                        UtilPreferences.put("super_cheat_mode", true);

                                        SpotlightSequence sequence = SpotlightSequence.getInstance(MainActivity.this, config);
                                        sequence.addSpotlight(colorSeekBar, "Super Custom Mode", "You found my easter egg!\n\nGood job!\n\nThis mode pretty much gives you the right answer at a cost", "supercheatmode", false);
                                        sequence.startSequence();
                                    }
                                })
                                .install(new KonamiCodeLayout.Direction[]{KonamiCodeLayout.Direction.RIGHT,
                                        KonamiCodeLayout.Direction.LEFT, KonamiCodeLayout.Direction.DOWN,
                                        KonamiCodeLayout.Direction.UP},
                                        new KonamiCodeLayout.Button[]{KonamiCodeLayout.Button.NONE});

                    }
                })
                .install(direction, buttons);

        cheat = findViewById(R.id.cheatMode);

        //this is all to set up the guessing color
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        colorToGuess = findViewById(R.id.profile_image);
        colorToGuess.getLayoutParams().width=(width/2);
        colorToGuess.requestLayout();

        //this is the color slider cheat
        colorSeekBar = findViewById(R.id.colorSlider);

        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                reset(color);
                if(superCheatMode) {
                    setPresets(color);
                }

            }
        });

        colorSeekBar.setBackgroundResource(R.drawable.rounded_corners);
        colorSeekBar.setPadding(5,5,5,5);
        colorSeekBar.setBarHeight(5);
        colorSeekBar.setBarMargin(5);

        //these are to quick clear fields
        rgbClear = findViewById(R.id.clear_rgb);
        hexClear = findViewById(R.id.clear_hex);
        cmykClear = findViewById(R.id.clear_cmyk);

        rgbClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rValue.setText("");
                gValue.setText("");
                bValue.setText("");
            }
        });

        hexClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hexValue.setText("");
            }
        });

        cmykClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cValue.setText("");
                mValue.setText("");
                yValue.setText("");
                kValue.setText("");
            }
        });

        //this is color presets
        presetColor = findViewById(R.id.preset_color);

        final String[] colors = getResources().getStringArray(R.array.colorNames);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.colorNames, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        presetColor.setAdapter(adapter);

        presetColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                UtilLog.e(colors[i]);

                int color;

                if(colors[i].equals("Color Presets")) {
                    color = Color.TRANSPARENT;
                    presetColor.getBackground().setTint(Color.WHITE);
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                } else {
                    //color = Color.parseColor(colors[i]);
                    TypedArray ta = getResources().obtainTypedArray(R.array.colors);
                    color = ta.getColor(i-1, 0);
                    ta.recycle();
                    presetColor.getBackground().setTint(color);
                    ((TextView) adapterView.getChildAt(0)).setTextColor(getComplimentaryColor(color));
                }

                UtilLog.e(color + " is the color " + colors[i]);

                //create two drawable files, one is the border, other is the background. Give lin border

                setPresets(color);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                setPresets(Color.TRANSPARENT);
            }
        });

        //this is a text watcher to dynamically change rgb field color
        TextWatcher rgbWatch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                try {

                    String r = rValue.getText().toString();
                    int r1;

                    if(r.equals("")) {
                        r1 = 0;
                    } else {
                        r1 = Integer.parseInt(r);
                    }

                    String g = gValue.getText().toString();
                    int g1;

                    if(g.equals("")) {
                        g1 = 0;
                    } else {
                        g1 = Integer.parseInt(g);
                    }

                    String b = bValue.getText().toString();
                    int b1;

                    if(b.equals("")) {
                        b1 = 0;
                    } else {
                        b1 = Integer.parseInt(b);
                    }
                    
                    int color = Color.rgb(r1,g1,b1);

                    if(b.equals("") && g.equals("") && r.equals("")) {
                        color = Color.WHITE;
                    }

                    int comColor = getComplimentaryColor(color);
                    int hintComColor = (comColor & 0x00FFFFFF) | 0x50000000;
                    rgb.getBackground().setTint(color);
                    rValue.setTextColor(comColor);
                    gValue.setTextColor(comColor);
                    bValue.setTextColor(comColor);
                    rValue.setHintTextColor(hintComColor);
                    gValue.setHintTextColor(hintComColor);
                    bValue.setHintTextColor(hintComColor);
                } catch(StringIndexOutOfBoundsException|IllegalArgumentException e) {
                    rgb.getBackground().setTint(Color.WHITE);
                    rValue.setTextColor(Color.BLACK);
                    gValue.setTextColor(Color.BLACK);
                    bValue.setTextColor(Color.BLACK);
                }
            }
        };
        
        rValue = findViewById(R.id.r_value);
        gValue = findViewById(R.id.g_value);
        bValue = findViewById(R.id.b_value);

        rValue.addTextChangedListener(rgbWatch);
        gValue.addTextChangedListener(rgbWatch);
        bValue.addTextChangedListener(rgbWatch);

        //hex value field
        hexValue = findViewById(R.id.hex_value);

        hexValue.setTextChanges(new PrefixEditText.OnTextChanges() {
            @Override
            public void afterTextChanged(Editable text) {

                hexValue.setSuffix(text.toString().length() + "/" + 6);

                try {
                    int color = Color.parseColor("#" + text.toString());
                    //hexValue.getBackground().setTint(color);
                    hex.getBackground().setTint(color);
                    hexValue.setTextColor(getComplimentaryColor(color));
                } catch(StringIndexOutOfBoundsException|IllegalArgumentException e) {
                    //hexValue.getBackground().setTint(Color.WHITE);
                    hex.getBackground().setTint(Color.WHITE);
                    hexValue.setTextColor(Color.BLACK);
                }

            }

            @Override
            public void onTextChanged(String text) {

            }

            @Override
            public void beforeTextChanged(String text) {

            }
        });

        //this is a text watcher to dynamically change cmyk field color
        TextWatcher cmykWatch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                try {

                    String c = cValue.getText().toString();
                    int c1;

                    if(c.equals("")) {
                        c1 = 0;
                    } else {
                        c1 = Integer.parseInt(c);
                    }

                    String m = mValue.getText().toString();
                    int m1;

                    if(m.equals("")) {
                        m1 = 0;
                    } else {
                        m1 = Integer.parseInt(m);
                    }

                    String y = yValue.getText().toString();
                    int y1;

                    if(y.equals("")) {
                        y1 = 0;
                    } else {
                        y1 = Integer.parseInt(y);
                    }

                    String k = kValue.getText().toString();
                    int k1;

                    if(k.equals("")) {
                        k1 = 0;
                    } else {
                        k1 = Integer.parseInt(k);
                    }

                    int[] cmykVal = getRGBFromCMYK(c1,m1,y1,k1);
                    int color = Color.rgb(cmykVal[0], cmykVal[1], cmykVal[2]);

                    int comColor = getComplimentaryColor(color);

                    cmyk.getBackground().setTint(color);
                    cValue.setTextColor(comColor);
                    mValue.setTextColor(comColor);
                    yValue.setTextColor(comColor);
                    kValue.setTextColor(comColor);
                } catch(StringIndexOutOfBoundsException|IllegalArgumentException e) {
                    cmyk.getBackground().setTint(Color.WHITE);
                    cValue.setTextColor(Color.BLACK);
                    mValue.setTextColor(Color.BLACK);
                    yValue.setTextColor(Color.BLACK);
                    kValue.setTextColor(Color.BLACK);
                    //e.printStackTrace();
                }
            }
        };

        cValue = findViewById(R.id.c_value);
        mValue = findViewById(R.id.m_value);
        yValue = findViewById(R.id.y_value);
        kValue = findViewById(R.id.k_value);

        cValue.addTextChangedListener(cmykWatch);
        mValue.addTextChangedListener(cmykWatch);
        yValue.addTextChangedListener(cmykWatch);
        kValue.addTextChangedListener(cmykWatch);

        //the background for the fields
        cmyk = findViewById(R.id.cmyk_view);
        rgb = findViewById(R.id.rgb_view);
        hex = findViewById(R.id.hex_view);

        //confetti for if the user guesses 3 fields correct
        konfettiView = findViewById(R.id.viewKonfetti);

        //current score set up
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        currentScore = sharedPref.getInt("score", 0);

        score = findViewById(R.id.score);
        score.setTextSize(20);
        score.setText("Score: " + currentScore);

        //full background
        layout = findViewById(R.id.background);

        //default spotlight config
        config = new SpotlightConfig();
        config.setIntroAnimationDuration(400);
        config.setRevealAnimationEnabled(true);
        config.setPerformClick(true);
        config.setFadingTextDuration(400);
        config.setHeadingTvColor(Color.parseColor("#eb273f"));
        config.setHeadingTvSize(32);
        config.setSubHeadingTvSize(16);
        config.setSubHeadingTvColor(Color.parseColor("#ffffff"));
        config.setMaskColor(Color.parseColor("#dc000000"));
        config.setLineAnimationDuration(400);
        config.setLineAndArcColor(Color.parseColor("#eb273f"));
        config.setDismissOnTouch(true);
        config.setDismissOnBackpress(true);

        //the guess button!
        guess = findViewById(R.id.guess);

        guess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //the score that will be added
                int addedScore = 0;
                //so the user can see how many points they earned in each category
                int hexPoints = 0;
                int rgbPoints = 0;
                int cmykPoints = 0;

                int color = currentColor;
                //the rgb of the current color
                int A = (color >> 24) & 0xff; // or color >>> 24
                int RCol = (color >> 16) & 0xff;
                int G = (color >>  8) & 0xff;
                int B = (color      ) & 0xff;

                //------RGB-----

                String rGuessed = rValue.getText().toString();
                String gGuessed = gValue.getText().toString();
                String bGuessed = bValue.getText().toString();
                //if any of the fields are empty, no points...no matter what
                if(!rGuessed.equals("") && !gGuessed.equals("") && !bGuessed.equals("")) {

                    int r = Integer.parseInt(rGuessed);
                    int g = Integer.parseInt(gGuessed);
                    int b = Integer.parseInt(bGuessed);

                    int rScore = getScore(RCol, r);
                    int gScore = getScore(G, g);
                    int bScore = getScore(B, b);

                    UtilLog.w("RS: " + rScore + "|r " + r + "|RCol " + RCol);
                    UtilLog.w("GS: " + gScore + "|g " + g + "|G " + G);
                    UtilLog.w("BS: " + bScore + "|b " + b + "|B " + B);
                    UtilLog.i(Color.red(color) + " is the red");
                    UtilLog.i(Color.green(color) + " is the green");
                    UtilLog.i(Color.blue(color) + " is the blue");

                    //if all three are correct, CONFETTI!!!
                    if(rScore==RGB_MAX && gScore==RGB_MAX && bScore==RGB_MAX) {
                        confetti(Color.RED, Color.GREEN, Color.BLUE);
                    }

                    rgbPoints = rScore+gScore+bScore;
                    if(superCheatMode)
                        rgbPoints/=4;
                    addedScore+=rgbPoints;

                }

                //----HEX----

                String hexGuess = hexValue.getText().toString();

                try {
                    //if the hex field is empty, ZERO points!
                    if (!hexGuess.equals("")) {
                        //parse the color
                        int c = Color.parseColor("#" + hexGuess);
                        //get the rgb values for it
                        int r = (c >> 16) & 0xff;
                        int g = (c >> 8) & 0xff;
                        int b = (c) & 0xff;

                        int rScore = getScore(RCol, r);
                        int gScore = getScore(G, g);
                        int bScore = getScore(B, b);
                        //if its perfect, CONFETTI!
                        if(rScore==RGB_MAX && gScore==RGB_MAX && bScore==RGB_MAX) {
                            confetti(Color.RED, Color.GREEN, Color.BLUE);
                        }

                        hexPoints = rScore + gScore + bScore;
                        if(superCheatMode)
                            hexPoints/=4;
                        addedScore += hexPoints;

                    }

                } catch(NumberFormatException e) {
                    addedScore+=0;
                }


                //-----CMYK-----

                String cGuessed = cValue.getText().toString();
                String mGuessed = mValue.getText().toString();
                String yGuessed = yValue.getText().toString();
                String kGuessed = kValue.getText().toString();
                //this is all to get the CMYK values. Taken from online.
                //http://www.javascripter.net/faq/hex2cmyk.htm
                double computedC = 1 - (Double.parseDouble(RCol+"") / RGB_MAX);
                double computedM = 1 - (Double.parseDouble(G+"") / RGB_MAX);
                double computedY = 1 - (Double.parseDouble(B+"") / RGB_MAX);

                double minCMY = Math.min(computedC, Math.min(computedM, computedY));

                computedC = (computedC - minCMY) / (1 - minCMY);
                computedM = (computedM - minCMY) / (1 - minCMY);
                computedY = (computedY - minCMY) / (1 - minCMY);
                double computedK = minCMY;
                //the CMYK values
                int C = (int) (computedC*100);
                int M = (int) (computedM*100);
                int Y = (int) (computedY*100);
                int K = (int) (computedK*100);
                //if any of the fields are empty, no points
                if(!cGuessed.equals("") && !mGuessed.equals("") && !yGuessed.equals("") && !kGuessed.equals("")) {

                    double c = Double.parseDouble(cGuessed);
                    double m = Double.parseDouble(mGuessed);
                    double y = Double.parseDouble(yGuessed);
                    double k = Double.parseDouble(kGuessed);
                    //making sure everything is within the right values
                    if((c<=100 || c>=0) && (m<=100 || m>=0) && (y<=100 || y>=0) && (k<=100 || k>=0)) {

                        double cs = getCMYKScore(C, c);
                        double ms = getCMYKScore(M, m);
                        double ys = getCMYKScore(Y, y);
                        double ks = getCMYKScore(K, k);
                        //if everything is perfect, CONFETTI!
                        if(cs==CMYK_MAX && ms==CMYK_MAX && ys==CMYK_MAX && ks==CMYK_MAX) {
                            confetti(Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.BLACK);
                        }

                        cmykPoints = (int) (cs + ms + ys + ks);
                        if(superCheatMode)
                            cmykPoints/=4;
                        addedScore += cmykPoints;

                    } else {
                        addedScore+=0;
                    }

                }

                currentScore+=addedScore;

                //------Add Scores------

                score.setText(getString(R.string.scores, currentScore));

                //the correct answer info
                String checkedMark = "\u2713";
                String xMark = "X";
                //the results for hex value
                String hexInfo = "Hex: #" + Integer.toHexString(currentColor).substring(2) + "\t"
                        + ((Integer.toHexString(currentColor).substring(2)).equals(hexGuess) ? checkedMark : xMark);// + "\t" + hexGuess;
                //the results for rgb
                String rInfo = "R: " + RCol + "\t" + ((RCol+"").equals(rGuessed) ? checkedMark : xMark);// + "\t" + rGuessed;
                String gInfo = "G: " + G + "\t" + ((G+"").equals(gGuessed) ? checkedMark : xMark);// + "\t" + gGuessed;
                String bInfo = "B: " + B + "\t" + ((B+"").equals(bGuessed) ? checkedMark : xMark);// + "\t" + bGuessed;
                //some housekeeping
                String rgbInfo = rInfo+"\n"+gInfo+"\n"+bInfo;
                //the results for cmyk
                String cInfo = "C: " + C + "\t" + ((C+"").equals(cGuessed) ? checkedMark : xMark);// + "\t" + cGuessed;
                String mInfo = "M: " + M + "\t" + ((M+"").equals(mGuessed) ? checkedMark : xMark);// + "\t" + mGuessed;
                String yInfo = "Y: " + Y + "\t" + ((Y+"").equals(yGuessed) ? checkedMark : xMark);// + "\t" + yGuessed;
                String kInfo = "K: " + K + "\t" + ((K+"").equals(kGuessed) ? checkedMark : xMark);// + "\t" + kGuessed;
                //more housekeeping
                String cmykInfo = cInfo+"\n"+mInfo+"\n"+yInfo+"\n"+kInfo;
                //points scored
                String scoreInfo = "Points Scored: " + addedScore;
                //user guessed info
                //user hex guess
                String hexGuessedInfo = "Hex: #" + (!hexGuess.equals("") ? hexGuess : "ffffff");
                int hexGuessedColor;

                try {
                    hexGuessedColor = Color.parseColor("#" + hexGuess);
                } catch (NumberFormatException e) {
                    hexGuessedColor = Color.WHITE;
                }
                //user rgb guess
                String rGuessInfo = (!rGuessed.equals("") ? rGuessed : "0");
                String gGuessInfo = (!gGuessed.equals("") ? gGuessed : "0");
                String bGuessInfo = (!bGuessed.equals("") ? bGuessed : "0");
                String rgbGuessedInfo = "R: " + rGuessInfo + "\nG: " + gGuessInfo + "\nB: " + bGuessInfo;
                int rgbGuessedColor;

                try {
                    rgbGuessedColor = Color.rgb(Integer.parseInt(rGuessed), Integer.parseInt(gGuessed), Integer.parseInt(bGuessed));
                } catch (NumberFormatException e) {
                    rgbGuessedColor = Color.WHITE;
                }
                //user cmyk guess
                String cGuessInfo = (!cGuessed.equals("") ? cGuessed : "0");
                String mGuessInfo = (!mGuessed.equals("") ? mGuessed : "0");
                String yGuessInfo = (!yGuessed.equals("") ? yGuessed : "0");
                String kGuessInfo = (!kGuessed.equals("") ? kGuessed : "0");
                String cmykGuessedInfo = "C: " + cGuessInfo + "\nM: " + mGuessInfo + "\nY: " + yGuessInfo + "\nK: " + kGuessInfo;
                int cmykGuessedColor;

                try {
                    int[] rgbs = getRGBFromCMYK(Integer.parseInt(cGuessed),
                            Integer.parseInt(mGuessed),
                            Integer.parseInt(yGuessed),
                            Integer.parseInt(kGuessed));
                    cmykGuessedColor = Color.rgb(rgbs[0], rgbs[1], rgbs[2]);
                } catch (NumberFormatException e) {
                    cmykGuessedColor = Color.WHITE;
                }
                //point info
                String hexPointInfo = hexPoints + " points";
                String rgbPointInfo = rgbPoints + " points";
                String cmykPointInfo = cmykPoints + " points";

                //user results menu
                CustomPowerMenu customPowerMenu = new CustomPowerMenu.Builder<>(MainActivity.this, new IconMenuAdapter())
                        .addItem(new IconPowerMenuItem(hexInfo, getComplimentaryColor(currentColor), currentColor))
                        .addItem(new IconPowerMenuItem(hexGuessedInfo, getComplimentaryColor(hexGuessedColor), hexGuessedColor))
                        .addItem(new IconPowerMenuItem(hexPointInfo, getComplimentaryColor(hexGuessedColor), hexGuessedColor))
                        //.addItem(new IconPowerMenuItem(rInfo, getComplimentaryColor(currentColor), currentColor))
                        //.addItem(new IconPowerMenuItem(gInfo, getComplimentaryColor(currentColor), currentColor))
                        //.addItem(new IconPowerMenuItem(bInfo, getComplimentaryColor(currentColor), currentColor))
                        .addItem(new IconPowerMenuItem(rgbInfo, getComplimentaryColor(currentColor), currentColor))
                        .addItem(new IconPowerMenuItem(rgbGuessedInfo, getComplimentaryColor(rgbGuessedColor), rgbGuessedColor))
                        .addItem(new IconPowerMenuItem(rgbPointInfo, getComplimentaryColor(rgbGuessedColor),rgbGuessedColor))
                        //.addItem(new IconPowerMenuItem(cInfo, getComplimentaryColor(currentColor), currentColor))
                        //.addItem(new IconPowerMenuItem(mInfo, getComplimentaryColor(currentColor), currentColor))
                        //.addItem(new IconPowerMenuItem(yInfo, getComplimentaryColor(currentColor), currentColor))
                        //.addItem(new IconPowerMenuItem(kInfo, getComplimentaryColor(currentColor), currentColor))
                        .addItem(new IconPowerMenuItem(cmykInfo, getComplimentaryColor(currentColor), currentColor))
                        .addItem(new IconPowerMenuItem(cmykGuessedInfo, getComplimentaryColor(cmykGuessedColor), cmykGuessedColor))
                        .addItem(new IconPowerMenuItem(cmykPointInfo, getComplimentaryColor(cmykGuessedColor), cmykGuessedColor))
                        .addItem(new IconPowerMenuItem(scoreInfo, getComplimentaryColor(currentColor), currentColor))
                        .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        .setDividerHeight(3)
                        .setDivider(getResources().getDrawable(android.R.drawable.dark_header, null))
                        .setShowBackground(false)
                        .setLifecycleOwner(MainActivity.this)
                        .build();

                customPowerMenu.showAsDropDown(score);

                //--------RESET----------

                //String rgb = currentColor.red() + "|" + currentColor.green() + "|" + currentColor.blue();

                //String hex = "#" + Integer.toHexString(currentColor.toArgb()).substring(2);
                /*
                UtilLog.e("A:" + A + "|" + "RCol:" + RCol + "|" + "G:" + G + "|" + "B:" + B +
                        " | Hex: " + hex +
                        " | C: " + computedC + " | M: " + computedM + " | Y: " + computedY + " | K: " + computedK);*/
                //reset the field
                reset(getRandomColor());

            }
        });

        //settings button
        fab = findViewById(R.id.settings);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckPowerMenuItem tut = new CheckPowerMenuItem("Show Tutorial", currentColor, getComplimentaryColor(currentColor), false, new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settingsMenu.dismiss();
                        SpotlightSequence.resetSpotlights(MainActivity.this);
                        showTutorial();
                    }
                });

                CheckPowerMenuItem ded = new CheckPowerMenuItem("Show Dedication", currentColor, getComplimentaryColor(currentColor), false, new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settingsMenu.dismiss();
                        new EasyDialog.Builder(MainActivity.this)
                                .setTitle("Dedication")
                                .setSubtitle("This app is dedicated to my mother. " +
                                        "Who is an amazing woman and has this insane ability to guess CMYK colors just by looking at it! " +
                                        "This is to challenge her. Have fun!")
                                .isCancellable(true)
                                .setCancelBtnColor("#008000")
                                .setIcon(Icon.INFO)
                                .setConfirmBtn("OK!", new EasyDialogListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).build();
                    }
                });

                CheckPowerMenuItem cheatMode = new CheckPowerMenuItem("Cheat Mode", currentColor, getComplimentaryColor(currentColor), cheat.getVisibility()==View.VISIBLE, new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settingsMenu.dismiss();
                        cheat.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                    }
                });

                final CheckPowerMenuItem customMode = new CheckPowerMenuItem("Custom Mode", currentColor, getComplimentaryColor(currentColor), colorSeekBar.getVisibility()==View.VISIBLE, new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settingsMenu.dismiss();
                        colorSeekBar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                        if(!b) {
                            reset(getRandomColor());
                        }
                    }
                });

                CheckPowerMenuItem superCustomMode = new CheckPowerMenuItem("Super Custom Cheat Mode", currentColor, getComplimentaryColor(currentColor), superCheatMode, new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settingsMenu.dismiss();
                        superCheatMode = b || customMode.isChecked();
                    }
                });

                CustomPowerMenu.Builder customPowerMenu = new CustomPowerMenu.Builder<>(MainActivity.this, new CheckMenuAdapter())
                        .addItem(tut)
                        .addItem(ded)
                        .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) // Animation start point (TOP | LEFT)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        .setShowBackground(false)
                        .setLifecycleOwner(MainActivity.this);

                if(UtilPreferences.get("cheat_mode", false)) {
                    customPowerMenu.addItem(cheatMode);
                }

                if(UtilPreferences.get("custom_mode", false)) {
                    customPowerMenu.addItem(customMode);
                }

                if(UtilPreferences.get("super_cheat_mode", false)) {
                    customPowerMenu.addItem(superCustomMode);
                }
                settingsMenu = customPowerMenu.build();
                settingsMenu.showAsDropDown(fab);

                /*final PowerMenu powerMenu = new PowerMenu.Builder(MainActivity.this)
                        //.addItemList(list) // list has "Novel", "Poerty", "Art"
                        .addItem(new PowerMenuItem("Show Tutorial", false))
                        .addItem(new PowerMenuItem("View Dedication Again", false))
                        .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) // Animation start point (TOP | LEFT)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        //.setTextColor(Color.BLACK)
                        //.setTextColor(getComplimentaryColor(currentColor))
                        //.setTextColor(currentColor.toArgb())
                        .setTextColor(currentColor)
                        .setSelectedTextColor(Color.WHITE)
                        .setDivider(getResources().getDrawable(android.R.drawable.divider_horizontal_dim_dark, null))
                        //.setMenuColor(Color.WHITE)
                        //.setMenuColor(currentColor.toArgb())
                        .setMenuColor(getComplimentaryColor(currentColor))
                        .setSelectedMenuColor(Color.WHITE)
                        .setShowBackground(false)
                        .setLifecycleOwner(MainActivity.this)
                        .build();

                powerMenu.showAsDropDown(fab);*/


            }
        });

        //let's begin with a random color
        reset(getRandomColor());
        //first time tutorial
        firstTime();

    }

    /**
     * getRGBFromCMYK - gets rgb values from the cmyk
     * @param c - c
     * @param m - m
     * @param y - y
     * @param k - k
     * @return the rgb values in an array
     */
    public int[] getRGBFromCMYK(int c, int m, int y, int k) {

        /*
        CMYK to RGB
        double computedC = 1 - (Double.parseDouble(RCol+"") / RGB_MAX);
        double computedM = 1 - (Double.parseDouble(G+"") / RGB_MAX);
        double computedY = 1 - (Double.parseDouble(B+"") / RGB_MAX);

        double minCMY = Math.min(computedC, Math.min(computedM, computedY));

        computedC = (computedC - minCMY) / (1 - minCMY);
        computedM = (computedM - minCMY) / (1 - minCMY);
        computedY = (computedY - minCMY) / (1 - minCMY);
        double computedK = minCMY;

        int C = (int) (computedC*100);
        int M = (int) (computedM*100);
        int Y = (int) (computedY*100);
        int K = (int) (computedK*100);
         */

        double c1 = ((c/100.0)*(1-(k/100.0)))+(k/100.0);
        double m1 = ((m/100.0)*(1-(k/100.0)))+(k/100.0);
        double y1 = ((y/100.0)*(1-(k/100.0)))+(k/100.0);

        double r = ((1-c1)*RGB_MAX);
        double g = ((1-m1)*RGB_MAX);
        double b = ((1-y1)*RGB_MAX);

        UtilLog.w(cheat.getText()+"");
        UtilLog.w("R: " + r + " G: " + g + " B: " + b + " C: " + c + " M: " + m + " Y: " + y + " K: " + k);

        return new int[]{(int) r, (int) g, (int) b};
    }

    /**
     * getComplimentaryColor - gets the complimentary color
     * @param color - the color you want to get the complimentary color from
     * @return - the complimentary color
     */
    public static int getComplimentaryColor(int color) {
        return Color.rgb(RGB_MAX-Color.red(color),
                RGB_MAX-Color.green(color),
                RGB_MAX-Color.blue(color));
    }

    /**
     * getScore - gets the score
     * @param actual - the actual value
     * @param guessed - the guessed value
     * @return - points
     */
    public int getScore(int actual, int guessed) {
        //RGB_MAX - difference
        return RGB_MAX-(actual>=guessed ? (actual-guessed) : (guessed-actual));
    }

    /**
     * getCMYKScore - gets the score
     * @param actual - the actual value
     * @param guessed - the guessed value
     * @return - points
     */
    public double getCMYKScore(double actual, double guessed) {
        //CMYK_MAX - difference
        return CMYK_MAX-(actual>=guessed ? (actual-guessed) : (guessed-actual));

    }

    /**
     * changes all of the text colors
     * @param color - the color to use
     */
    public void setTextColors(int color) {
        score.setTextColor(color);
        rValue.setTextColor(color);
        gValue.setTextColor(color);
        bValue.setTextColor(color);
        hexValue.setTextColor(color);
        cValue.setTextColor(color);
        mValue.setTextColor(color);
        yValue.setTextColor(color);
        kValue.setTextColor(color);
        //presetColor.setTextColor(getComplimentaryColor(Color.valueOf(color)));
        //presetColor.setTextColor(getComplimentaryColor(color));

        score.setHintTextColor(color);
        rValue.setHintTextColor(color);
        gValue.setHintTextColor(color);
        bValue.setHintTextColor(color);
        hexValue.setHintTextColor(color);
        cValue.setHintTextColor(color);
        mValue.setHintTextColor(color);
        yValue.setHintTextColor(color);
        kValue.setHintTextColor(color);
    }

    /**
     * reset - resets everything with a new color
     * @param newColor - the color to use
     */
    public void reset(int newColor) {
        rValue.setText("");
        gValue.setText("");
        bValue.setText("");

        hexValue.setText("");

        cValue.setText("");
        mValue.setText("");
        yValue.setText("");
        kValue.setText("");

        currentColor = newColor;

        Drawable d = colorToGuess.getDrawable();
        d.setColorFilter(new PorterDuffColorFilter(currentColor, PorterDuff.Mode.MULTIPLY));
        colorToGuess.setImageDrawable(d);

        fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getComplimentaryColor(currentColor)}));

        int color = currentColor;
        //rgb
        int A1 = (color >> 24) & 0xff; // or color >>> 24
        int R1 = (color >> 16) & 0xff;
        int G1 = (color >>  8) & 0xff;
        int B1 = (color      ) & 0xff;
        //hex
        String hex1 = "#" + Integer.toHexString(currentColor).substring(2);
        //cmyk
        double computedC = 1 - (Double.parseDouble(R1+"") / RGB_MAX);
        double computedM = 1 - (Double.parseDouble(G1+"") / RGB_MAX);
        double computedY = 1 - (Double.parseDouble(B1+"") / RGB_MAX);

        double minCMY = Math.min(computedC, Math.min(computedM, computedY));

        computedC = (computedC - minCMY) / (1 - minCMY);
        computedM = (computedM - minCMY) / (1 - minCMY);
        computedY = (computedY - minCMY) / (1 - minCMY);
        double computedK = minCMY;

        computedC = Double.parseDouble(String.format("%.3f", computedC));
        computedM = Double.parseDouble(String.format("%.3f", computedM));
        computedY = Double.parseDouble(String.format("%.3f", computedY));
        computedK = Double.parseDouble(String.format("%.3f", computedK));
        //what the values are
        String msg = "A:" + A1 + "|" + "R:" + R1 + "|" + "G:" + G1 + "|" + "B:" + B1 +
                "| Hex: " + hex1 +
                " | C: " + computedC + " | M: " + computedM + " | Y: " + computedY + " | K: " + computedK;

        UtilLog.e(msg);

        UtilDevice.changeStatusBarColor(this, color);
        //for cheat mode
        cheat.setText(msg);
        //change tutorial colors
        config.setLineAndArcColor(currentColor);
        config.setHeadingTvColor(currentColor);

        presetColor.setSelection(0);
        //hide the keyboard if its showing
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (NullPointerException e) {

        }

    }

    /**
     * setPresets - sets all fields to a color
     * @param color - the color
     */
    public void setPresets(int color) {
        //if its transparent, reset the fields
        if(color==Color.TRANSPARENT) {

            rValue.setText("");
            gValue.setText("");
            bValue.setText("");

            hexValue.setText("");

            cValue.setText("");
            mValue.setText("");
            yValue.setText("");
            kValue.setText("");

        } else {

            int A1 = (color >> 24) & 0xff; // or color >>> 24
            int R1 = (color >> 16) & 0xff;
            int G1 = (color >> 8) & 0xff;
            int B1 = (color) & 0xff;

            String hex1 = Integer.toHexString(color).substring(2);

            double computedC = 1 - (Double.parseDouble(R1+"") / RGB_MAX);
            double computedM = 1 - (Double.parseDouble(G1+"") / RGB_MAX);
            double computedY = 1 - (Double.parseDouble(B1+"") / RGB_MAX);

            double minCMY = Math.min(computedC, Math.min(computedM, computedY));

            computedC = (computedC - minCMY) / (1 - minCMY);
            computedM = (computedM - minCMY) / (1 - minCMY);
            computedY = (computedY - minCMY) / (1 - minCMY);
            double computedK = minCMY;

            int C = (int) (computedC * 100);
            int M = (int) (computedM * 100);
            int Y = (int) (computedY * 100);
            int K = (int) (computedK * 100);

            rValue.setText(R1 + "");
            gValue.setText(G1 + "");
            bValue.setText(B1 + "");
            hexValue.setText(hex1 + "");

            cValue.setText(C + "");
            mValue.setText(M + "");
            yValue.setText(Y + "");
            kValue.setText(K + "");

        }

    }

    /**
     * getRandomColor - gets a random color
     * @return - a random color
     */
    public int getRandomColor() {
        int a = 255;//gen.nextInt(RGB_MAX+1);
        int r = gen.nextInt(RGB_MAX+1);
        int g = gen.nextInt(RGB_MAX+1);
        int b = gen.nextInt(RGB_MAX+1);
        //return Color.valueOf(Color.rgb(r,g,b));
        return Color.argb(a,r,g,b);
    }

    /**
     * confetti - shoots confetti
     * @param colors - an array of colors to use
     */
    public void confetti(int... colors) {
        konfettiView.build()
                .addColors(colors)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 12))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .stream(300, 500L);
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        sharedPref.getInt("score", currentScore);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("score", currentScore);
        editor.apply();

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("score", currentScore);
        editor.apply();

        super.onStop();
    }

    /**
     * showTutorial - shows a tutorial
     */
    public void showTutorial() {
        SpotlightSequence sequence = SpotlightSequence.getInstance(MainActivity.this, config);
        sequence.addSpotlight(colorToGuess, getString(R.string.color_guess), getString(R.string.color_guess_info), "colortoguess", false);
        sequence.addSpotlight(rgb, getString(R.string.rgb), getString(R.string.rgb_info), "rgb");
        sequence.addSpotlight(hexValue, getString(R.string.hex), getString(R.string.hex_info), "hex");
        sequence.addSpotlight(cmyk, getString(R.string.cmyk), getString(R.string.cmyk_info), "cmyk");
        sequence.addSpotlight(presetColor, getString(R.string.preset), getString(R.string.preset_info), "preset");
        sequence.addSpotlight(guess, getString(R.string.guess), getString(R.string.guess_info), "guess");
        sequence.addSpotlight(score, getString(R.string.score), getString(R.string.score_info), "score");
        sequence.addSpotlight(fab, getString(R.string.settings), getString(R.string.settings_info), "settings");
        sequence.startSequence();
    }

    /**
     * firstTime - shows a first time dialog
     */
    public void firstTime() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPref.edit();
        boolean  firstTime=sharedPref.getBoolean("first", true);
        if(firstTime) {

            new EasyDialog.Builder(this)
                    .setTitle("Dedication")
                    .setSubtitle("This app is dedicated to my mother. " +
                            "Who is an amazing woman and has this insane ability to guess CMYK colors just by looking at it! " +
                            "This is to challenge her. Have fun!")
                    .isCancellable(true)
                    .setCancelBtnColor("#008000")
                    .setIcon(Icon.INFO)
                    .setConfirmBtn("OK!", new EasyDialogListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            editor.putBoolean("first",false);
                            //For commit the changes, Use either editor.commit(); or  editor.apply();.
                            editor.apply();

                            //SpotlightSequence.resetSpotlights(this);
                            showTutorial();


                        }
                    })
                    .setCancelBtn("I don't want to", new EasyDialogListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.this.finish();
                        }
                    })
                    .build();


        } else {
            //continue on
            //SpotlightSequence.resetSpotlights(this);
            //SpotlightSequence sequence = SpotlightSequence.getInstance(MainActivity.this, config);
            //sequence.addSpotlight(guess, getString(R.string.rgb), getString(R.string.rgb_info), "rgb");
            //sequence.startSequence();
        }


    }

}
