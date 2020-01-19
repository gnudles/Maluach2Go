package kapandaria.maluach2go;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import kapandaria.YDate.Format;
import kapandaria.YDate.YDateLanguage;

/**
 * Created by Orr Dvori on 8/11/2017.
 */

public class PsalmsActivity extends Activity {
    int current_chapter=1;
    Menu psalm_menu;
    float text_size_mm;
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.psalms_layout);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/EzraSILSR.ttf");
        //Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Cardo-Regular.ttf");
        settings=getPreferences(MODE_PRIVATE);
        setFontSize(settings.getFloat("psalms.text_size_mm",5));
        ((TextView) findViewById(R.id.textViewPsalms)).setTypeface(font);

        //((TextView) findViewById(R.id.textViewPsalms)).setMovementMethod(new ScrollingMovementMethod());
        ((TextView) findViewById(R.id.textViewPsalms)).setIncludeFontPadding(true);
        if (savedInstanceState!=null)
        {
            setCurrentChapter( savedInstanceState.getInt("chapter"));
        }
        else
        {
            setCurrentChapter(1);
        }

    }
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt("chapter",current_chapter);
    }

    void setCurrentChapter(int a)
    {
        if (a>150)
            a=150;
        if (a<1)
            a=1;
        current_chapter = a;
        //settings.edit().putInt("psalms.chapter",current_chapter).apply();
        Spanned psalm_span=getPsalmText(current_chapter);
        ((TextView) findViewById(R.id.textViewPsalms)).setText(psalm_span, TextView.BufferType.SPANNABLE);
        //((TextView) findViewById(R.id.textViewPsalms)).scrollTo(0,0);
        ((ScrollView)findViewById(R.id.scroller)).scrollTo(0,0);
        //((Button) findViewById(R.id.psalm_chapter)).setText(YDateLanguage.getLanguageEngine(YDateLanguage.Language.HEBREW).getNumber(current_chapter));
        if (psalm_menu != null)
            (psalm_menu.findItem(R.id.psalm_chapter_menu_item)).setTitle(Format.HebIntSubString(current_chapter,false,false));


    }
    public void nextChapter()
    {
        setCurrentChapter((current_chapter%150)+1);
    }
    public void prevChapter()
    {
        setCurrentChapter((current_chapter+148)%150+1);
    }
    private void fixNumberPickerBug(NumberPicker np)
    {
        Field f = null;
        try {
            f =NumberPicker.class.getDeclaredField("mInputText");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);

        EditText et= null;
        try {
            et = (EditText)f.get(np);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        et.setFilters(new InputFilter[0]);
    }
    public void openSelector()
    {
        AlertDialog.Builder h=new AlertDialog.Builder(this);
        final NumberPicker np=new NumberPicker(this);
        np.setMinValue(1);
        np.setMaxValue(150);
        np.setWrapSelectorWheel(true);
        np.setValue(current_chapter);



        np.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return Format.HebIntSubString(value,false,false);
            }
        });
        //fixNumberPickerBug(np);
        h.setView(np);
        h.setPositiveButton(R.string.choose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setCurrentChapter(np.getValue());
            }
        });
        //h.setMessage("בחר פרק");
        h.setTitle("תהלים");
        h.create().show();
    }
    public void zoomIn()
    {
        {
            setFontSize(text_size_mm+0.5f);
        }
    }
    public void zoomOut()
    {

        {
            setFontSize(text_size_mm-0.5f);
        }
    }
    void setFontSize(float textSize)
    {
        final float min_text_size_mm=3;
        final float max_text_size_mm=12;
        if (textSize<min_text_size_mm)
            textSize=min_text_size_mm;
        else if (textSize>max_text_size_mm)
            textSize=max_text_size_mm;
        text_size_mm = textSize;
        settings.edit().putFloat("psalms.text_size_mm",text_size_mm).apply();
        ((TextView) findViewById(R.id.textViewPsalms)).setTextSize(TypedValue.COMPLEX_UNIT_MM,text_size_mm);
    }
    Spanned formatPsalmInner(String text)
    {
        String outputHtml=text.replace("-","־")
                .replace("{","<small><font color='#3a638e'>")
                .replace("}","</font></small>")
                .replace("(","<small><font color='#888888'>")
                .replace(")","</font></small>")
                .replace(":","\u05c3<br/>");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(outputHtml,Html.FROM_HTML_MODE_COMPACT);
        }
        else
        {
            return Html.fromHtml(outputHtml);
        }
    }

    Spanned formatPsalm(String text)
    {
        SpannableStringBuilder builder =new SpannableStringBuilder(formatPsalmInner(text));
        builder.setSpan(new LeadingMarginSpan.Standard(8),0,builder.length(),0);
        return builder;

    }
    Spanned getPsalmText(int chapter)
    {

        try {
            InputStream inputStream = getAssets().open("psalms/" + String.valueOf(chapter) + ".txt");
            byte [] b = new byte[inputStream.available()];
            inputStream.read(b);
            return formatPsalm(new String(b));


        }
        catch (IOException e)
        {
            return Html.fromHtml("");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.psalms_menu,menu);
        psalm_menu = menu;
        if (psalm_menu != null)
            (psalm_menu.findItem(R.id.psalm_chapter_menu_item)).setTitle(Format.HebIntSubString(current_chapter,false,false));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.psalm_chapter_menu_item:
                openSelector();
                return true;
            case R.id.next_psalm_item:
                nextChapter();

                return true;
            case R.id.prev_psalm_item:
                prevChapter();
                return true;
            case R.id.scale_up_font_item:
                zoomIn();
                return true;
            case R.id.scale_down_font_item:
                zoomOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

}
