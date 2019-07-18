package com.william.zhibiaoview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class ZheXianView extends View {

    private int minWidth = 100;
    private int minHeight = 100;

    private TextPaint mTextPaint = new TextPaint();
    private Paint mNormalPaint = new Paint();
    private Paint mScorePaint = new Paint();
    private Paint mBorderPaint = new Paint();
    private TextPaint mUnitPaint = new TextPaint();

    private int normalColor = Color.parseColor("#dddddd");
    private int scoreColor = Color.parseColor("#34c083");

    private LinkedList<Data> dataList = new LinkedList<>();

    /**
     * 最高分
     */
    private float maxValue = 100f;
    /**
     * 步长
     */
    private float stepValue = 20f;

    /**
     * 标号文字大小
     */
    private float textSize = 28f;

    private Path dataPath;
    private Path dataPath1;

    private LinearGradient linearGradient;

    private float num = 100f;

    private LinkedList<Location> baseLineList = new LinkedList<>();
    private LinkedList<Location> pointList = new LinkedList<>();
    private LinkedList<label> labelList = new LinkedList<>();

    public ZheXianView(Context context) {
        this(context, null);
    }

    public ZheXianView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZheXianView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initTextPaint();
        initPaint();
        initScorePaint();
        initScoreBorderPaint();
    }


    /**
     * 初始化资源
     *
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ZXV);
        this.normalColor = ta.getColor(R.styleable.ZXV_zxv_normal_color, getResources().getColor(R.color.nomal));
        this.scoreColor = ta.getColor(R.styleable.ZXV_zxv_score_color, getResources().getColor(R.color.score));
        this.maxValue = ta.getFloat(R.styleable.ZXV_maxValue, 100f);
        this.stepValue = ta.getFloat(R.styleable.ZXV_stepValue, 20f);
        this.textSize = ta.getInteger(R.styleable.ZXV_textSize, 28);
        ta.recycle();
    }

    /**
     * 初始化数据
     *
     * @param dataList
     */
    public void initData(Collection<Data> dataList) {
        this.dataList.addAll(dataList);
        invalidate();
    }

    /**
     * 添加数据
     *
     * @param data
     */
    public void add(Data data) {
        dataList.add(data);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 获取测量模式（Mode）
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int w = widthSpecMode == MeasureSpec.EXACTLY ? widthSpecSize : minWidth;
        int h = heightSpecMode == MeasureSpec.EXACTLY ? heightSpecSize : minHeight;

        setMeasuredDimension(w, h);

        if (linearGradient == null) {
            linearGradient = new LinearGradient(0, 0, 0, h, scoreColor, Color.WHITE, Shader.TileMode.CLAMP);
        }
    }

    /**
     * 计算位置
     *
     * @param width
     * @param height
     */
    private void path(float width, float height) {
        num = 100f;
        float startX = num;
        float endX = width - num;
        float startY = num;
        float endY = height - num;
        float chartWidth = width - startX - num;
        float chartHeight = height - startY - num;

        //准备横向网格线
        float stepY = chartHeight / (maxValue / stepValue);
        float val = maxValue;
        baseLineList.clear();
        labelList.clear();
        for (float i = startY; i <= endY; i += stepY) {
            baseLineList.add(new Location(100f, i));
            labelList.add(new label(new Location(50f, i), String.valueOf((int) val)));
            val -= stepValue;
        }

        //准备数据路径（包括折线和点点）
        pointList.clear();
        float x = startX;
        float stepX = chartWidth / (dataList.size() - 1);
        for (int i = 0; i < dataList.size(); i++) {
            pointList.add(new Location(x, endY - dataList.get(i).value / maxValue * chartHeight));
            labelList.add(new label(new Location(x, endY + 50), dataList.get(i).label));
            x += stepX;
        }

        if (pointList.isEmpty()){
            return;
        }

        dataPath = new Path();
        dataPath1 = new Path();

        dataPath.moveTo(startX, endY);

        for (int i = 0; i < pointList.size(); i++) {
            dataPath.lineTo(pointList.get(i).x, pointList.get(i).y);
            if (i == 0) {
                dataPath1.moveTo(pointList.get(i).x, pointList.get(i).y);
            } else {
                dataPath1.lineTo(pointList.get(i).x, pointList.get(i).y);
            }
        }
        dataPath.lineTo(pointList.getLast().x, endY);
        dataPath.close();
    }

    /**
     * 初始化文字画笔
     */
    private void initTextPaint() {
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mUnitPaint.setTextSize(16);
    }

    /**
     * 初始化普通砖块的画笔
     */
    private void initPaint() {
        mNormalPaint.setColor(this.normalColor);
        mNormalPaint.setStyle(Paint.Style.STROKE);
        mNormalPaint.setStrokeWidth(2);
        mNormalPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
    }

    /**
     * 初始化选中砖块的画笔
     */
    private void initScorePaint() {
        mScorePaint.setAntiAlias(true);
        mScorePaint.setDither(true);
        mScorePaint.setColor(this.scoreColor);
    }

    private void initScoreBorderPaint() {
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setDither(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(4);
        mBorderPaint.setColor(this.scoreColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path(getWidth(), getHeight());

        //先画实际数据
        if (dataPath != null) {
            mScorePaint.setShader(linearGradient);
            canvas.drawPath(dataPath, mScorePaint);
            mScorePaint.setShader(null);
        }

        //给数据描边
        if (dataPath1 != null) {
            canvas.drawPath(dataPath1, mBorderPaint);
        }
        //画线线
        setLayerType(LAYER_TYPE_SOFTWARE, null);//关闭硬件加速，否则画不出虚线
        for (Location location : baseLineList) {
            canvas.drawLine(location.x, location.y, getWidth(), location.y, mNormalPaint);
        }

        //画小点点
        for (Location location : pointList) {
            canvas.drawCircle(location.x, location.y, 10, mScorePaint);
        }

        //画文本
        if (!labelList.isEmpty()) {
            for (label label : labelList) {
                canvas.drawText(label.text, label.location.x, label.location.y, mTextPaint);
            }
            canvas.drawText("分", labelList.getFirst().location.x + 30f, labelList.getFirst().location.y, mUnitPaint);
            canvas.drawText("日期", labelList.getLast().location.x + 40f, labelList.getLast().location.y, mUnitPaint);
        }
    }

    private static class Location {
        float x;
        float y;

        public Location(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Data {
        private String label;
        private float value;

        public Data(String label, float value) {
            this.label = label;
            this.value = value;
        }
    }

    private static class label {
        Location location;
        String text;

        public label(Location location, String text) {
            this.location = location;
            this.text = text;
        }
    }

    public int getNormalColor() {
        return normalColor;
    }

    /**
     * 设置基底颜色（线线）
     *
     * @param normalColor
     */
    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public int getScoreColor() {
        return scoreColor;
    }

    /**
     * 设置折线区域颜色
     *
     * @return
     */
    public void setScoreColor(int scoreColor) {
        this.scoreColor = scoreColor;
    }

    public float getMaxValue() {
        return maxValue;
    }

    /**
     * 设置最大值
     *
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getStepValue() {
        return stepValue;
    }

    /**
     * 设置纵向分值的步长
     *
     * @param stepValue
     */
    public void setStepValue(float stepValue) {
        this.stepValue = stepValue;
    }

    public float getTextSize() {
        return textSize;
    }

    /**
     * 设置label文字size
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }
}
