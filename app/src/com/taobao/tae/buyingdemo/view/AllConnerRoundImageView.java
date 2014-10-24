package com.taobao.tae.buyingdemo.view;
/*
 * Copyright (C) 2013 Siddhesh S Shetye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;

import com.taobao.tae.buyingdemo.R;


/**
 * The Class RoundImageView. All connner is round.<br/>
 * <code>RoundImageView</code><br/> Is a Custom <code>ImageView</code> class.<br/> which
 * provides all the functionalities of rounded edges for the <code>ImageView</code> with gradient effect.<br/>
 * This Class is dependent on StreamDrawable from <b>RomainGuy</b> in his Image With Rounded Corners Demo but with little modifications.
 * @author siddhesh
 * @version 1.2
 * @see <a href ="https://android.googlesource.com/platform/frameworks/volley">Volley<a/>
 * @see <a href ="https://android.googlesource.com/platform/frameworks/volley/+/master/src/com/android/volley/toolbox/NetworkImageView.java">NetworkImageView<a/>
 * @see <a href ="http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/">Rounded Corner Images<a/>
 */
public class AllConnerRoundImageView extends AutoAdjustHeightImageView{

    private final RectF roundRect = new RectF();
    private float rect_adius = getResources().getDimensionPixelSize(R.dimen.pinterest_image_conner_radius);
    private final Paint maskPaint = new Paint();
    private final Paint zonePaint = new Paint();


    public AllConnerRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        maskPaint.setAntiAlias(true);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //
        zonePaint.setAntiAlias(true);
        zonePaint.setColor(Color.WHITE);
        //
        float density = getResources().getDisplayMetrics().density;
        rect_adius = rect_adius * density;
    }

    public void setRectAdius(float adius) {
        rect_adius = adius;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int w = getWidth();
        int h = getHeight();
        roundRect.set(0, 0, w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawRoundRect(roundRect, rect_adius, rect_adius, zonePaint);
        //
        canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        canvas.restore();
    }
}