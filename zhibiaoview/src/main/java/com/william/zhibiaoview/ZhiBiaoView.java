package com.william.zhibiaoview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class ZhiBiaoView extends View {

    /**
     * 正常的分数
     */
    private int normalScore;

    /**
     * 实际得分
     */
    private int score;

    /**
     * 砖块间距
     */
    private int space;

    /**
     * 这俩是我调了半天搞出来比较合适的尺寸，在wrap_content时会生效，你们就别改了，当然也欢迎高手闲的用反射来改这个值。
     */
    private int minWidth = 30;
    private int minHeight = 30;

    private Paint mNormalPaint;
    private Paint mScorePaint;

    private int normalColor;
    private int scoreColor;

    private Path mNormalPath;
    private Path mScorePath;


    /**
     * 预留字段，如果需要在上面插入其他图案，那么现在整个图案就需要整体下移，直接改这个值就可以了
     */
    private float top;


    public ZhiBiaoView(Context context) {
        this(context, null);
    }

    public ZhiBiaoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        initPaint();
        initScorePaint();
    }

    /**
     * 初始化资源
     *
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ZhiBiaoView);
        this.normalScore = ta.getInteger(R.styleable.ZhiBiaoView_normalScore, 10);
        this.score = ta.getInteger(R.styleable.ZhiBiaoView_score, 0);
        this.space = ta.getInteger(R.styleable.ZhiBiaoView_space, 2);
        this.normalColor = ta.getColor(R.styleable.ZhiBiaoView_normalColor, getResources().getColor(R.color.nomal));
        this.scoreColor = ta.getColor(R.styleable.ZhiBiaoView_scoreColor, getResources().getColor(R.color.score));
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

        float brickWidth = ((float) w - (space * (normalScore - 1))) / (float) normalScore;
        float brickHeight = ((float) h) * 2 / 3;
        float arcWidth = brickHeight;

        setupNormalBrick(brickWidth, brickHeight, arcWidth);
    }

    /**
     * 绘制左圆矩形
     */
    private void setupStartNormalPath(Path path, float startX, float brickWidth, float brickHeight, float arcWidth) {
        path.arcTo(startX, top, startX + arcWidth, brickHeight, 90, 180, true);
        path.lineTo(brickWidth, top);
        path.lineTo(brickWidth, brickHeight);
        path.lineTo(arcWidth, brickHeight);
        path.close();
    }

    /**
     * 绘制右圆矩形
     *
     * @param startX
     */
    private void setupEndNormalPath(Path path, float startX, float brickWidth, float brickHeight, float arcWidth) {
        path.arcTo(startX + brickWidth - arcWidth, top, startX + brickWidth, brickHeight, -90, 180, true);
        path.lineTo(startX, brickHeight);
        path.lineTo(startX, top);
        path.lineTo(startX + brickWidth - arcWidth, top);
        path.close();
    }

    /**
     * 绘制普通矩形
     *
     * @param startX
     */
    private void setupNormalPath(Path path, float startX, float brickWidth, float brickHeight) {
        path.addRect(startX, top, startX + brickWidth, brickHeight, Path.Direction.CW);
    }

    /**
     * 绘制三角形
     */
    private void setupTrianglePath(Path path, float startX, float brickWidth, float brickHeight) {
        float arrowHeight = brickHeight / 2;
        float centerPointX = startX + brickWidth / 2;
        double a = Math.toRadians(30d);
        double tan = Math.tan(a);
        float helfWidth = (float) (arrowHeight * tan);
        path.moveTo(centerPointX - helfWidth, top + brickHeight);
        path.lineTo(centerPointX, top + brickHeight + arrowHeight);
        path.lineTo(centerPointX + helfWidth, top + brickHeight);
        path.close();
    }

    /**
     * 绘制未选中的砖块
     */
    private void setupNormalBrick(float brickWidth, float brickHeight, float arcWidth) {
        mNormalPath   = new Path();
        mScorePath = new Path();
        float startX = 0f;
        for (int i = 0; i < normalScore; i++) {
            if (i != score - 1) {
                if (i == 0) {
                    setupStartNormalPath(mNormalPath, startX, brickWidth, brickHeight, arcWidth);
                } else if (i == normalScore - 1) {
                    setupEndNormalPath(mNormalPath, startX, brickWidth, brickHeight, arcWidth);
                    break;
                } else {
                    setupNormalPath(mNormalPath, startX, brickWidth, brickHeight);
                }
            } else {
                setupScoreBrick(startX, brickWidth, brickHeight, arcWidth);
            }
            startX += (brickWidth + space);
        }
    }

    /**
     * 绘制选中的砖块
     */
    private void setupScoreBrick(float x, float brickWidth, float brickHeight, float arcWidth) {
        int scoreIndex = score - 1;
        if (scoreIndex == 0) {
            setupStartNormalPath(mScorePath, x, brickWidth, brickHeight, arcWidth);
        } else if (scoreIndex == normalScore - 1) {
            setupEndNormalPath(mScorePath, x, brickWidth, brickHeight, arcWidth);
        } else {
            setupNormalPath(mScorePath, x, brickWidth, brickHeight);
        }
        setupTrianglePath(mScorePath, x, brickWidth, brickHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(mNormalPath, mNormalPaint);
        canvas.drawPath(mScorePath, mScorePaint);
    }

    /**
     * 设置正常的值
     * @param normalScore
     */
    public void setNormalScore(int normalScore) {
        this.normalScore = normalScore;
        requestLayout();
    }

    /**
     * 设置实际的值
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
        requestLayout();
    }

    /**
     * 设置间距
     * @param space
     */
    public void setSpace(int space) {
        this.space = space;
        requestLayout();
    }

    /**
     * 设置普通格子的颜色
     * @param normalColor
     */
    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
        invalidate();
    }

    /**
     * 设置选中格子的颜色
     * @param scoreColor
     */
    public void setScoreColor(int scoreColor) {
        this.scoreColor = scoreColor;
        invalidate();
    }
}
