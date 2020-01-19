package kapandaria.maluach2go;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.util.AttributeSet;
import android.view.View;

import kapandaria.GP.EventHandler;
import kapandaria.YDate.*;

/**
 * TODO: document your custom view class.
 */
public class YDateView extends View implements OnGestureListener {

    private EventHandler dateChanged;

    public EventHandler dateChanged() {
        if (dateChanged == null)
            dateChanged = new EventHandler();
        return dateChanged;
    }

    private EventHandler dateClicked;

    public EventHandler dateClicked() {
        if (dateClicked == null)
            dateClicked = new EventHandler();
        return dateClicked;
    }

    YDateDual mDateCursor;
    private GestureDetector mGestureDetector;
    private TextPaint mTextPaint;
    private boolean mRightToLeft;
    private boolean mHebrewMonthAlign;
    private boolean mShowBothInCell;
    private boolean mUseFullWeekDayNames;
    private int mFrameColor;
    private int mBackgroundColor;
    private int mSelectionColor;
    private int mSelectedCell;
    private int mZeroCellDay;//days since begginning of the earliest cell that is shown(the first gray)
    private int mHdYearDay;//days since begginning of hebrew year.
    private int mFirstDayCell;//first cell of our month from the view beggining (0..41)
    private int mLastMonthLength;
    private int mMonthLength;
    private int mTodayDay;
    private YDateLanguage mLanguage;


    public void setRTL(boolean RTL) {
        mRightToLeft = RTL;
        invalidate();
    }
    public void setLanguage(YDateLanguage language)
    {
        mLanguage=language;
    }

    public boolean getHebrewMonthAlign() {
        return mHebrewMonthAlign;
    }
    public void setHebrewMonthAlign(boolean heb) {
        mHebrewMonthAlign = heb;
        dateCursorUpdated();
    }

    public YDateView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public YDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public YDateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    public YDateDual getDateCursor() {
        return mDateCursor;
    }
    public EventsMaintainer getDateEvents()
    {
        return mDateCursor.getEventMaintainerErezHaKodesh();
    }

    private void updateSelection(int selection) {
        if (selection >= 0 && mSelectedCell != selection) {
            mDateCursor.jewishDate().seekBy(selection - mSelectedCell);
            dateCursorUpdated();
        }

    }

    public String getYearTitle() {
        if (mHebrewMonthAlign) {
            return mLanguage.getNumber(mDateCursor.hebrewDate().year());
        } else
            return Integer.toString(mDateCursor.gregorianDate().year());
    }

    public String getMonthTitle() {
        if (mHebrewMonthAlign) {
            return mDateCursor.hebrewDate().monthName(mLanguage);
        } else
            return mDateCursor.gregorianDate().monthName(mLanguage);
    }

    public void dateCursorUpdated() {
        int month_fisrt_day;
        int day_in_month;
        if (mHebrewMonthAlign) {
            month_fisrt_day = mDateCursor.hebrewDate().monthFirstDay();
            day_in_month = mDateCursor.hebrewDate().dayInMonth();
            mMonthLength = mDateCursor.hebrewDate().monthLength();
            mLastMonthLength = mDateCursor.hebrewDate().previousMonthLength();
        } else {
            month_fisrt_day = mDateCursor.gregorianDate().monthFirstDay();
            day_in_month = mDateCursor.gregorianDate().dayInMonth();
            mMonthLength = mDateCursor.gregorianDate().monthLength();
            mLastMonthLength = mDateCursor.gregorianDate().previousMonthLength();
        }
        mFirstDayCell = month_fisrt_day % 7;
        if (mFirstDayCell < 2)
            mFirstDayCell += 7;
        mZeroCellDay = month_fisrt_day - mFirstDayCell;
        mSelectedCell = day_in_month - 1 + mFirstDayCell;
        mHdYearDay = mDateCursor.hebrewDate().yearFirstDay();
        if (dateChanged != null) dateChanged.trigger(this);
        invalidate();
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mGestureDetector = new GestureDetector(context, this);

        mStyleDrawer = new YDateViewStyle();
        mTextPaint = new TextPaint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(0xff000000);
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.YDateView, defStyle, 0);

        mRightToLeft = a.getBoolean(
                R.styleable.YDateView_rightToLeft, true);
        mHebrewMonthAlign = a.getBoolean(
                R.styleable.YDateView_hebrewMonth, true);
        mFrameColor = a.getColor(
                R.styleable.YDateView_frameColor,
                0x000000);
        mBackgroundColor = a.getColor(
                R.styleable.YDateView_backgroundColor,
                0xffffff);
        mSelectionColor = a.getColor(
                R.styleable.YDateView_selectionColor,
                0xff0000);

        mDateCursor = YDateDual.getNow();



        mTodayDay = mDateCursor.hebrewDate().GDN();
        dateCursorUpdated();

        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        /*mExampleDimension = a.getDimension(
                R.styleable.YDateView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.YDateView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.YDateView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }*/

        a.recycle();
        if (getWidth() > 0 && getHeight() > 0)
            invalidateMeasurements();

        // Set up a default TextPaint object
        /*mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);*/

        // Update TextPaint and text measurements from attributes
        /*invalidateTextPaintAndMeasurements();*/
    }

    public int getSelectedCell() {
        return mSelectedCell;
    }

    private int m_cellSpacing = 0;

    private int m_cellWidth, m_cellHeight, m_BoardHeight;
    private int m_cellHeaderH;
    private int m_left, m_top;
    private String[] header_weekday;

    private void invalidateMeasurements() {
        int w = getWidth();
        int h = getHeight();
        m_cellSpacing = Math.max(2,3*Math.min(w,h)/256);
        m_cellSpacing &=~1;
        m_cellWidth = (w - 8 * m_cellSpacing) / 7;
        m_left = (w - 6 * m_cellSpacing - 7 * m_cellWidth) / 2;

        m_cellHeaderH = Math.max(h / 20,(int)YDateViewStyle.dp2px(20));
        m_cellHeight = (h - 8 * m_cellSpacing - m_cellHeaderH) / 6;
        mShowBothInCell=m_cellWidth>25 && m_cellHeight>25;

        m_top = (h - m_cellHeaderH - 6 * m_cellHeight - 6 * m_cellSpacing) / 2;
        m_BoardHeight = (m_cellHeight + m_cellSpacing) * 6;
        int min_dimension = Math.min(m_cellHeight, m_cellWidth);
        if (min_dimension > 12) {
            mTextPaint.setTextSize((float) min_dimension * 0.5f);
        }
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();

        header_weekday = new String[7];
        if (mUseFullWeekDayNames) {
            for (int i = 0; i < 7; ++i)
                header_weekday[i] = getResources().getString(dayWeeksFull[i]);
        } else {
            for (int i = 0; i < 7; ++i)
                header_weekday[i] = getResources().getString(dayWeeksShort[i]);
        }
        mStyleDrawer.updateMeasurements(m_cellWidth, m_cellHeight, m_cellHeaderH, m_cellSpacing);

        //if (getWidth())
        /*mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;*/
    }

    protected final int cellToIndex(Point p) {
        if (mRightToLeft) {
            return (6 - p.x) + p.y * 7;
        }
        return p.x + p.y * 7;
    }

    protected final void indexToCell(int i, Point p) {
        if (mRightToLeft) {
            p.set(6 - i % 7, i / 7);
        } else {
            p.set(i % 7, i / 7);
        }
    }

    static final int[] dayWeeksFull = {
            R.string.week_day_full_sun,
            R.string.week_day_full_mon,
            R.string.week_day_full_tue,
            R.string.week_day_full_wed,
            R.string.week_day_full_thu,
            R.string.week_day_full_fri,
            R.string.week_day_full_sat};

    static final int[] dayWeeksShort = {
            R.string.week_day_short_sun,
            R.string.week_day_short_mon,
            R.string.week_day_short_tue,
            R.string.week_day_short_wed,
            R.string.week_day_short_thu,
            R.string.week_day_short_fri,
            R.string.week_day_short_sat};

    static class DrawAttributes {
        public boolean before;
        public boolean after;
        public boolean selected;
        public boolean isToday;
        public boolean jewishDay;
        public boolean evnet;
        public boolean shabbat;
        public boolean erevHag;
        public boolean tzeitHag;//if both  erevHag and tzeitHag is true then it's a Hag.

    }

    public interface StyleDraw {
        void updateMeasurements(int cellw, int cellh, int headerh, int space);

        void drawCell(Canvas c, int x, int y, String text, DrawAttributes attr);

        void drawHeader(Canvas c, int x, int y, String text);
    }

    StyleDraw mStyleDrawer;

    int getCellByPixels(int x, int y) {
        int x_start = m_left;
        int x_jump = (m_cellWidth + m_cellSpacing);
        int y_start = m_top + m_cellHeaderH + m_cellSpacing;
        int y_jump = m_cellHeight + m_cellSpacing;
        if (y >= y_start && x >= x_start) {
            y -= y_start;
            x -= x_start;
            if (y < (6 * y_jump) && x < (7 * x_jump)) {
                if (y % y_jump < m_cellHeight && x % x_jump < m_cellWidth) {
                    return cellToIndex(new Point(x / x_jump, y / y_jump));
                }
            }
        }
        return -1;

    }



    private DrawAttributes attr = new DrawAttributes();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if (getWidth() > 0 && getHeight() > 0) {


            int x_start, y_start;
            int wd;
            Point p = new Point();
            int cell_day;
            int day_num;
            int day_in_year;
            byte event;

            for (int i = 0; i < 6 * 7; ++i) {
                indexToCell(i, p);
                x_start = m_left + p.x * (m_cellWidth + m_cellSpacing);
                y_start = m_top + m_cellHeaderH + m_cellSpacing + p.y * (m_cellHeight + m_cellSpacing);
                cell_day = i - mFirstDayCell;
                day_num = (cell_day < 0) ? (mLastMonthLength + cell_day + 1) :
                        ((cell_day >= mMonthLength) ? (cell_day - mMonthLength + 1) : cell_day + 1);
                day_in_year = i + mZeroCellDay - mHdYearDay;
                attr.selected = (i == mSelectedCell);
                attr.isToday = (mZeroCellDay + i == mTodayDay);
                attr.before = (cell_day < 0);
                attr.after = (cell_day >= mMonthLength);
                event = mDateCursor.getEventMaintainerErezHaKodesh().getEvent(day_in_year);
                attr.evnet = event != 0;
                attr.jewishDay = mHebrewMonthAlign;
                attr.shabbat = (mZeroCellDay + i)%7==ADate.SATURDAY;

                String day_txt;
                if (mShowBothInCell) {
                    if (mHebrewMonthAlign) {
                        day_txt = Format.HebIntString(day_num, false) + ":" +
                                Integer.toString(new GregorianDate(i + mZeroCellDay).dayInMonth());
                    } else {
                        day_txt = Integer.toString(day_num) +":"+Format.HebIntString(new JewishDate(i + mZeroCellDay).dayInMonth(), false);
                    }
                } else {
                    if (mHebrewMonthAlign) {
                        day_txt = Format.HebIntString(day_num, false);
                    } else {
                        day_txt = Integer.toString(day_num);
                    }
                }
                mStyleDrawer.drawCell(canvas, x_start - m_cellSpacing, y_start - m_cellSpacing,
                        day_txt, attr);
                //canvas.drawText(day_txt, x_start, y_start + m_cellHeight, mTextPaint);

            }
            Paint painter = new Paint();
            painter.setColor(getDrawingCacheBackgroundColor());
            canvas.drawRect(0, 0, getWidth(), m_top + m_cellHeaderH + m_cellSpacing, painter);
            painter.setColor(mBackgroundColor);
            //draw header
            for (int j = 0; j < 7; ++j) {
                x_start = m_left + j * (m_cellWidth + m_cellSpacing);
                if (mRightToLeft)
                    wd = 6 - j;
                else
                    wd = j;
                mStyleDrawer.drawHeader(canvas, x_start - m_cellSpacing, m_top,
                        header_weekday[wd]);
                /*canvas.drawRect(x_start, m_top, x_start + m_cellWidth, m_top + m_cellHeaderH, painter);

                canvas.drawText(header_weekday[wd], x_start, m_top + m_cellHeaderH, mTextPaint);*/
            }
        }
        /*
        canvas.drawCircle(60,60,10,p);
        // Draw the text.
        canvas.drawText("hello",
                40 ,
                40 , p);
        canvas.drawRect(0,30,40,60, p);
*/


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        invalidateMeasurements();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);
        //if (!result)
        {
            System.out.println("touch event");
            int action = event.getActionMasked();

            switch (action) {
                case (MotionEvent.ACTION_DOWN):
                    System.out.println("Action was DOWN");
                    return true;
                case (MotionEvent.ACTION_MOVE):
                    System.out.println("Action was MOVE");
                    return true;
                case (MotionEvent.ACTION_UP):
                    System.out.println("Action was UP");
                    return true;
                case (MotionEvent.ACTION_CANCEL):
                    System.out.println("Action was CANCEL");
                    return true;
                case (MotionEvent.ACTION_OUTSIDE):
                    System.out.println("Movement occurred outside bounds " +
                            "of current screen element");
                    return true;
                default:
                    return super.onTouchEvent(event);
            }

            //return true;
        }
        //return result;
    }


    @Override
    public boolean onDown(MotionEvent e) {
        //System.out.println("onDown event");
        updateSelection(getCellByPixels((int) e.getX(), (int) e.getY()));
        if (dateChanged != null) dateChanged.trigger(this);
        invalidate();

        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        //System.out.println("onFling");


        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        System.out.println("onShowPress");
        updateSelection(getCellByPixels((int) e.getX(), (int) e.getY()));
        if (dateClicked != null) dateClicked.trigger(this);
        invalidate();

    }

    /**
     * Notified when a scroll occurs with the initial on down {@link MotionEvent} and the
     * current move {@link MotionEvent}. The distance in x and y is also supplied for
     * convenience.
     *
     * @param e1        The first down motion event that started the scrolling.
     * @param e2        The move motion event that triggered the current onScroll.
     * @param distanceX The distance along the X axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @param distanceY The distance along the Y axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        //System.out.println("onSingleTapUp");
        return true;
    }

}
