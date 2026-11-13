package net.kdt.pojavlaunch.profiles;

import static net.kdt.pojavlaunch.extra.ExtraCore.getValue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AlertDialog;

import net.kdt.pojavlaunch.JMinecraftVersionList;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.tasks.AsyncVersionList;

/** Version picker that waits for the remote manifest instead of showing an empty list on first launch. */
public class VersionSelectorDialog {
    public static void open(Context context, boolean hideCustomVersions, VersionSelectorListener listener) {
        JMinecraftVersionList versions = (JMinecraftVersionList) getValue(ExtraConstants.RELEASE_TABLE);
        if (versions != null && versions.versions != null && versions.versions.length > 0) {
            showVersions(context, hideCustomVersions, listener, versions);
            return;
        }

        AlertDialog loading = new AlertDialog.Builder(context)
                .setTitle("Minecraft versions")
                .setMessage("Loading available versions…")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
        loading.show();

        new AsyncVersionList().getVersionList(result -> new Handler(Looper.getMainLooper()).post(() -> {
            if (!loading.isShowing()) return;
            loading.dismiss();
            if (result == null || result.versions == null || result.versions.length == 0) {
                new AlertDialog.Builder(context)
                        .setTitle("Couldn't load versions")
                        .setMessage("Check your internet connection and try again.")
                        .setNegativeButton("Close", null)
                        .setPositiveButton("Retry", (dialog, which) ->
                                open(context, hideCustomVersions, listener))
                        .show();
                return;
            }
            Tools.runOnUiThread(() -> showVersions(context, hideCustomVersions, listener, result));
        }), false);
    }

    private static void showVersions(Context context, boolean hideCustomVersions,
                                     VersionSelectorListener listener,
                                     JMinecraftVersionList versionList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ExpandableListView expandableListView = (ExpandableListView) LayoutInflater.from(context)
                .inflate(R.layout.dialog_expendable_list_view, null);
        VersionListAdapter adapter = new VersionListAdapter(
                versionList.versions, hideCustomVersions, context);
        expandableListView.setAdapter(adapter);
        builder.setTitle("Select Minecraft version");
        builder.setView(expandableListView);
        AlertDialog dialog = builder.create();
        expandableListView.setOnChildClickListener((parent, v1, groupPosition, childPosition, id) -> {
            String version = adapter.getChild(groupPosition, childPosition);
            listener.onVersionSelected(version, adapter.isSnapshotSelected(groupPosition));
            dialog.dismiss();
            return true;
        });
        dialog.show();
    }
}
