package kapandaria.maluach2go;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;

import android.view.View;


public class Compass extends View implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;
    private float currentAzimuth = 0;
    Paint painter;
    Path arrowPath;
    float minWH;
    float Radius;
    float font_height;

    public Compass(Context context) {
        super(context);
        init(context,null,0);
    }
    public Compass(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public Compass(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }
    private void init(Context context, AttributeSet attrs, int defStyle)
    {
        painter=new Paint(Paint.ANTI_ALIAS_FLAG);
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (getWidth() > 0 && getHeight() > 0)
            invalidateMeasurements();
    }

    private void invalidateMeasurements() {
        arrowPath = new Path();
        minWH=Math.min(getWidth(),getHeight())*0.98f;
        Radius=minWH/2.0f;
        arrowPath.moveTo((-0.06f*Radius),0);
        arrowPath.lineTo( (0.06f*Radius),0);
        arrowPath.lineTo(0, 0.9f*Radius);
        arrowPath.lineTo( (-0.06f*Radius),0);
        Rect bounds=new Rect();
        painter.getTextBounds("N",0,1,bounds);
        font_height=bounds.height()/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.save();
        {
            canvas.translate(getWidth() / 2, getHeight() / 2);
            {
                painter.setStyle(Paint.Style.STROKE);
                painter.setStrokeWidth(3);
                canvas.drawCircle(0, 0, Radius, painter);
                float CapsRadius=0.8f*Radius;
                painter.setStyle(Paint.Style.FILL);
                painter.setTextAlign(Paint.Align.CENTER);
                painter.setTextSize(Radius/8);

                canvas.drawText("N",0,-CapsRadius+font_height,painter);
                canvas.drawText("E",CapsRadius,0+font_height,painter);

                canvas.drawText("S",0,CapsRadius+font_height,painter);
                canvas.drawText("W",-CapsRadius,+font_height,painter);

                float innerLineRadius=0.87f*Radius;
                float points[]={
                        Radius,0,innerLineRadius,0,
                        -Radius,0,-innerLineRadius,0,
                        0,-Radius,0,-innerLineRadius,
                        0,Radius,0,innerLineRadius,
                };
                canvas.drawLines(points,painter);


            }
            painter.setStyle(Paint.Style.FILL);
            painter.setColor(0xffff0000);
            canvas.rotate(-currentAzimuth + 180);
            canvas.drawPath(arrowPath, painter);
            painter.setColor(0xffaaaaaa);
            canvas.rotate(180);
            canvas.drawPath(arrowPath, painter);

        }
        canvas.restore();
    }
    public void start() {
        sensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    private void adjustArrow() {
        currentAzimuth = azimuth;
        invalidate();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.95f;//for low pass filtering

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mGravity[0] = alpha * mGravity[0] + (1 - alpha)
                        * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha)
                        * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha)
                        * event.values[2];

            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                // mGeomagnetic = event.values;

                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
                        * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
                        * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
                        * event.values[2];


            }

            float R[] = new float[9];
            //float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, null, mGravity,
                    mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth + 360) % 360;

                adjustArrow();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        invalidateMeasurements();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
