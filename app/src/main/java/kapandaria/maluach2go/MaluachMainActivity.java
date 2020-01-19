package kapandaria.maluach2go;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Typeface;

import java.util.Date;

import kapandaria.YDate.*;
import kapandaria.GP.EventHandler;


public class MaluachMainActivity extends Activity {
    EventHandler.Listener _showInfo = new EventHandler.Listener() {
        @Override
        public void process(Object sender) {
            showInfo();
        }
    };
    EventHandler.Listener _updateText = new EventHandler.Listener() {
        @Override
        public void process(Object sender) {
            updateText();
        }
    };
    protected void showInformation(YDateDual dateCursor)
    {
        String lstr=parasha(dateCursor);
        if (lstr.length()>0)
        {

            lstr+="\n";
        }
        String omer=SfiratOmer(dateCursor);
        lstr+=omer;
        if (omer.length()>0)
            lstr+="\n";
        if (dateCursor.hebrewDate().dayInMonth()==1 || dateCursor.hebrewDate().dayInMonth()==30)
            lstr+="ראש חדש\n";
        else
        {
            if (dateCursor.hebrewDate().mevarchinShabbat())
                lstr+="מברכין החדש\n";
        }
        if (dateCursor.hebrewDate().yomHakhel())
        {
            lstr+="יום הקהל\n";
        }
        if (TorahReading.getShabbatBereshit(dateCursor.hebrewDate().yearLength(),dateCursor.hebrewDate().yearFirstDay())+15*7-4 == dateCursor.hebrewDate().GDN())
        {
            lstr+="סגולת פרשת המן שניים מקרא ואחד תרגום\n";
        }
        lstr+="שנה "+dateCursor.hebrewDate().ShmitaLabel(language)+"\n";
        int day_tkufa=dateCursor.hebrewDate().dayInTkufa();
        //int day_mazal=dateCursor.hebrewDate().dayInMazal();
        lstr+="יום "+Integer.toString(day_tkufa+1)+" ל" +dateCursor.hebrewDate().TkufaName(language)+"\n";
        if (day_tkufa==0)
            lstr+="תחילת תקופה "+dateCursor.hebrewDate().TkufaBeginning(new NativeTzProvider(),language)+"\n";
        if (day_tkufa==59 && dateCursor.hebrewDate().TkufaType()==JewishDate.M_ID_TISHREI)
            lstr+="מתחילים לומר תן טל ומטר בחו\"ל\n";
        if (dateCursor.hebrewDate().dayInYear()==36 ) // 7 in chesvan
            lstr+="מתחילים לומר תן טל ומטר בארץ ישראל\n";
        /*lstr+="יום "+Integer.toString(day_mazal+1)+" ל" +dateCursor.hebrewDate().MazalName(true)+"\n";
        if (day_tkufa!=0 && day_mazal == 0)
            lstr+="חילוף מזל "+dateCursor.hebrewDate().MazalBeginning(new NativeTzProvider())+"\n";*/
        lstr+="ברכת החמה בעוד "+Integer.toString((10227-dateCursor.hebrewDate().TkufotCycle())%10227)+" יום\n";
        lstr+="\nדף יומי "+ DailyStudy.getBavliDafYomi(dateCursor.hebrewDate().GDN(), true);
        lstr+="\nירושלמי "+ DailyStudy.getYerushalmiDafYomi(dateCursor.hebrewDate().GDN(), true);
        lstr+="\nמשנה יומית "+ DailyStudy.MishnaYomit(dateCursor.hebrewDate().GDN(),false, true);

        if (lstr.length()>0)
            showAlert(dateCursor.hebrewDate().dayString(language), lstr);
    }
    protected void showMolad(YDateDual dateCursor)
    {
        String lstr;
        lstr=dateCursor.hebrewDate().MoladString(new NativeTzProvider(),language);
        showAlert("מולד הלבנה", lstr);
    }
    private void showAlert(String title,String text)
    {
        AlertDialog.Builder h=new AlertDialog.Builder(this);
        h.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        h.setMessage(text);
        h.setTitle(title);
        h.create().show();
    }
    /*protected void showLimud(YDateDual dateCursor)
    {
        showAlert("דף יומי", DailyLimud.getDafYomi(dateCursor.hebrewDate().daysSinceBeginning(), true));
    }*/
    protected String parasha(YDateDual dateCursor)
    {
        String il_parasha=TorahReading.GetTorahReading(dateCursor.hebrewDate(),false,false,language);
        String diasp=TorahReading.GetTorahReading(dateCursor.hebrewDate(),true,false,language);

        String lstr="";
        if ((diasp.length()==0 || !il_parasha.equals(diasp))&& il_parasha.length()>0)
            lstr+="באה\"ק ";
        lstr+=il_parasha;
        String shabat_str=TorahReading.SpecialShabbat(dateCursor.hebrewDate(),language);
        if (shabat_str.length()>0)
            lstr="שבת "+shabat_str+", "+lstr;
        if (diasp.length()>0 && !il_parasha.equals(diasp))
            lstr+="\nבחו\"ל "+diasp;

        return lstr;
    }
    protected String SfiratOmer(YDateDual dateCursor)
    {
        int om=dateCursor.hebrewDate().sfiratHaomer();
        String lstr="";
        if (om>0)
        {
            lstr="היום "+Format.HebIntString(om,true) + " לעמר (מערב אתמול)";

        }
        om+=1;
        if (om<50 && om>0)
        {
            lstr+="\nבערב "+Format.HebIntString(om,true) + " לעמר";
        }
        return lstr;
    }
    private void showInfo()
    {
        showInformation(ydateView.getDateCursor());


    }
    private void updateText()
    {
        txtView.setText(ydateView.getDateCursor().hebrewDate().dayString(language));
        txtViewGreg.setText(ydateView.getDateCursor().gregorianDate().dayString(language));
        String event_text=ydateView.getDateEvents().yearEvents().getYearEventForDayRejection(ydateView.getDateCursor().hebrewDate(),language);
        txtViewEvent.setText(event_text);
        nextMonthBtn.setText(ydateView.getMonthTitle());
        nextYearBtn.setText(ydateView.getYearTitle());
    }

    public void nextMonthClick(View v)
    {
        YDateDual.STEP_TYPE st=(ydateView.getHebrewMonthAlign())?YDateDual.STEP_TYPE.HEB_MONTH_FORWARD:YDateDual.STEP_TYPE.GRE_MONTH_FORWARD;
        YDateDual yd=ydateView.getDateCursor();
        yd.step(st);
        ydateView.dateCursorUpdated();
    }
    public void previousMonthClick(View v)
    {
        YDateDual.STEP_TYPE st=(ydateView.getHebrewMonthAlign())?YDateDual.STEP_TYPE.HEB_MONTH_BACKWARD:YDateDual.STEP_TYPE.GRE_MONTH_BACKWARD;
        YDateDual yd=ydateView.getDateCursor();
        yd.step(st);
        ydateView.dateCursorUpdated();
    }
    public void nextYearClick(View v)
    {
        YDateDual.STEP_TYPE st=(ydateView.getHebrewMonthAlign())?YDateDual.STEP_TYPE.HEB_YEAR_FORWARD:YDateDual.STEP_TYPE.GRE_YEAR_FORWARD;
        YDateDual yd=ydateView.getDateCursor();
        yd.step(st);
        ydateView.dateCursorUpdated();
    }
    public void previousYearClick(View v)
    {
        YDateDual.STEP_TYPE st=(ydateView.getHebrewMonthAlign())?YDateDual.STEP_TYPE.HEB_YEAR_BACKWARD:YDateDual.STEP_TYPE.GRE_YEAR_BACKWARD;
        YDateDual yd=ydateView.getDateCursor();
        yd.step(st);
        ydateView.dateCursorUpdated();
    }
    public void jumpToToday()
    {
        ydateView.getDateCursor().hebrewDate().MimicDate(YDateDual.createFrom(new Date()).hebrewDate());
        ydateView.dateCursorUpdated();
    }
    public void setHebrewBoard(boolean hebrewBoard)
    {
        ydateView.setHebrewMonthAlign(hebrewBoard);
    }
    public void setRTL(boolean RTL)
    {
        ydateView.setRTL(RTL);
    }

    TextView txtView;
    TextView txtViewGreg;
    TextView txtViewEvent;
    //TextView textViewTitle;
    //Switch switchGregHeb;
    //Switch switchRTL;
    YDateView ydateView;
    Button nextMonthBtn;
    Button nextYearBtn;
    //MenuItem miGregHeb;
    //MenuItem miRTL;
    YDateLanguage language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //super.attachBaseContext(MyContextWrapper.wrap(getBaseContext(),"he"));
        setContentView(R.layout.maluach_layout);

        language = YDateLanguage.getLanguageEngine(YDateLanguage.getLanguageFromCode(getResources().getString(R.string.ydate_lang)));
        txtViewGreg = (TextView) findViewById(R.id.textViewGreg);
        txtViewEvent = (TextView) findViewById(R.id.textViewEvent);
        //textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        txtView = (TextView) findViewById(R.id.textViewHeb);

        nextMonthBtn = (Button) findViewById(R.id.nextMonthBtn);
        nextMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthClick(v);
            }
        });
        nextYearBtn = (Button) findViewById(R.id.nextYearBtn);
        nextYearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextYearClick(v);
            }
        });
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/StamAshkenazCLM.ttf");
        txtView.setTypeface(font);
        txtViewEvent.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/KeterAramTsova.ttf"));
        ydateView = (YDateView) findViewById(R.id.ydateView);
        ydateView.setLanguage(language);
        ydateView.dateClicked().addListener(_showInfo);
        ydateView.dateChanged().addListener(_updateText);
        if (savedInstanceState!=null) {
            ydateView.getDateCursor().hebrewDate().setByGDN(savedInstanceState.getInt("selected_day"));
            ydateView.dateCursorUpdated();
        }
        _updateText.process(null);


    }
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt("selected_day",ydateView.getDateCursor().hebrewDate().GDN());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.psalms_menu_item:
                startPsalms();
                return true;
            case R.id.gregorian_oriented_item:
                item.setChecked(!item.isChecked());
                setHebrewBoard(!item.isChecked());
                return true;
            case R.id.compass_menu_item:
                startCompass();
                return true;
            case R.id.rightToLeft_item:
                item.setChecked(!item.isChecked());
                setRTL(item.isChecked());
                return true;
            case R.id.today_menu_item:
                jumpToToday();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
    private void startPsalms()
    {
        Intent intent = new Intent(this,PsalmsActivity.class);
        startActivity(intent);
    }
    private void startCompass()
    {
        Intent intent = new Intent(this,CompassActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maluach_menu,menu);
        getActionBar().setDisplayShowTitleEnabled(false);

        return true;
    }
}
