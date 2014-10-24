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


public class TopConnerRoundImageView extends AutoAdjustHeightImageView {

    private final RectF roundRect = new RectF();
    private final Rect topLeftRect = new Rect();
    private final Rect topRightRect = new Rect();
    private float rect_adius = getResources().getDimensionPixelSize(R.dimen.pinterest_image_conner_radius);
    private final Paint maskPaint = new Paint();
    private final Paint zonePaint = new Paint();


    public TopConnerRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        maskPaint.setAntiAlias(true);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        zonePaint.setAntiAlias(true);
        zonePaint.setColor(Color.WHITE);
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
        topLeftRect.set(0, h / 2, w / 2, h);
        topRightRect.set(w / 2, h / 2, w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawRoundRect(roundRect, rect_adius, rect_adius, zonePaint);
        // Fill in upper left corner
        canvas.drawRect(topLeftRect, zonePaint);
        // Fill in upper right corners
        canvas.drawRect(topRightRect, zonePaint);
        canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        canvas.restore();
    }
}