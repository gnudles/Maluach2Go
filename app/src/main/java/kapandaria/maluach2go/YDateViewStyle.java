package kapandaria.maluach2go;
import kapandaria.maluach2go.YDateView.DrawAttributes;
import kapandaria.maluach2go.YDateView.StyleDraw;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;



public class YDateViewStyle implements StyleDraw {
    Path mCellPath;
    Path mCellPathDark;
    Path mCellPathLight;
    Path mCellPathLightGreg;
    Path mCellPathDarkGregTop;
    Path mCellPathDarkGregBottom;
    Path mCellPathTipClear;
    Path mCellPathTip;
    Bitmap mCellBitmap;
    Bitmap mCellBitmapSelected;
    Bitmap mCellBitmapGray;
    Paint mTransparentPaint;
    Paint mCellPaint;
    int mSpacing;
    int mHSpacing;
    int mCellW;
    int mCellH;
    int mCellBW;
    int mCellBH;
    int mHeaderH;
    int mCellMin;
    boolean mDrawText;
    float font_size;
    float font_lamed_height;
    float font_num_height;
    float font_height;
    float circle_offset_x;
    float circle_offset_y;
    YDateViewStyle()
    {

        mTransparentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTransparentPaint.setColor(0xffffffff);
        mTransparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCellPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.SUBPIXEL_TEXT_FLAG);

    }
    public static float dp2px(float dp)
    {
        return dp* Resources.getSystem().getDisplayMetrics().density;
    }
    public static float px2dp(float px)
    {
        return px/ Resources.getSystem().getDisplayMetrics().density;
    }
    private void initCellPath()
    {
        mCellPath= new Path();
        mCellPath.moveTo(mHSpacing+mCellMin*0.2f,mHSpacing);
        mCellPath.lineTo(mCellW+mHSpacing,mHSpacing);
        mCellPath.lineTo(mCellW+mHSpacing,mCellH+mHSpacing);
        mCellPath.lineTo(0+mHSpacing,mCellH+mHSpacing);
        mCellPath.lineTo(0+mHSpacing,mCellMin*0.2f+mHSpacing);
        mCellPath.lineTo(mHSpacing+mCellMin*0.2f,mHSpacing);

        mCellPathTipClear=new Path();
        mCellPathTipClear.moveTo(mHSpacing,mHSpacing);
        mCellPathTipClear.lineTo(mHSpacing+mCellMin*0.21f,mHSpacing);
        mCellPathTipClear.lineTo(mHSpacing,mCellMin*0.21f+mHSpacing);
        mCellPathTipClear.lineTo(mHSpacing,mHSpacing);
        mCellPathTip=new Path();
        mCellPathTip.moveTo(mHSpacing,mHSpacing);
        mCellPathTip.lineTo(mHSpacing+mCellMin*0.17f,mHSpacing);
        mCellPathTip.lineTo(mHSpacing,mCellMin*0.17f+mHSpacing);
        mCellPathTip.lineTo(mHSpacing,mHSpacing);

        mCellPathDark= new Path();
        mCellPathDark.moveTo(mHSpacing,mHSpacing);
        mCellPathDark.lineTo(mCellW+mHSpacing,mHSpacing);
        mCellPathDark.lineTo(mCellW+mHSpacing,mCellH*0.4f+mHSpacing);
        mCellPathDark.quadTo(mCellW*0.5f+mHSpacing,mCellH*0.6f+mHSpacing,mHSpacing,mCellH*0.4f+mHSpacing);
        mCellPathDark.lineTo(mHSpacing,mHSpacing);
        mCellPathDark.op(mCellPathTipClear, Path.Op.DIFFERENCE);

        mCellPathLight= new Path();
        mCellPathLight.moveTo(mHSpacing,mCellH*0.4f+mHSpacing);
        mCellPathLight.lineTo(mHSpacing,mCellH+mHSpacing);
        mCellPathLight.lineTo(mHSpacing+mCellW,mCellH+mHSpacing);
        mCellPathLight.lineTo(mHSpacing+mCellW,mCellH*0.4f+mHSpacing);

        mCellPathDarkGregTop= new Path();
        mCellPathDarkGregTop.moveTo(mHSpacing,mHSpacing);
        mCellPathDarkGregTop.lineTo(mCellW+mHSpacing,mHSpacing);
        mCellPathDarkGregTop.lineTo(mCellW+mHSpacing,mCellH*0.2f+mHSpacing);
        mCellPathDarkGregTop.quadTo(mCellW*0.32f+mHSpacing,mCellH*0.35f+mHSpacing,mHSpacing,mCellH*0.21f+mHSpacing);
        mCellPathDarkGregTop.lineTo(mHSpacing,mHSpacing);
        mCellPathDarkGregTop.op(mCellPathTipClear, Path.Op.DIFFERENCE);

        mCellPathLightGreg= new Path();
        mCellPathLightGreg.moveTo(mHSpacing,mCellH*0.15f+mHSpacing);
        mCellPathLightGreg.lineTo(mHSpacing,mCellH*0.8f+mHSpacing);
        mCellPathLightGreg.lineTo(mHSpacing+mCellW,mCellH*0.8f+mHSpacing);
        mCellPathLightGreg.lineTo(mHSpacing+mCellW,mCellH*0.15f+mHSpacing);
        mCellPathLightGreg.op(mCellPathTipClear, Path.Op.DIFFERENCE);

        mCellPathDarkGregBottom= new Path();
        mCellPathDarkGregBottom.moveTo(mCellW+mHSpacing,mCellH+mHSpacing);
        mCellPathDarkGregBottom.lineTo(mHSpacing,mCellH+mHSpacing);
        mCellPathDarkGregBottom.lineTo(mHSpacing,mCellH*0.7f+mHSpacing);
        mCellPathDarkGregBottom.quadTo(mCellW*0.6f+mHSpacing,mCellH*0.8f+mHSpacing,mCellW+mHSpacing,mCellH*0.69f+mHSpacing);
        mCellPathDarkGregBottom.lineTo(mCellW+mHSpacing,mCellH+mHSpacing);
    }
    /*private void initSelectionPath()
    {

        mSelectionPath= new Path();
        int min_cell_dim=Math.min(mCellW,mCellH);
        float corner = min_cell_dim*0.1f;
        mSelectionPath.moveTo(corner,0);
        mSelectionPath.lineTo(mCellW-corner,0);
        mSelectionPath.quadTo(mCellW,0,mCellW,corner);
        mSelectionPath.lineTo(mCellW,mCellH-corner);
        mSelectionPath.quadTo(mCellW,mCellH,mCellW-corner,mCellH);
        mSelectionPath.lineTo(corner,mCellH);
        mSelectionPath.quadTo(0,mCellH,0,mCellH-corner);
        mSelectionPath.lineTo(0,corner);
        mSelectionPath.quadTo(0,0,corner,0);

    }*/
    void drawCellBackground(Canvas c,int color_index,boolean day_sunset)
    {
        int color_dark=cell_colors[color_index][0];
        int color_light=cell_colors[color_index][1];

            mCellPaint.setColor(color_light);
        if(day_sunset) {
            c.drawPath(mCellPathLight, mCellPaint);
        }
        else {
            c.drawPath(mCellPathLightGreg, mCellPaint);
        }
            mCellPaint.setColor(color_dark);
        if(day_sunset) {
            c.drawPath(mCellPathDark, mCellPaint);
        }
        else
        {
            c.drawPath(mCellPathDarkGregTop, mCellPaint);
            c.drawPath(mCellPathDarkGregBottom, mCellPaint);
        }


        //mCellPaint.setColor(0xffffffff);
        //c.drawPath(mCellPathTipClear,mCellPaint);
    }
    public void updateMeasurements(int cellw,int cellh, int headerh, int space)
    {
        mSpacing=space;
        mHSpacing=space/2;
        mCellW=cellw;
        mCellH=cellh;
        mHeaderH=headerh;
        mCellMin=Math.min(mCellH,mCellW);
        circle_offset_x=3;
        circle_offset_y=3;
        mCellPaint.setTextAlign(Paint.Align.CENTER);
        mTransparentPaint.setTextAlign(Paint.Align.CENTER);
        if (mCellMin>12)
        {
            font_size=(float)mCellMin*0.5f;
            mTransparentPaint.setTextSize(font_size);
            mCellPaint.setTextSize(font_size);
            //mCellPaint.setTypeface(Typeface.create("serif",Typeface.NORMAL));

            Rect bounds=new Rect();
            mCellPaint.getTextBounds("ל\"",0,2,bounds);
            font_lamed_height = bounds.height();
            mCellPaint.getTextBounds("0",0,1,bounds);
            font_num_height = bounds.height();
            mDrawText=true;
        }
        else {
            mDrawText = false;
            font_size=(float)mCellMin*0.9f;
        }
        initCellPath();
        mCellBW=mCellW+2*mHSpacing;
        mCellBH=mCellH+2*mHSpacing;
        //initSelectionPath();

        mCellBitmap = Bitmap.createBitmap(cellw+2*mHSpacing, cellh+2*mHSpacing, Bitmap.Config.ARGB_8888);
        mCellBitmapSelected = Bitmap.createBitmap(cellw+2*mSpacing-2, cellh+2*mSpacing-2, Bitmap.Config.ARGB_8888);
        mCellBitmapGray = Bitmap.createBitmap(cellw+2*mHSpacing, cellh+2*mHSpacing, Bitmap.Config.ARGB_8888);
        Canvas cell_canvas =new Canvas(mCellBitmap);

        mCellPaint.setColor(0xfff3f1e2);
        cell_canvas.drawPath(mCellPathLight,mCellPaint);
        mCellPaint.setColor(0xffe8e6d0);
        cell_canvas.drawPath(mCellPathDark,mCellPaint);

        cell_canvas.drawPath(mCellPathTipClear,mTransparentPaint);

        //selection
        cell_canvas.setBitmap(mCellBitmapSelected);
        mCellPaint.setColor(0xffff8019);
        //cell_canvas.drawRect(0,0,mCellW+2*mSpacing-2,mCellH+2*mSpacing-2,mCellPaint);
        cell_canvas.drawRect(0,0,mSpacing-1,mCellH+2*mSpacing-2,mCellPaint);
        cell_canvas.drawRect(mCellW+mSpacing-1,0,mCellW+2*mSpacing-2,mCellH+2*mSpacing-2,mCellPaint);
        cell_canvas.drawRect(mSpacing-1,0,mCellW+mSpacing-1,mSpacing-1,mCellPaint);
        cell_canvas.drawRect(mSpacing-1,mCellH+mSpacing-1,mCellW+mSpacing-1,mCellH+2*mSpacing-2,mCellPaint);
        /*//cell_canvas.drawPath(mSelectionPath,mCellPaint);
        mCellPaint.setColor(0xffaecff1);
        cell_canvas.drawPath(mCellPathLight,mCellPaint);
        mCellPaint.setColor(0xff549be5);
        cell_canvas.drawPath(mCellPathDark,mCellPaint);

        cell_canvas.drawPath(mCellPathTipClear,mTransparentPaint);
        */
        //cell_canvas.drawRect(mHSpacing,mHSpacing,cellw+mHSpacing,cellh+mHSpacing,cell_paint);
        
        cell_canvas.setBitmap(mCellBitmapGray);
        mCellPaint.setColor(0xffe8e7e2);
        cell_canvas.drawPath(mCellPath,mCellPaint);

    }

    public void drawHeader(Canvas c, int x, int y,String txt)
    {
        x += mSpacing;
        mCellPaint.setColor(0xff9aa4c7);
        c.drawRect(x,y,x+mCellBW-mSpacing,y+mHeaderH,mCellPaint);
        Rect bounds = new Rect();
        mCellPaint.setTextSize(font_size*0.8f);
        mCellPaint.getTextBounds(txt,0,txt.length(),bounds);
        float text_pos_x=x+(mCellBW-mSpacing)/2.0f;
        float text_pos_y=y+(mHeaderH+bounds.height())/2.0f;
        mCellPaint.setColor(0xff000000);
        //mCellPaint.setTypeface(Typeface.create("Arial",Typeface.BOLD));

        mCellPaint.setTextAlign(Paint.Align.CENTER);
        c.drawText(txt, text_pos_x, text_pos_y, mCellPaint);
    }
    public static boolean isHebrewLetter(int letter)
    {
        return ( "א".codePointAt(0) <= letter && "ת".codePointAt(0)>= letter);
    }
    final int cell_colors[][]=
            {
                    {0xffe8e6d0,0xfff3f1e2},
                    {0xffe3d3ab,0xfff1e5c8}
            };
    public void drawCell(Canvas c, int x, int y,String txt, DrawAttributes attr)
    {
        Bitmap toDraw;
        //float txt_w=mCellPaint.measureText(txt);
        x += mHSpacing;
        y += mHSpacing;

        if (attr.before || attr.after) {

            toDraw = mCellBitmapGray;
            c.drawBitmap(toDraw,x,y,null);
        }
        else {
            int color_index=0;
            if (attr.shabbat)
            {
                color_index=1;
            }
                c.translate(x,y);
                drawCellBackground(c,color_index,attr.jewishDay);
                c.translate(-x,-y);
        }


        if (attr.selected)
        {
            toDraw = mCellBitmapSelected;
            c.drawBitmap(toDraw,x-mHSpacing+1,y-mHSpacing+1,null);
        }
        if (attr.evnet) {
            mCellPaint.setColor(0xffee7700);
            Path shifted= new Path();
            //c.drawCircle(x+circle_offset_x, y+circle_offset_y, 2, mCellPaint);
            mCellPathTip.offset(x,y,shifted);
            c.drawPath(shifted, mCellPaint);
        }

        int text_color;
        if (attr.isToday)
            text_color=0xffac213b;
        else
        {
            if (attr.before || attr.after) {
                text_color=0xff87917b;
            }
            else text_color=0xff384d1d;
        }
        mCellPaint.setColor(text_color);
        if (txt.contains(":")) {
            String[] splited=txt.split(":",2);

            mCellPaint.setTextAlign(Paint.Align.RIGHT);
            float font_scale=1.0f;
            float font_h_offset=isHebrewLetter(splited[0].codePointAt(0))?font_lamed_height:font_num_height;
            font_h_offset*=font_scale;
            mCellPaint.setTextSize(font_size*font_scale);
            float text_padding=3;
            float text_pos_x=x+mCellW+mHSpacing-text_padding;
            float text_pos_y=y+(font_h_offset)+mHSpacing+text_padding;
            c.drawText(splited[0], text_pos_x, text_pos_y, mCellPaint);

            float font_scale_small=0.72f;
            mCellPaint.setTextSize(font_size*font_scale_small);
            text_pos_x=x+mHSpacing+text_padding;
            text_pos_y=y+mCellH+mHSpacing-text_padding;
            mCellPaint.setTextAlign(Paint.Align.LEFT);


            c.drawText(splited[1], text_pos_x, text_pos_y, mCellPaint);

        }
        else
        {
            float text_pos_x=x+mHSpacing+(mCellW)/2.0f;
            float font_scale_small=1.4f;
            float font_h_offset=isHebrewLetter(txt.codePointAt(0))?font_lamed_height:font_num_height;
            font_h_offset*=font_scale_small;
            float text_pos_y=y+mHSpacing+(mCellH)/2.0f+(font_h_offset)/2.0f;
            mCellPaint.setTextAlign(Paint.Align.CENTER);
            mCellPaint.setTextSize(font_size*font_scale_small);
            c.drawText(txt, text_pos_x, text_pos_y, mCellPaint);
        }
    }
}
