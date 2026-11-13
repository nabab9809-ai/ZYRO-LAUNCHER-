package net.kdt.pojavlaunch;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceControlFragment;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceExperimentalFragment;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceJavaFragment;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceMiscellaneousFragment;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceRendererSettingsFragment;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceVideoFragment;

/** Original ZYRO settings landing page. Existing setting implementations remain the source of truth. */
public class ZyroSettingsFragment extends Fragment {
    private static final int BG = Color.rgb(15, 8, 10);
    private static final int CARD = Color.rgb(31, 15, 19);
    private static final int ACCENT = Color.rgb(220, 25, 55);
    private static final int TEXT = Color.rgb(248, 244, 245);
    private static final int MUTED = Color.rgb(170, 153, 158);

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable android.view.ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ScrollView scroll = new ScrollView(requireContext());
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);

        LinearLayout page = new LinearLayout(requireContext());
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(24), dp(20), dp(24), dp(24));
        scroll.addView(page);

        TextView eyebrow = label("ZYRO CONTROL CENTER", 11, ACCENT, true);
        eyebrow.setLetterSpacing(0.14f);
        page.addView(eyebrow);

        TextView title = label("Settings", 29, TEXT, true);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
        titleParams.topMargin = dp(5);
        page.addView(title, titleParams);

        TextView intro = label("Tune your launcher and Minecraft experience.", 13, MUTED, false);
        LinearLayout.LayoutParams introParams = new LinearLayout.LayoutParams(-1, -2);
        introParams.topMargin = dp(4);
        introParams.bottomMargin = dp(18);
        page.addView(intro, introParams);

        addSection(page, "GAME & PERFORMANCE");
        addCard(page, "Graphics & Display", "Resolution, display behavior and performance options",
                "01", LauncherPreferenceVideoFragment.class);
        addCard(page, "Renderer Engine", "Renderer-specific options and graphics compatibility",
                "02", LauncherPreferenceRendererSettingsFragment.class);
        addCard(page, "Java & Memory", "Java runtime, allocated RAM and JVM arguments",
                "03", LauncherPreferenceJavaFragment.class);

        addSection(page, "INPUT & LAUNCHER");
        addCard(page, "Controls & Input", "Touch controls and input behavior",
                "04", LauncherPreferenceControlFragment.class);
        addCard(page, "Downloads & Storage", "Download source, file checks and launcher utilities",
                "05", LauncherPreferenceMiscellaneousFragment.class);
        addCard(page, "Advanced Options", "Experimental and advanced launcher features",
                "06", LauncherPreferenceExperimentalFragment.class);

        TextView note = label("ZYRO SETTINGS  •  BUILT AROUND THE EXISTING LAUNCH ENGINE",
                9, MUTED, true);
        note.setLetterSpacing(0.06f);
        LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(-1, -2);
        noteParams.topMargin = dp(18);
        page.addView(note, noteParams);
        return scroll;
    }

    private void addSection(LinearLayout parent, String name) {
        TextView section = label(name, 10, ACCENT, true);
        section.setLetterSpacing(0.12f);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dp(8);
        params.bottomMargin = dp(8);
        parent.addView(section, params);
    }

    private void addCard(LinearLayout parent, String title, String summary, String number,
                         Class<? extends Fragment> destination) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(15), dp(12), dp(15), dp(12));
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(CARD);
        shape.setCornerRadius(dp(10));
        shape.setStroke(dp(1), Color.rgb(67, 30, 39));
        card.setBackground(shape);
        card.setClickable(true);
        card.setFocusable(true);

        TextView index = label(number, 12, ACCENT, true);
        index.setGravity(Gravity.CENTER);
        card.addView(index, new LinearLayout.LayoutParams(dp(34), dp(34)));

        LinearLayout copy = new LinearLayout(requireContext());
        copy.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams copyParams = new LinearLayout.LayoutParams(0, -2, 1f);
        copyParams.leftMargin = dp(13);
        card.addView(copy, copyParams);
        copy.addView(label(title, 15, TEXT, true));
        TextView detail = label(summary, 11, MUTED, false);
        LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(-1, -2);
        detailParams.topMargin = dp(3);
        copy.addView(detail, detailParams);

        TextView arrow = label("›", 25, ACCENT, false);
        card.addView(arrow, new LinearLayout.LayoutParams(dp(22), -2));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
        cardParams.bottomMargin = dp(9);
        parent.addView(card, cardParams);
        card.setOnClickListener(v -> Tools.swapFragment(
                requireActivity(), destination, destination.getSimpleName(), null));
    }

    private TextView label(String value, int sizeSp, int color, boolean bold) {
        TextView text = new TextView(requireContext());
        text.setText(value);
        text.setTextSize(sizeSp);
        text.setTextColor(color);
        text.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        return text;
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
