package iestrassierra.dcorsan.catchball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    FrameLayout container;
    final Handler handler = new Handler();
    Sensor proximitySensor;
    BallView ball;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //Se registra el sensor de proximidad
        List<Sensor> listSensor = sensorManager.getSensorList(Sensor.TYPE_PROXIMITY);
        if (!listSensor.isEmpty()) {
            proximitySensor = listSensor.get(0);
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_GAME);
        }

        System.out.println("Max proximity: " + proximitySensor.getMaximumRange());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int scrHeight = displayMetrics.heightPixels - getNavigationBarHeight();
        int scrWidth = displayMetrics.widthPixels;

        int size = 150;
        int speed = 15;
        boolean bounce = true;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(255, (int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));

        ball = new BallView(this, size, speed, bounce, paint);

        ball.setOnTouchListener((view, event) -> {
            if (Math.sqrt(Math.pow((event.getX() - ball.getSize()/2),2) + Math.pow((event.getY() - ball.getSize()/2),2)) <= ball.getSize() / 2) {
                Toast.makeText(this, "Bola pillada", Toast.LENGTH_SHORT).show();
            }

            return true;
        });

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        runOnUiThread(() -> {
            container.addView(ball, params);
            container.addView(ball.tmpBall, params);
        });

        int margin = 100;
        ball.setY((int)(Math.random() * (scrHeight - (size + margin))));
        ball.setX((int)(Math.random() * (scrWidth - (size + margin))));

        handler.post(new Runnable() {
            @Override
            public void run() {
                ball.move(scrWidth, scrHeight);
                handler.postDelayed(this, 1000/60);
            }
        });
    }

    private int getNavigationBarHeight() {
        Resources resources = this.getResources();

        int id = resources.getIdentifier(
                resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                        "navigation_bar_height" : "navigation_bar_height_landscape",
                "dimen", "android");

        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }
        return 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY)
        {
            float proximity = event.values[0];

            float maxProximity = proximitySensor.getMaximumRange();

            if (proximity < maxProximity / 3) {
                ball.setSpeed(50);
            } else if (proximity < maxProximity / 2) {
                ball.setSpeed(30);
            } else {
                ball.setSpeed(15);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}