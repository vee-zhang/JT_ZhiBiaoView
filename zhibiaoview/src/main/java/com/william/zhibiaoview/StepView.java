package com.william.zhibiaoview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class StepView extends View {

    private int selectedIndex = 0;

    private String[] stepTitles = {"面诊", "舌上", "舌下", "问诊"};
    private Step[] steps;

    /**
     * 砖块间距
     */
    private int space;

    /**
     * 这俩是我调了半天搞出来比较合适的尺寸，在wrap_content时会生效，你们就别改了，当然也欢迎高手闲的用反射来改这个值。
     */
//    private int minHeight = 30;

    private Paint mNormalPaint;
    private Paint mScorePaint;
    private Paint mNormalTextPaint;
    private Paint mScoreTextPaint;

    private int normalColor;
    private int scoreColor;

    float brickWidth;
    float brickHeight;


    /**
     * 预留字段，如果需要在上面插入其他图案，那么现在整个图案就需要整体下移，直接改这个值就可以了
     */
    private float top;


    public StepView(Context context) {
        this(context, null);
    }

    public StepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        initPaint();
        initScorePaint();
        initTextPaint();
        steps = new Step[stepTitles.length];
    }

    /**
     * 初始化资源
     *
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ZhiBiaoView);
        this.space = ta.getInteger(R.styleable.ZhiBiaoView_space, 1);
        this.normalColor = ta.getColor(R.styleable.ZhiBiaoView_normalColor, getResources().getColor(R.color.nomal));
        this.scoreColor = ta.getColor(R.styleable.ZhiBiaoView_scoreColor, getResources().getColor(R.color.mistake));
        this.selectedIndex = ta.getInteger(R.styleable.ZhiBiaoView_selectIndex, 0);
        ta.recycle();
    }

    /**
     * 初始化普通砖块的画笔
     */
    private void initPaint() {
        mNormalPaint = new Paint();
        mNormalPaint.setAntiAlias(true);
        mNormalPaint.setDither(true);
        mNormalPaint.setColor(this.normalColor);
    }

    /**
     * 初始化选中砖块的画笔
     */
    private void initScorePaint() {
        mScorePaint = new Paint();
        mScorePaint.setAntiAlias(true);
        mScorePaint.setDither(true);
        mScorePaint.setColor(this.scoreColor);
    }

    private void initTextPaint() {

        mScoreTextPaint = new Paint();
        mScoreTextPaint.setColor(Color.WHITE);
        mScoreTextPaint.setStrokeWidth(5);
        mScoreTextPaint.setAntiAlias(true);
        mScoreTextPaint.setTextSize(40);
        mScoreTextPaint.setStyle(Paint.Style.FILL);


        mNormalTextPaint = new Paint();
        mNormalTextPaint.setStrokeWidth(5);
        mNormalTextPaint.setAntiAlias(true);
        mNormalTextPaint.setColor(Color.parseColor("#999999"));
        mNormalTextPaint.setTextSize(40);
        mNormalTextPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取测量模式（Mode）
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        //todo 根据字的高度决定height，上下各预留一点边距

        int w = widthSpecMode == MeasureSpec.EXACTLY ? widthSpecSize : 0;
        int h = heightSpecMode == MeasureSpec.EXACTLY ? heightSpecSize : 0;

        setMeasuredDimension(w, h);

        brickWidth = ((float) w - (space * (stepTitles.length - 1))) / (float) stepTitles.length;
        brickHeight = (float) h;

        setupNormalBrick(brickWidth, brickHeight);
    }

    /**
     * 绘制未选中的砖块
     */
    private void setupNormalBrick(float brickWidth, float brickHeight) {
        float startX = 0f;
        int arcOri;
        for (int i = 0; i < stepTitles.length; i++) {
            if (i == 0) {
                arcOri = Step.ARC_LEFT;
            } else if (i == stepTitles.length - 1) {
                arcOri = Step.ARC_RIGHT;
            } else {
                arcOri = Step.ARC_NONE;
            }
            steps[i] = new Step(arcOri, stepTitles[i], startX, top, startX + brickWidth, brickHeight, mNormalPaint, mNormalTextPaint, mScorePaint, mScoreTextPaint);
            startX += (brickWidth + space);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < steps.length; i++) {
            steps[i].draw(canvas, i <= selectedIndex);
        }
    }


    /**
     * 设置间距
     *
     * @param space
     */
    public void setSpace(int space) {
        this.space = space;
        requestLayout();
    }

    /**
     * 设置普通格子的颜色
     *
     * @param normalColor
     */
    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
        invalidate();
    }

    /**
     * 设置选中格子的颜色
     *
     * @param scoreColor
     */
    public void setScoreColor(int scoreColor) {
        this.scoreColor = scoreColor;
        invalidate();
    }

    /**
     * 设置步骤
     *
     * @param stepTitles
     */
    public void setStepTitles(String... stepTitles) {
        this.stepTitles = stepTitles;
        invalidate();
    }


    public String[] getStepTitles() {
        return stepTitles;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        invalidate();
    }

    private static class Step {

        static final int ARC_NONE = 0;
        static final int ARC_LEFT = -1;
        static final int ARC_RIGHT = 1;

        Paint normalCellPaint;
        Paint normalTextPaint;

        Paint doneCellPaint;
        Paint doneTextPaint;

        String text;
        float left;
        float top;

        float textOffsetX;
        float textOffsetY;

        Path path = new Path();

        public Step(int arcOri, String text, float left, float top, float right, float bottom, Paint normalCellPaint, Paint normalTextPaint, Paint doneCellPaint, Paint doneTextPaint) {

            this.text = text;

            this.left = left;
            this.top = top;

            this.normalTextPaint = normalTextPaint;
            this.normalCellPaint = normalCellPaint;
            this.doneTextPaint = doneTextPaint;
            this.doneCellPaint = doneCellPaint;

            if (arcOri == ARC_LEFT) {
                setupStartNormalPath(left, top, right, bottom);
            } else if (arcOri == ARC_RIGHT) {
                setupEndNormalPath(left, top, right, bottom);
            } else {
                setupNormalPath(left, top, right, bottom);
            }

            Rect rect = new Rect();
            normalTextPaint.getTextBounds(text, 0, text.length(), rect);
            float textWidth = rect.width();
            float textHeight = rect.top + rect.bottom;
            float width = right - left;
            float height = bottom - top;
            textOffsetX = (width - textWidth) / 2;
            textOffsetY = (height - textHeight) / 2;
        }

        /**
         * 绘制左圆矩形
         */
        private void setupStartNormalPath(float left, float top, float right, float bottom) {
            path.moveTo(left, top);
            float arcWidth = bottom - top;
            path.arcTo(left, top, left + arcWidth, bottom, 90, 180, true);
            path.lineTo(right, top);
            path.lineTo(right, bottom);
            path.lineTo(arcWidth, bottom);
            path.close();
        }

        /**
         * 绘制右圆矩形
         */
        private void setupEndNormalPath(float left, float top, float right, float bottom) {
            path.moveTo(left, top);
            float arcWidth = bottom - top;
            path.arcTo(right - arcWidth, top, right, bottom, -90, 180, true);
            path.lineTo(left, bottom);
            path.lineTo(left, top);
            path.lineTo(right - arcWidth, top);
            path.close();
        }

        /**
         * 绘制普通矩形
         */
        private void setupNormalPath(float left, float top, float right, float bottom) {
            path.moveTo(left, top);
            path.addRect(left, top, right, bottom, Path.Direction.CW);
        }

        void draw(Canvas canvas, boolean done) {
            if (done) {
                canvas.drawPath(path, doneCellPaint);
                canvas.drawText(text, left + textOffsetX, top + textOffsetY, doneTextPaint);
            } else {
                canvas.drawPath(path, normalCellPaint);
                canvas.drawText(text, left + textOffsetX, top + textOffsetY, normalTextPaint);
            }
        }
    }
}
