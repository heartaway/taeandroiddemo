package com.taobao.tae.buyingdemo.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taobao.tae.buyingdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>自定义带下划线指示器的文本，事件可通过TextView的Onlick事件触发，无ViewPager的左右滑动效果</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/21
 * Time: 下午11:56
 */
public class UnderLineTextIndicator extends LinearLayout {

    private List<UnderLineTextButton> underlineTextViews;

    /**
     * 等间距平分父View宽度，并居中显示
     */
    public static final int SPACE_EQUAL_DIVIDE = 1;

    /**
     * 等间距追加
     */
    public static final int SAPCE_EQUAL_APPAND = 2;

    /**
     * 默认的间距方式
     */
    private static int SPACE_MODE = SPACE_EQUAL_DIVIDE;

    /**
     * 当采用追加左间距的方式时，默认的间距大小
     */
    private static int DEFAULT_MARGIN_LEFT_SIZE = 16;

    /**
     * 默认情况下，底部下划线的宽度与顶部字体的宽度是一致的，但是用户可以追加线条的宽度，默认追加宽度为0
     */
    private static int UNDER_LINE_APPAND_WIDTH = 0;

    /**
     * 标题字体大小
     */
    private static int textSizeResourceId = 0;

    /**
     * 指示器距离文字的距离
     */
    private int titlePaddingBottomLine = 0;

    /**
     * 线的高度
     */
    private int lineHeight = 4;


    public UnderLineTextIndicator(Context context) {
        super(context);
    }

    public UnderLineTextIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initView(Context context, String[] titls) {
        underlineTextViews = new ArrayList<UnderLineTextButton>();
        for (int i = 0; i < titls.length; i++) {
            final UnderLineTextButton underLineTextButton = new UnderLineTextButton(context);
            if (textSizeResourceId != 0) {
                underLineTextButton.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(textSizeResourceId));
            }
            underLineTextButton.initTextAndLine(titls[i]);
            if (SPACE_MODE == SPACE_EQUAL_DIVIDE) {
                int singleItemWidth = getScreenWidth() / titls.length;
                LayoutParams layoutParams = new LayoutParams(singleItemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                underLineTextButton.setLayoutParams(layoutParams);
            }
            if (SPACE_MODE == SAPCE_EQUAL_APPAND) {
                LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(DEFAULT_MARGIN_LEFT_SIZE, 0, 0, 0);
                underLineTextButton.setLayoutParams(layoutParams);
            }
            underlineTextViews.add(underLineTextButton);
            this.addView(underLineTextButton);
        }
        if (underlineTextViews.size() > 0) {
            underlineTextViews.get(0).setSelected();
        }
    }

    public void initView(Context context, String[] titls, int underLineAppandWidthResourId) {
        UNDER_LINE_APPAND_WIDTH = getResources().getDimensionPixelSize(underLineAppandWidthResourId);
        this.initView(context, titls);
    }

    public void initView(Context context, String[] titls, int spaceMode, int underLineAppandWidthResourid) {
        setItemSpaceMode(spaceMode);
        UNDER_LINE_APPAND_WIDTH = getResources().getDimensionPixelSize(underLineAppandWidthResourid);
        this.initView(context, titls);
    }


    /**
     * 设置字体的大小
     *
     * @param resourceId
     */
    public void setTextSizeResourceId(int resourceId) {
        this.textSizeResourceId = resourceId;
    }

    /**
     * 设置每个标题的点击监听事件
     *
     * @param position
     * @param listener
     */
    public void setOnClickListener(int position, OnClickListener listener) {
        if (underlineTextViews == null || position < 0 || position >= underlineTextViews.size()) {
            return;
        }
        underlineTextViews.get(position).getTextView().setOnClickListener(listener);
    }

    /**
     * 设置 postion 被单击后选中的状态
     *
     * @param position
     */
    public void setSelected(int position) {
        underlineTextViews.get(position).setSelected();
    }


    /**
     * 设置元素间距的方式
     *
     * @param mode
     */
    private void setItemSpaceMode(int mode) {
        if (mode == SPACE_EQUAL_DIVIDE) {
            SPACE_MODE = SPACE_EQUAL_DIVIDE;
        }
        if (mode == SAPCE_EQUAL_APPAND) {
            SPACE_MODE = SAPCE_EQUAL_APPAND;
        }
    }


    /**
     * 获取屏幕宽度
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }


    public class UnderLineTextButton extends LinearLayout {

        /* 是否处于选中状态*/
        private boolean isSelecked;
        /* 文本框 */
        private TextView textView;
        /* 下划线 */
        private LinearLayout bottomLine;

        public UnderLineTextButton(Context context) {
            super(context);
            View view = LayoutInflater.from(context).inflate(R.layout.underline_text_button, this, true);
            bottomLine = (LinearLayout) view.findViewById(R.id.underline_line);
            textView = (TextView) view.findViewById(R.id.underline_text);
            isSelecked = false;
        }

        /**
         * 初始化标题和底部线的宽度
         *
         * @param title
         */
        public void initTextAndLine(String title) {
            this.textView.setText(title);
            this.textView.setPadding(0,0,0, titlePaddingBottomLine);
            Paint textPaint=new Paint();
            textPaint.setTextSize(this.textView.getTextSize());
            Float textLength = new Float(textPaint.measureText(title));
            setBottomLineWidth(textLength.intValue());
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            textView.setLayoutParams(layoutParams);

        }

        /**
         * 设置状态为选中状态
         */
        public void setSelected() {
            if (isSelecked) {
                return;
            }
            LinearLayout linearLayoutGroup = (LinearLayout) this.getParent();
            if (linearLayoutGroup != null) {
                int child = linearLayoutGroup.getChildCount();
                for (int i = 0; i < child; i++) {
                    ((UnderLineTextButton) linearLayoutGroup.getChildAt(i)).setUnSelected();
                }
            }
            isSelecked = true;
            lineSelected();
        }

        /**
         * 根据 Text 的大小初始化线的宽度
         */
        private void lineSelected() {
            this.bottomLine.setVisibility(View.VISIBLE);
        }

        /**
         * RedioButton 没有被选中
         */
        private void lineUnSelected() {
            this.bottomLine.setVisibility(View.GONE);
        }

        /**
         * 设置下划线的宽度
         *
         * @param width
         */
        private void setBottomLineWidth(int width) {
            LayoutParams layoutParams = new LayoutParams(width + UNDER_LINE_APPAND_WIDTH, lineHeight);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            this.bottomLine.setLayoutParams(layoutParams);
        }

        /**
         * 设置为非选中状态，用户无需关心此状态
         */
        private void setUnSelected() {
            if (!isSelecked) {
                return;
            }
            lineUnSelected();
            isSelecked = false;
        }

        public TextView getTextView() {
            return textView;
        }

    }



    public void setTitlePaddingBottomLine(int lineMarginTop) {
        this.titlePaddingBottomLine = lineMarginTop;
    }


    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }
}
