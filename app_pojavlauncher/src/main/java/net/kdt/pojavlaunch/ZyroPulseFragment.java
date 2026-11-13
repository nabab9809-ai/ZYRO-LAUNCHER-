package net.kdt.pojavlaunch;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.Locale;

/** ZYRO Pulse: device-level metrics only; deliberately does not invent in-game FPS. */
public class ZyroPulseFragment extends Fragment {
    private static final int BG=0xFF0F080A, CARD=0xFF1F0F13, ACCENT=0xFFDC1937, WHITE=0xFFF8F4F5, MUTED=0xFFAA999E;

    @Nullable @Override public View onCreateView(@NonNull android.view.LayoutInflater inflater,
            @Nullable android.view.ViewGroup container, @Nullable Bundle state) {
        Context context=requireContext();
        ScrollView scroll=new ScrollView(context); scroll.setBackgroundColor(BG);
        LinearLayout page=new LinearLayout(context); page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(22),dp(18),dp(22),dp(20)); scroll.addView(page);
        TextView eyebrow=label("ZYRO SYSTEM MONITOR",11,ACCENT,true); page.addView(eyebrow);
        TextView title=label("Pulse",29,WHITE,true); page.addView(title);
        TextView desc=label("A live snapshot of your device. Game FPS is not available until a game session is instrumented.",12,MUTED,false);
        LinearLayout.LayoutParams d=new LinearLayout.LayoutParams(-1,-2); d.topMargin=dp(5); d.bottomMargin=dp(18); page.addView(desc,d);

        ActivityManager.MemoryInfo memory=new ActivityManager.MemoryInfo();
        ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        if(am!=null) am.getMemoryInfo(memory);
        long totalRam=readTotalRam();
        addMetric(page,"AVAILABLE MEMORY",formatBytes(memory.availMem),
                totalRam>0 ? "Device RAM: "+formatBytes(totalRam) : "Android-reported available memory");
        File storageRoot=Environment.getDataDirectory();
        StatFs fs=new StatFs(storageRoot.getAbsolutePath());
        long free=fs.getAvailableBytes(), total=fs.getTotalBytes();
        addMetric(page,"INTERNAL STORAGE",formatBytes(free)+" free",
                formatBytes(Math.max(0,total-free))+" used of "+formatBytes(total));
        addMetric(page,"ANDROID VERSION","Android "+android.os.Build.VERSION.RELEASE,
                android.os.Build.MANUFACTURER+" "+android.os.Build.MODEL);
        addMetric(page,"PROCESS STATUS","Launcher active",
                "Device snapshot refreshed when this page opens.");
        TextView foot=label("ZYRO PULSE • DEVICE METRICS ONLY",10,MUTED,true);
        LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(-1,-2); fp.topMargin=dp(16); page.addView(foot,fp);
        return scroll;
    }
    private void addMetric(LinearLayout parent,String title,String value,String detail){
        LinearLayout card=new LinearLayout(requireContext()); card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(15),dp(12),dp(15),dp(12)); card.setBackgroundColor(CARD);
        TextView h=label(title,10,ACCENT,true); card.addView(h);
        TextView v=label(value,21,WHITE,true); LinearLayout.LayoutParams vp=new LinearLayout.LayoutParams(-1,-2); vp.topMargin=dp(4); card.addView(v,vp);
        TextView sub=label(detail,11,MUTED,false); LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2); sp.topMargin=dp(3); card.addView(sub,sp);
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2); cp.bottomMargin=dp(10); parent.addView(card,cp);
    }
    private TextView label(String s,int size,int color,boolean bold){TextView v=new TextView(requireContext());v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setTypeface(android.graphics.Typeface.create("sans-serif",bold?1:0));return v;}
    private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    private static String formatBytes(long bytes){if(bytes<1024)return bytes+" B";int exp=(int)(Math.log(bytes)/Math.log(1024));String[] units={"B","KB","MB","GB","TB"};exp=Math.min(exp,units.length-1);return String.format(Locale.US,"%.1f %s",bytes/Math.pow(1024,exp),units[exp]);}
    private long readTotalRam(){try{java.io.BufferedReader r=new java.io.BufferedReader(new java.io.FileReader("/proc/meminfo"));String line=r.readLine();r.close();if(line!=null){String[] parts=line.split("\\s+");return Long.parseLong(parts[1])*1024L;}}catch(Exception ignored){}return 0;}
}