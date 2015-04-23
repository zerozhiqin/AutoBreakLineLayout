package dev.misono.breaklinelayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class BreakLineLayout extends ViewGroup {

    public BreakLineLayout(Context context) {
        super(context);
    }

    public BreakLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BreakLineLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BreakLineLayout,
                0, 0
        );

        try {
            shareWidth = a.getBoolean(R.styleable.BreakLineLayout_shareWidth, true);

            int lines = a.getInteger(R.styleable.BreakLineLayout_maxLines, Integer.MAX_VALUE);

            if (lines == -1) {
                lines = Integer.MAX_VALUE;
            }
            showLines = lines;
        } finally {
            a.recycle();
        }

    }

    private int showLines = Integer.MAX_VALUE;
    private int oldShowLines = showLines;
    private boolean shareWidth = true;
    private boolean center = false;

    public void setShowCenterWhenSingleLine(boolean center) {
        this.center = center;
        requestLayout();
    }

    public boolean isShareWidth() {
        return shareWidth;
    }

    public void setShareWidth(boolean shareWidth) {
        this.shareWidth = shareWidth;
        requestLayout();
    }

    public void setShowLines(int showLines) {
        if (this.showLines == showLines) {
            return;
        }
        oldShowLines = this.showLines;
        this.showLines = showLines;
        requestLayout();
    }

    public void setShowAll() {
        if (this.showLines == Integer.MAX_VALUE) {
            return;
        }
        oldShowLines = this.showLines;
        this.showLines = Integer.MAX_VALUE;
        requestLayout();
    }

    public boolean isShowAll() {
        return this.showLines == Integer.MAX_VALUE;
    }

    class Line {
        int height;
        int width;
        ArrayList<View> childs = new ArrayList<>();

        @Override
        public String toString() {
            return "	Line [height=" + height + ", width=" + width + ", childs=" + childs.size() + "]\n";
        }
    }

    ArrayList<Line> lines = new ArrayList<Line>();

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        ViewGroup.LayoutParams lp = getLayoutParams();
        int measureHeight = 0;
        int warpHeight = 0;

        int colCount = Integer.MAX_VALUE;

        lines.clear();
        Line line = new Line();
        boolean breakLine = false;

        int index = 0;
        int viewW = measureWidth - getPaddingLeft() - getPaddingRight();
        int currentW = 0;
        int maxChildWidth = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int childW = childView.getMeasuredWidth();
            maxChildWidth = Math.max(maxChildWidth, childW);
        }

        while (index < childCount) {
            View childView = getChildAt(index);
            if (!shareWidth) {
                childView.measure(MeasureSpec.makeMeasureSpec(maxChildWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(childView.getMeasuredHeight(), MeasureSpec.EXACTLY));
            }
            LayoutParams childLp = (LayoutParams) childView.getLayoutParams();
            int childH = childView.getMeasuredHeight() + childLp.topMargin + childLp.bottomMargin;
            int childW = childView.getMeasuredWidth() + childLp.leftMargin + childLp.rightMargin;
            boolean
                    placaToPut = currentW + childW < viewW;
            if (placaToPut) {
                line.childs.add(childView);
                line.height = Math.max(line.height, childH);
                currentW += childW;
                index++;
            } else {
                if (currentW == 0) {
                    line.childs.add(childView);
                    line.width = childW;
                    line.height = childH;
                    warpHeight += childH;
                    lines.add(line);
                    if (lines.size() >= showLines) {
                        break;
                    }
                    currentW = 0;
                    line = new Line();
                    index++;
                } else {
                    warpHeight += line.height;
                    line.width = currentW;
                    lines.add(line);
                    if (lines.size() >= showLines) {
                        breakLine = true;
                        break;
                    }
                    currentW = 0;
                    colCount = line.childs.size();
                    line = new Line();
                }
            }
        }
        if (!line.childs.isEmpty() && !breakLine) {
            line.width = currentW;
            lines.add(line);
            warpHeight += line.height;
        }

        if (shareWidth) {
            for (int i = 0; i < lines.size() - 1; i++) {
                Line oneline = lines.get(i);
                int widthAdd = (measureWidth - getPaddingLeft() - getPaddingRight() - oneline.width) / oneline.childs.size();
                for (View childView : oneline.childs) {
                    childView.measure(
                            MeasureSpec.makeMeasureSpec(childView.getMeasuredWidth() + widthAdd, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(childView.getMeasuredHeight(), MeasureSpec.EXACTLY));
                }
            }
        }

        if (lp.height == LayoutParams.WRAP_CONTENT) {
            measureHeight = warpHeight + getPaddingTop() + getPaddingBottom();
        } else {
            measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = getPaddingTop();
        int left = getPaddingLeft();
        int parentW = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int index = 0;
        for (Line line : lines) {

            int widthAdd = 0;
            if (center == true && lines.size() == 1) {
                widthAdd = (parentW - line.width) / 2;
            }

            for (View childView : line.childs) {
                int realtop = (line.height - childView.getMeasuredHeight()) / 2 + top;
                LayoutParams childLp = (LayoutParams) childView.getLayoutParams();
                childView.layout(left + childLp.leftMargin + widthAdd,
                        realtop + childLp.topMargin,
                        left + childView.getMeasuredWidth() + childLp.leftMargin + widthAdd,
                        realtop + childView.getMeasuredHeight() + childLp.topMargin);

                left += childView.getMeasuredWidth() + childLp.leftMargin + childLp.rightMargin;
                if (index > oldShowLines - 1) {
                    ViewCompat.setAlpha(childView, 0);
                    ViewCompat.animate(childView).alpha(1).setDuration(300).start();
                }
            }

            index++;
            left = getPaddingLeft();
            top += line.height;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new BreakLineLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new BreakLineLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof BreakLineLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }
}