package net.kdt.pojavlaunch;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/** Lightweight ambient crimson particles for the ZYRO home backdrop. */
public class ZyroAmbientView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float[] x = {0.12f, 0.31f, 0.56f, 0.79f, 0.91f, 0.43f, 0.68f};
    private final float[] y = {0.22f, 0.73f, 0.16f, 0.62f, 0.34f, 0.46f, 0.88f};
    private final float[] radius = {2.2f, 1.5f, 2.8f, 1.8f, 1.3f, 2.0f, 1.6f};
    private long phase;
    private ValueAnimator animator;

    public ZyroAmbientView(Context context) { super(context); init(); }
    public ZyroAmbientView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public ZyroAmbientView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        paint.setStyle(Paint.Style.FILL);
        setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(18000L);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
            phase = (long) (Float.floatToIntBits((Float) a.getAnimatedValue()) & 0xffffffffL);
            invalidate();
        });
        animator.start();
    }

    @Override protected void onDetachedFromWindow() {
        if (animator != null) { animator.cancel(); animator = null; }
        super.onDetachedFromWindow();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        float t = (System.nanoTime() % 18000000000L) / 18000000000f;
        paint.setShader(new RadialGradient(w * (0.22f + 0.06f * (float)Math.sin(t * 6.283f)),
                h * (0.35f + 0.08f * (float)Math.cos(t * 6.283f)), Math.max(w, h) * 0.58f,
                new int[]{0x16e51d43, 0x08a30d2d, 0x00000000}, null, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
        for (int i = 0; i < x.length; i++) {
            float drift = (float)Math.sin((t * 6.283f) + i * 1.7f);
            float px = x[i] * w + drift * 9f;
            float py = (y[i] * h - t * (8f + i) + h) % h;
            int alpha = 22 + (int)(18 * (0.5f + 0.5f * Math.sin(t * 6.283f + i)));
            paint.setColor((alpha << 24) | 0x00ff3458);
            canvas.drawCircle(px, py, radius[i] * getResources().getDisplayMetrics().density, paint);
        }
    }
}
