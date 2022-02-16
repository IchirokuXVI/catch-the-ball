package iestrassierra.dcorsan.catchball;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class BallView extends View {

    private int size;
    private int speed;
    private Paint paint;
    private float angle;
    private boolean bounce;
    private boolean towardsX;
    private boolean towardsY;

    public BallView tmpBall;

    public BallView(Context context, int size, int speed, boolean bounce, Paint paint) {
        super(context);
        this.size = size;
        this.speed = speed;
        this.paint = paint;

        this.angle = (float) ((Math.random() * 90) * Math.PI / 180);

        this.bounce = bounce;

        towardsX = Math.random() < 0.5;
        towardsY = Math.random() < 0.5;

        tmpBall = new BallView(this.getContext(), size, speed, paint, angle, bounce, towardsX, towardsY);
        setTmpBallVisiblity(View.GONE);
    }

    public BallView(Context context, int size, int speed, Paint paint, float angle, boolean bounce, boolean towardsX, boolean towardsY) {
        super(context);
        this.size = size;
        this.speed = speed;
        this.paint = paint;
        this.angle = angle;
        this.bounce = bounce;
        this.towardsX = towardsX;
        this.towardsY = towardsY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
    }

    public boolean isFullyOnScreenExceptBorder(float scrWidth, float scrHeight) {
        if (this.getParent() == null)
            return false;

        float limitX = scrWidth - size;
        float limitY = scrHeight - size;
        boolean touchingStartX = !towardsX && getX() < 0;
        boolean touchingEndX = towardsX && getX() > limitX;
        boolean touchingStartY = !towardsY && getY() < 0;
        boolean touchingEndY = towardsY && getY() > limitY;

        if (towardsX && towardsY)
            return !(touchingEndX && touchingEndY);
        else if (!towardsX && !towardsY)
            return !(touchingStartX && touchingStartY);
        else if (!towardsX && towardsY)
            return !(touchingStartX && touchingEndY);
        else // if (towardsX && !towardsY) // Last possible case
            return !(touchingEndX && touchingStartY);
    }

    public boolean isOnScreen(float scrWidth, float scrHeight) {
        return this.getParent() != null && this.getX() > 0 - size && this.getX() < scrWidth && this.getY() > 0 - size && this.getY() < scrHeight;
    }

    public void checkBorders(float scrWidth, float scrHeight) {
        float limitX = scrWidth - size;
        float limitY = scrHeight - size;
        boolean touchingStartX = !towardsX && getX() < 1;
        boolean touchingEndX = towardsX && getX() > limitX;
        boolean touchingStartY = !towardsY && getY() < 1;
        boolean touchingEndY = towardsY && getY() > limitY;

        if (bounce) {
            if (touchingStartX)
                towardsX = true;
            else if (touchingEndX)
                towardsX = false;

            if (touchingStartY)
                towardsY = true;
            else if (touchingEndY)
                towardsY = false;

        } else if (tmpBall != null && tmpBall.getVisibility() == View.GONE && (touchingStartX || touchingEndX || touchingStartY || touchingEndY)) {
            float finalX = getX();
            float finalY = getY();

            setTmpBallVisiblity(View.VISIBLE);

            if (touchingStartX)
                finalX = getX() + scrWidth;
            else if (touchingEndX)
                finalX = getX() - scrWidth;

            if (touchingStartY)
                finalY = getY() + scrHeight;
            else if (touchingEndY)
                finalY = getY() - scrHeight;

            tmpBall.setX(finalX);
            tmpBall.setY(finalY);
        } else if (tmpBall != null && tmpBall.getVisibility() == View.VISIBLE && !this.isOnScreen(scrWidth, scrHeight) && tmpBall.isFullyOnScreenExceptBorder(scrWidth, scrHeight)) {
            replaceTmpBall();
        }
    }

    public void move(int scrWidth, int scrHeight) {
        checkBorders(scrWidth, scrHeight);

        this.setX((float) (getX() + Math.cos(angle) * (towardsX ? speed : -speed)));
        this.setY((float) (getY() + Math.sin(angle) * (towardsY ? speed : -speed)));

        if (!bounce && tmpBall != null && tmpBall.getVisibility() == View.VISIBLE) {
            tmpBall.move(scrWidth, scrHeight);
        }
    }

    public void replaceTmpBall() {
        setTmpBallVisiblity(View.GONE);
        this.setX(tmpBall.getX());
        this.setY(tmpBall.getY());
    }

    public void setTmpBallVisiblity(int visiblity) {
        ((Activity) this.getContext()).runOnUiThread(() -> {
            tmpBall.setVisibility(visiblity);
        });
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public boolean isBounce() {
        return bounce;
    }

    public void setBounce(boolean bounce) {
        this.bounce = bounce;

        if (tmpBall != null)
            setTmpBallVisiblity(View.GONE);
    }

    public boolean isTowardsX() {
        return towardsX;
    }

    public void setTowardsX(boolean towardsX) {
        this.towardsX = towardsX;
    }

    public boolean isTowardsY() {
        return towardsY;
    }

    public void setTowardsY(boolean towardsY) {
        this.towardsY = towardsY;
    }

    @Override
    public String toString() {
        return "BallView{" +
                "size=" + size +
                ", speed=" + speed +
                ", angle=" + angle +
                ", bounce=" + bounce +
                ", x=" + getX() +
                ", y=" + getY() +
                ", towardsX=" + towardsX +
                ", towardsY=" + towardsY +
                ", tmpBall=" + tmpBall +
                '}';
    }
}
