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
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.majeur.cling.Cling;
import com.majeur.cling.ClingManager;
import com.majeur.cling.ViewTarget;
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
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {

    Button guess;

    EditText rValue;
    EditText gValue;
    EditText bValue;
    //EditText hexValue;
    PrefixEditText hexValue;
    //ExtendedEditText hexValue;

    EditText cValue;
    EditText mValue;
    EditText yValue;
    EditText kValue;

    TextView score;

    RelativeLayout layout;
    RelativeLayout cmyk;
    RelativeLayout rgb;

    FloatingActionButton fab;

    Random gen = new Random();

    //Color currentColor;
    int currentColor;

    int currentScore = 0;

    SpotlightConfig config;

    KonfettiView konfettiView;

    final static int RGB_MAX = 255;
    final static int CMYK_MAX = 100;

    TextView cheat;

    //MaterialSpinner presetColor;
    Spinner presetColor;

    CircleImageView colorToGuess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        new KonamiCode.Installer(this)
                .on(this)
                .callback(new KonamiCodeLayout.Callback() {
                    @Override
                    public void onFinish() {
                        //whatever
                        cheat.setVisibility(View.VISIBLE);
                        UtilNotification.showToast(MainActivity.this, "Super Color Mode Activated!", UtilNotification.Lengths.Short);
                    }
                })
                .install();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        colorToGuess = findViewById(R.id.profile_image);

        colorToGuess.getLayoutParams().width=(width/2);

        colorToGuess.requestLayout();

        guess = findViewById(R.id.guess);

        //guess.setBackgroundResource(R.drawable.rounded_corners);

        cheat = findViewById(R.id.cheatMode);

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
                    ((TextView) adapterView.getChildAt(0)).setTextColor(rValue.getHintTextColors());
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

        //presetColor.setBackgroundResource(R.drawable.rounded_corners);
        //presetColor.setBackgroundColor(guess.getSolidColor());

        /*presetColor.setItems(getResources().getStringArray(R.array.colors));

        presetColor.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                UtilLog.e(item);

                int color;

                if(item.equals("")) {
                    color = Color.TRANSPARENT;
                } else if(item.equals("Dark Gray")) {
                    color = Color.DKGRAY;
                } else if(item.equals("Light Gray")) {
                    color = Color.LTGRAY;
                } else {
                    color = Color.parseColor(item);
                }

                UtilLog.e(color + " is the color " + item);

                setPresets(color);

            }
        });*/

        rValue = findViewById(R.id.r_value);
        gValue = findViewById(R.id.g_value);
        bValue = findViewById(R.id.b_value);

        hexValue = findViewById(R.id.hex_value);

        hexValue.setTextChanges(new PrefixEditText.OnTextChanges() {
            @Override
            public void afterTextChanged(Editable text) {
                hexValue.setSuffix(text.toString().length() + "/" + 6);
            }

            @Override
            public void onTextChanged(String text) {

            }

            @Override
            public void beforeTextChanged(String text) {

            }
        });

        cValue = findViewById(R.id.c_value);
        mValue = findViewById(R.id.m_value);
        yValue = findViewById(R.id.y_value);
        kValue = findViewById(R.id.k_value);

        cmyk = findViewById(R.id.cmyk_view);
        rgb = findViewById(R.id.rgb_view);

        fab = findViewById(R.id.settings);

        konfettiView = findViewById(R.id.viewKonfetti);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        currentScore = sharedPref.getInt("score", 0);

        score = findViewById(R.id.score);
        score.setTextSize(20);
        score.setText("Score: " + currentScore);

        layout = findViewById(R.id.background);

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

        reset();

        firstTime();

        guess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int addedScore = 0;

                int hexPoints = 0;
                int rgbPoints = 0;
                int cmykPoints = 0;

                //int color = currentColor.toArgb();
                int color = currentColor;

                int A = (color >> 24) & 0xff; // or color >>> 24
                int RCol = (color >> 16) & 0xff;
                int G = (color >>  8) & 0xff;
                int B = (color      ) & 0xff;

                //------RGB-----

                String rGuessed = rValue.getText().toString();
                String gGuessed = gValue.getText().toString();
                String bGuessed = bValue.getText().toString();

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

                    if(rScore==RGB_MAX && gScore==RGB_MAX && bScore==RGB_MAX) {
                        confetti(Color.RED, Color.GREEN, Color.BLUE);
                    }

                    rgbPoints = rScore+gScore+bScore;
                    addedScore+=rgbPoints;

                }

                //----HEX----

                String hexGuess = hexValue.getText().toString();

                try {

                    if (!hexGuess.equals("")) {

                        int c = Color.parseColor("#" + hexGuess);

                        int r = (c >> 16) & 0xff;
                        int g = (c >> 8) & 0xff;
                        int b = (c) & 0xff;

                        int rScore = getScore(RCol, r);
                        int gScore = getScore(G, g);
                        int bScore = getScore(B, b);

                        if(rScore==RGB_MAX && gScore==RGB_MAX && bScore==RGB_MAX) {
                            confetti(Color.RED, Color.GREEN, Color.BLUE);
                        }

                        hexPoints = rScore + gScore + bScore;
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

                if(!cGuessed.equals("") && !mGuessed.equals("") && !yGuessed.equals("") && !kGuessed.equals("")) {

                    double c = Double.parseDouble(cGuessed);
                    double m = Double.parseDouble(mGuessed);
                    double y = Double.parseDouble(yGuessed);
                    double k = Double.parseDouble(kGuessed);

                    if((c<=100 || c>=0) && (m<=100 || m>=0) && (y<=100 || y>=0) && (k<=100 || k>=0)) {

                        //c = Double.parseDouble(String.format("%.2f", c));
                        //m = Double.parseDouble(String.format("%.2f", c));
                        //y = Double.parseDouble(String.format("%.2f", c));
                        //k = Double.parseDouble(String.format("%.2f", c));

                        double cs = getCMYKScore(C, c);
                        double ms = getCMYKScore(M, m);
                        double ys = getCMYKScore(Y, y);
                        double ks = getCMYKScore(K, k);

                        if(cs==CMYK_MAX && ms==CMYK_MAX && ys==CMYK_MAX && ks==CMYK_MAX) {
                            confetti(Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.BLACK);
                        }

                        cmykPoints = (int) (cs + ms + ys + ks);
                        addedScore += cmykPoints;

                    } else {
                        addedScore+=0;
                    }

                }

                currentScore+=addedScore;

                //------Add Scores------

                score.setText(getString(R.string.scores, currentScore));

                String checkedMark = "\u2713";
                String xMark = "X";

                /*String hexInfo = "Hex: #" + Integer.toHexString(currentColor.toArgb()).substring(2) + "\t"
                        + ((Integer.toHexString(currentColor.toArgb()).substring(2)).equals(hexGuess) ? checkedMark : xMark) +
                        "\t" + hexGuess;*/
                String hexInfo = "Hex: #" + Integer.toHexString(currentColor).substring(2) + "\t"
                        + ((Integer.toHexString(currentColor).substring(2)).equals(hexGuess) ? checkedMark : xMark);// + "\t" + hexGuess;

                String rInfo = "R: " + RCol + "\t" + ((RCol+"").equals(rGuessed) ? checkedMark : xMark);// + "\t" + rGuessed;
                String gInfo = "G: " + G + "\t" + ((G+"").equals(gGuessed) ? checkedMark : xMark);// + "\t" + gGuessed;
                String bInfo = "B: " + B + "\t" + ((B+"").equals(bGuessed) ? checkedMark : xMark);// + "\t" + bGuessed;

                String rgbInfo = rInfo+"\n"+gInfo+"\n"+bInfo;

                String cInfo = "C: " + C + "\t" + ((C+"").equals(cGuessed) ? checkedMark : xMark);// + "\t" + cGuessed;
                String mInfo = "M: " + M + "\t" + ((M+"").equals(mGuessed) ? checkedMark : xMark);// + "\t" + mGuessed;
                String yInfo = "Y: " + Y + "\t" + ((Y+"").equals(yGuessed) ? checkedMark : xMark);// + "\t" + yGuessed;
                String kInfo = "K: " + K + "\t" + ((K+"").equals(kGuessed) ? checkedMark : xMark);// + "\t" + kGuessed;

                String cmykInfo = cInfo+"\n"+mInfo+"\n"+yInfo+"\n"+kInfo;

                String scoreInfo = "Points Scored: " + addedScore;


                String hexGuessedInfo = "Hex: #" + (!hexGuess.equals("") ? hexGuess : "ffffff");
                int hexGuessedColor;

                try {
                    hexGuessedColor = Color.parseColor("#" + hexGuess);
                } catch (NumberFormatException e) {
                    hexGuessedColor = Color.WHITE;
                }

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

                String hexPointInfo = hexPoints + " points";
                String rgbPointInfo = rgbPoints + " points";
                String cmykPointInfo = cmykPoints + " points";

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

                /*PowerMenu powerMenu = new PowerMenu.Builder(MainActivity.this)
                        //.addItemList(list) // list has "Novel", "Poerty", "Art"
                        .addItem(new PowerMenuItem(hexInfo, false))
                        .addItem(new PowerMenuItem(rInfo, false))
                        .addItem(new PowerMenuItem(gInfo, false))
                        .addItem(new PowerMenuItem(bInfo, false))
                        .addItem(new PowerMenuItem(cInfo, false))
                        .addItem(new PowerMenuItem(mInfo, false))
                        .addItem(new PowerMenuItem(yInfo, false))
                        .addItem(new PowerMenuItem(kInfo, false))
                        .addItem(new PowerMenuItem(scoreInfo, false))
                        .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        .setDivider(getResources().getDrawable(android.R.drawable.divider_horizontal_dim_dark))
                        //.setTextColor(Color.BLACK)
                        .setTextColor(getComplimentaryColor(currentColor))
                        .setSelectedTextColor(Color.WHITE)
                        //.setMenuColor(Color.WHITE)
                        //.setMenuColor(currentColor.toArgb())
                        .setMenuColor(currentColor)
                        .setSelectedMenuColor(Color.WHITE)
                        .setShowBackground(false)
                        .setLifecycleOwner(MainActivity.this)
                        .build();

                //powerMenu.showAsDropDown(score);*/

                //--------RESET----------

                //String rgb = currentColor.red() + "|" + currentColor.green() + "|" + currentColor.blue();

                //String hex = "#" + Integer.toHexString(currentColor.toArgb()).substring(2);
                /*
                UtilLog.e("A:" + A + "|" + "RCol:" + RCol + "|" + "G:" + G + "|" + "B:" + B +
                        " | Hex: " + hex +
                        " | C: " + computedC + " | M: " + computedM + " | Y: " + computedY + " | K: " + computedK);*/

                reset();

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final PowerMenu powerMenu = new PowerMenu.Builder(MainActivity.this)
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

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int position, PowerMenuItem item) {

                        powerMenu.dismiss();

                        switch (item.getTitle()) {

                            case "Show Tutorial":

                                SpotlightSequence.resetSpotlights(MainActivity.this);
                                showTutorial();

                                break;

                            case "View Dedication Again":

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

                                break;

                            default:
                                break;
                        }

                    }
                });

                powerMenu.showAsDropDown(fab);


            }
        });


    }

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

    public static int getComplimentaryColor(int color) {

        //int color1 = color.toArgb();

        return Color.rgb(RGB_MAX-Color.red(color),
                RGB_MAX-Color.green(color),
                RGB_MAX-Color.blue(color));
    }

    public int getScore(int actual, int guessed) {

        /*int dif;

        if(actual==guessed) {

            return 255;

        } else if(actual>guessed) {

            dif = actual-guessed;

        } else if(actual<guessed) {

            dif = guessed-actual;

        } else {
            return 0;
        }

        if(dif>=128) {
            return 128+dif;
        } else if(dif<128) {
            return 128 - dif;
        } else {
            return 0;
        }*/

        return RGB_MAX-(actual>=guessed ? (actual-guessed) : (guessed-actual));
    }

    public double getCMYKScore(double actual, double guessed) {

        return CMYK_MAX-(actual>=guessed ? (actual-guessed) : (guessed-actual));

    }

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

    public void reset() {
        rValue.setText("");
        gValue.setText("");
        bValue.setText("");

        hexValue.setText("");

        cValue.setText("");
        mValue.setText("");
        yValue.setText("");
        kValue.setText("");

        currentColor = getRandomColor();
        //layout.setBackgroundColor(currentColor.toArgb());
        //colorToGuess.setCircleBackgroundColor(currentColor.toArgb());

        Drawable d = colorToGuess.getDrawable();
        //d.setColorFilter(new PorterDuffColorFilter(currentColor.toArgb(), PorterDuff.Mode.MULTIPLY));
        d.setColorFilter(new PorterDuffColorFilter(currentColor, PorterDuff.Mode.MULTIPLY));
        colorToGuess.setImageDrawable(d);

        //setTextColors(getComplimentaryColor(currentColor));

        fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getComplimentaryColor(currentColor)}));

        //int color1 = currentColor.toArgb();
        int color1 = currentColor;

        int A1 = (color1 >> 24) & 0xff; // or color >>> 24
        int R1 = (color1 >> 16) & 0xff;
        int G1 = (color1 >>  8) & 0xff;
        int B1 = (color1      ) & 0xff;

        /*String hex1 = "#" + Integer.toHexString(currentColor.toArgb()).substring(2);

        double computedC = 1 - (currentColor.red() / RGB_MAX);
        double computedM = 1 - (currentColor.green() / RGB_MAX);
        double computedY = 1 - (currentColor.blue() / RGB_MAX);*/

        String hex1 = "#" + Integer.toHexString(currentColor).substring(2);

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

        String msg = "A:" + A1 + "|" + "R:" + R1 + "|" + "G:" + G1 + "|" + "B:" + B1 +
                "| Hex: " + hex1 +
                " | C: " + computedC + " | M: " + computedM + " | Y: " + computedY + " | K: " + computedK;

        UtilLog.e(msg);

        UtilDevice.changeStatusBarColor(this, color1);

        cheat.setText(msg);

        //config.setLineAndArcColor(getComplimentaryColor(currentColor));
        //config.setHeadingTvColor(getComplimentaryColor(currentColor));

        //config.setLineAndArcColor(currentColor.toArgb());
        //config.setHeadingTvColor(currentColor.toArgb());

        config.setLineAndArcColor(currentColor);
        config.setHeadingTvColor(currentColor);

        //presetColor.setBackgroundColor(currentColor.toArgb());
        //presetColor.setBackgroundColor(UtilImage.lighter(getComplimentaryColor(currentColor), 0.5f));
        //presetColor.setSelectedIndex(0);
        presetColor.setSelection(0);

    }

    public void setPresets(int color) {

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

    public int getRandomColor() {
        int a = 255;//gen.nextInt(RGB_MAX+1);
        int r = gen.nextInt(RGB_MAX+1);
        int g = gen.nextInt(RGB_MAX+1);
        int b = gen.nextInt(RGB_MAX+1);
        //return Color.valueOf(Color.rgb(r,g,b));
        return Color.argb(a,r,g,b);
    }

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

    public void getRGBOfColor(int color) {
        int A = (color >> 24) & 0xff; // or color >>> 24
        int RCol = (color >> 16) & 0xff;
        int G = (color >>  8) & 0xff;
        int B = (color      ) & 0xff;
    }

}
