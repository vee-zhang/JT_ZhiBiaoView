package com.william.zhibiaoview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class DashBoardView extends View {

    /**
     * 实际值
     */
    private float value = 0f;

    private String titleLeft = "健康";
    private String titleRight = "不健康";

    private Point centerPoint = new Point();

    private int w;
    private int h;

    private int goodColor = Color.parseColor("#34c083");
    private int middleColor = Color.parseColor("#01a9ef");
    private int bedColor = Color.parseColor("#ed7352");
    //设置渐变的颜色范围
    int[] colors = {goodColor, middleColor, bedColor};

    int lineWidth1 = 20;
    int lineWidth2 = 60;

    int width;
    int height;

    private final int startAngle = 170;
    private final int sweepAngle = 200;

    private LinearGradient linearGradient;

    private TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    public DashBoardView(Context context) {
        this(context, null);
    }

    public DashBoardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DashBoardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 画外圈蓝色圆弧
     *
     * @param canvas
     */
    private float drawArcOutside(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineWidth1);
        mPaint.setColor(Color.parseColor("#d8f3fe"));

        float radius = width / 2 - lineWidth1 / 2 - 100; //todo 看看这里
        rectF1.left = -radius;
        rectF1.top = -radius;
        rectF1.right = radius;
        rectF1.bottom = radius;
        canvas.drawArc(rectF1, startAngle, sweepAngle, false, mPaint);
        return radius;
    }

    /**
     * 画内圈渐变弧线
     *
     * @param canvas
     */
    private void drawArcInside(Canvas canvas) {
        Path path = new Path();
        rectF2.left = rectF1.left + lineWidth1 / 2 + lineWidth2 / 2;
        rectF2.top = rectF1.top + lineWidth1 / 2 + lineWidth2 / 2;
        rectF2.right = rectF1.right - lineWidth1 / 2 - lineWidth2 / 2;
        rectF2.bottom = rectF1.bottom - lineWidth1 / 2 - lineWidth2 / 2;
        path.addArc(rectF2, startAngle, sweepAngle);
        //设置渐变的蒙版

        setPaint2(rectF2);
        canvas.drawPath(path, mPaint);
    }

    /**
     * 画刻度线
     *
     * @param canvas
     */
    private void drawSpace(Canvas canvas, float radius) {
        float radiusTop = -radius + lineWidth1 / 2;
        float radiusBottom = radiusTop + lineWidth2;
        float arg = 10;
        float x1;
        float y1;
        float x2;
        float y2;
        double pi = Math.PI / 180;
        Path pathLines = new Path();
        for (int i = 0; i < 9; i++) {
            x1 = (float) (radiusTop * Math.cos(pi * arg));
            y1 = (float) (radiusTop * Math.sin(pi * arg));
            x2 = (float) (radiusBottom * Math.cos(pi * arg));
            y2 = (float) (radiusBottom * Math.sin(pi * arg));
            pathLines.moveTo(x1, y1);
            pathLines.lineTo(x2, y2);

            arg += 20;
        }
        setPaint3();
        canvas.drawPath(pathLines, mPaint);
    }

    /**
     * 画刻度数
     *
     * @param canvas
     */
    private void drawValues(Canvas canvas) {
        Rect textRect = new Rect();
        rectF3.left = rectF2.left + lineWidth2;
        rectF3.top = rectF2.top + lineWidth2;
        rectF3.right = rectF2.right - lineWidth2;
        rectF3.bottom = rectF2.bottom - lineWidth2;
        Path pathText = new Path();
        pathText.addArc(rectF3, startAngle, sweepAngle);

        setPaint4();
        for (int i = 0, j = 0; i <= 10; i++) {
            String text = String.valueOf(j);
            mPaint.getTextBounds(text, 0, text.length(), textRect);
            // 粗略把文字的宽度视为圆心角2*θ对应的弧长，利用弧长公式得到θ，下面用于修正角度
            float leng = (float) (180 * textRect.width() / 2 /
                    (Math.PI * (rectF3.width() / 2 - textRect.height())));

            pathText.reset();
            pathText.addArc(
                    rectF3,
                    startAngle + i * (200 / 10) - leng, // 正起始角度减去θ使文字居中对准长刻度
                    sweepAngle
            );
            canvas.drawTextOnPath(text, pathText, 0, 0, mPaint);
            j += 10;
        }
    }

    /**
     * 画指针
     *
     * @param canvas
     */
    private void drawPointer(Canvas canvas, float radius) {
        float pointerRadius = radius + 30; //todo 看看这里

        float circleRadius = 15;//指针小圆半径
        float height = 100;// todo 指针的总高度
        float angle = startAngle + 200 * value / 100;

        float cx = (float) (pointerRadius * Math.cos(angle * Math.PI / 180));
        float cy = (float) (pointerRadius * Math.sin(angle * Math.PI / 180));
        float triangleX = (float) ((rectF2.width() + lineWidth2) / 2 * Math.cos(angle * Math.PI / 180));
        float triangleY = (float) ((rectF2.width() + lineWidth2) / 2 * Math.sin(angle * Math.PI / 180));


        float pointX1 = cx + (float) (circleRadius * Math.sin(angle * Math.PI / 180));
        float pointY1 = cy - (float) (circleRadius * Math.cos(angle * Math.PI / 180));

        float pointX2 = cx - (float) (circleRadius * Math.sin(angle * Math.PI / 180));
        float pointY2 = cy + (float) (circleRadius * Math.cos(angle * Math.PI / 180));

        Path pointer = new Path();
        pointer.addCircle(cx, cy, circleRadius, Path.Direction.CCW);
        pointer.moveTo(pointX1, pointY1);
        pointer.lineTo(triangleX, triangleY);
        pointer.lineTo(pointX2, pointY2);
        pointer.close();
        setPaint6();
        canvas.drawPath(pointer, mPaint);
    }

    private void setPaint2(RectF rectF) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        linearGradient = new LinearGradient(rectF.left, rectF.bottom, rectF.right, rectF.bottom, colors, null, Shader.TileMode.CLAMP);
        mPaint.setShader(linearGradient);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineWidth2);
    }

    private void setPaint3() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(1);
    }

    private void setPaint4() {
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(30);
    }

    private void setPaint6() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(middleColor);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 获取测量模式（Mode）
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        w = widthSpecMode == MeasureSpec.EXACTLY ? widthSpecSize : 0;
        h = heightSpecMode == MeasureSpec.EXACTLY ? heightSpecSize : w / 2;


        width = w * 4 / 5;
        height = h;

        centerPoint.x = w / 2;
        centerPoint.y = h * 3 / 4;

        setMeasuredDimension(w, h);
    }

    private RectF rectF1 = new RectF();
    private RectF rectF2 = new RectF();
    private RectF rectF3 = new RectF();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        Rect titleLeftRect = new Rect();
        Rect titleRightRect = new Rect();
        mPaint.getTextBounds(titleLeft, 0, titleLeft.length(), titleLeftRect);
        mPaint.getTextBounds(titleRight, 0, titleRight.length(), titleRightRect);
        //画布移到圆心
        canvas.translate(centerPoint.x, centerPoint.y);
        float radius = drawArcOutside(canvas);

        drawArcInside(canvas);

        drawSpace(canvas, radius);
        drawValues(canvas);

        drawPointer(canvas, radius);
        //画实际显示值
        drawValue(canvas);

        //画生命熵
        drawTitle(canvas);

        drawMaxMinTitle(canvas);
    }

    /**
     * 画中间显示的实际值
     *
     * @param canvas
     */
    private void drawValue(Canvas canvas) {
        String v = String.valueOf(value);
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(90);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setColor(goodColor);
        canvas.drawText(v, 0, -20, mPaint);
        Rect rect = new Rect();
        mPaint.getTextBounds(v, 0, v.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();
        int leng = 30;
        int space = 20;
        mPaint.setStrokeWidth(4);
        canvas.drawLine(-textWidth / 2 - leng - space, -textHeight / 2 - 20, -textWidth / 2 - space, -textHeight / 2 - 20, mPaint);
        canvas.drawLine(textWidth / 2 + leng + space, -textHeight / 2 - 20, textWidth / 2 + space, -textHeight / 2 - 20, mPaint);
        mPaint.setTypeface(Typeface.DEFAULT);
    }

    /**
     * 画仪表盘的值标题（生命熵）
     *
     * @param canvas
     */
    Rect eventRect = new Rect();

    private void drawTitle(Canvas canvas) {
        String text = "生命熵";
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(40);
        mPaint.setTextAlign(Paint.Align.CENTER);
        Rect r = new Rect();
        mPaint.getTextBounds(text, 0, 2, r);
        mPaint.setColor(Color.BLACK);
        canvas.drawText(text, 0, r.height(), mPaint);
        mPaint.setColor(Color.parseColor("#dbdbdb"));
        canvas.drawCircle(r.width() / 2 + r.height() + 20, r.height() / 2 + 5, r.height() / 2, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawText("?", r.width() / 2 + r.height() + 20, r.height(), mPaint);
        eventRect.left = centerPoint.x + r.width() / 2 + 20;
        eventRect.top = centerPoint.y;
        eventRect.right = eventRect.left + r.height();
        eventRect.bottom = eventRect.top + r.height();
    }

    /**
     * 画极值标示
     *
     * @param canvas
     */
    private void drawMaxMinTitle(Canvas canvas) {
        int size = 40;
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(goodColor);
        mPaint.setTextSize(size);
        mPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(titleLeft, rectF1.left, 100, mPaint);
        mPaint.setColor(bedColor);
        mPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(titleRight, rectF1.right, 100, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {


            float x = event.getX();
            float y = event.getY();

            if (x >= eventRect.left && x <= eventRect.right
                    && y >= eventRect.top && y <= eventRect.bottom) {
                Toast.makeText(getContext(), x + ":" + y, Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    public void setValue(float v){
        this.value = v;
        invalidate();
    }
}
