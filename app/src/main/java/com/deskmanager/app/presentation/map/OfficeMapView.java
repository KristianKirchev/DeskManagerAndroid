package com.deskmanager.app.presentation.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.deskmanager.app.R;
import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.domain.enums.DeskStatus;

import java.util.ArrayList;
import java.util.List;

public class OfficeMapView extends View {

    private static final int MAX_COLS = 3;
    private static final int MAX_ROWS = 4;
    public static final int MAX_DESKS = MAX_COLS * MAX_ROWS;

    private float roomPadding;
    private float deskMargin;
    private float cellW, cellH;
    private float circleRadius;

    private final Paint roomFillPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint roomBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint deskFillPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint deskBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint      = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint subTextPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint greenPaint     = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint redPaint       = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shadowPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint emptyFillPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint emptyBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private List<DeskEntity> desks = new ArrayList<>();

    private final List<RectF> deskRects = new ArrayList<>();

    public interface OnDeskClickListener {
        void onDeskClick(DeskEntity desk);
    }

    private OnDeskClickListener clickListener;

    public OfficeMapView(Context context) {
        super(context);
        init();
    }

    public OfficeMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OfficeMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        boolean dark = isDarkMode();

        roomFillPaint.setColor(Color.parseColor(dark ? "#1E1E1E" : "#F0F4F8"));
        roomFillPaint.setStyle(Paint.Style.FILL);

        roomBorderPaint.setColor(Color.parseColor(dark ? "#455A64" : "#455A64"));
        roomBorderPaint.setStyle(Paint.Style.STROKE);
        roomBorderPaint.setStrokeWidth(4f);

        deskFillPaint.setColor(Color.parseColor(dark ? "#2C2C2C" : "#FFFFFF"));
        deskFillPaint.setStyle(Paint.Style.FILL);

        deskBorderPaint.setColor(Color.parseColor(dark ? "#546E7A" : "#90A4AE"));
        deskBorderPaint.setStyle(Paint.Style.STROKE);
        deskBorderPaint.setStrokeWidth(2f);

        textPaint.setColor(Color.parseColor(dark ? "#EEEEEE" : "#212121"));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(36f);
        textPaint.setFakeBoldText(true);

        subTextPaint.setColor(Color.parseColor(dark ? "#BDBDBD" : "#757575"));
        subTextPaint.setTextAlign(Paint.Align.CENTER);
        subTextPaint.setTextSize(26f);

        greenPaint.setColor(Color.parseColor("#4CAF50"));
        greenPaint.setStyle(Paint.Style.FILL);

        redPaint.setColor(Color.parseColor("#F44336"));
        redPaint.setStyle(Paint.Style.FILL);

        shadowPaint.setColor(Color.parseColor(dark ? "#616161" : "#BDBDBD"));
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setStrokeWidth(1.5f);

        emptyFillPaint.setColor(Color.parseColor(dark ? "#2A2A2A" : "#E0E0E0"));
        emptyFillPaint.setStyle(Paint.Style.FILL);

        emptyBorderPaint.setColor(Color.parseColor(dark ? "#424242" : "#BDBDBD"));
        emptyBorderPaint.setStyle(Paint.Style.STROKE);
        emptyBorderPaint.setStrokeWidth(1.5f);
    }

    public void setDesks(List<DeskEntity> desks) {
        this.desks = desks != null ? desks : new ArrayList<>();
        invalidate();
    }

    public void setOnDeskClickListener(OnDeskClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        recomputeGeometry(w, h);
    }

    private void recomputeGeometry(int w, int h) {
        float density = getContext().getResources().getDisplayMetrics().density;
        roomPadding  = 16f * density;
        deskMargin   = 12f * density;
        circleRadius = 10f * density;

        float innerW = w - roomPadding * 2;
        float innerH = h - roomPadding * 2;

        cellW = (innerW - deskMargin * (MAX_COLS + 1)) / MAX_COLS;
        cellH = (innerH - deskMargin * (MAX_ROWS + 1)) / MAX_ROWS;

        textPaint.setTextSize(Math.min(cellW, cellH) * 0.22f);
        subTextPaint.setTextSize(Math.min(cellW, cellH) * 0.16f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int vw = getWidth();
        int vh = getHeight();

        if (vw == 0 || vh == 0) return;

        RectF roomRect = new RectF(roomPadding, roomPadding,
                                   vw - roomPadding, vh - roomPadding);
        canvas.drawRoundRect(roomRect, 16f, 16f, roomFillPaint);
        canvas.drawRoundRect(roomRect, 16f, 16f, roomBorderPaint);

        deskRects.clear();

        int count = Math.min(desks.size(), MAX_DESKS);

        for (int i = 0; i < MAX_COLS * MAX_ROWS; i++) {
            int col = i % MAX_COLS;
            int row = i / MAX_COLS;

            float left   = roomPadding + deskMargin + col * (cellW + deskMargin);
            float top    = roomPadding + deskMargin + row * (cellH + deskMargin);
            float right  = left + cellW;
            float bottom = top  + cellH;

            RectF cell = new RectF(left, top, right, bottom);

            if (i < count) {
                drawDesk(canvas, cell, desks.get(i));
                deskRects.add(cell);
            } else {
                drawEmptySlot(canvas, cell);
                deskRects.add(null);
            }
        }
    }

    private void drawDesk(Canvas canvas, RectF cell, DeskEntity desk) {
        canvas.drawRoundRect(cell, 12f, 12f, deskFillPaint);
        canvas.drawRoundRect(cell, 12f, 12f, deskBorderPaint);

        float cx = cell.centerX();
        float cy = cell.centerY();

        canvas.drawText(desk.getRoomNumber(), cx, cy - subTextPaint.getTextSize() * 0.4f, textPaint);

        canvas.drawText(getContext().getString(R.string.label_desk_floor, desk.getFloor()), cx,
                cy + textPaint.getTextSize() * 0.8f, subTextPaint);

        float circleX = cell.right  - circleRadius - 6f;
        float circleY = cell.top    + circleRadius + 6f;

        Paint circleFill = (desk.getStatus() == DeskStatus.AVAILABLE) ? greenPaint : redPaint;
        canvas.drawCircle(circleX, circleY, circleRadius, circleFill);
        canvas.drawCircle(circleX, circleY, circleRadius, shadowPaint);
    }

    private void drawEmptySlot(Canvas canvas, RectF cell) {
        canvas.drawRoundRect(cell, 12f, 12f, emptyFillPaint);
        canvas.drawRoundRect(cell, 12f, 12f, emptyBorderPaint);
    }

    private boolean isDarkMode() {
        return (getContext().getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) return true;

        float x = event.getX();
        float y = event.getY();

        for (int i = 0; i < deskRects.size(); i++) {
            RectF rect = deskRects.get(i);
            if (rect != null && rect.contains(x, y)) {
                if (clickListener != null && i < desks.size()) {
                    clickListener.onDeskClick(desks.get(i));
                }
                return true;
            }
        }
        return true;
    }
}
