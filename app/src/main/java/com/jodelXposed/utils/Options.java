package com.jodelXposed.utils;

import android.os.FileObserver;

import com.jodelXposed.models.Location;
import com.jodelXposed.models.UDI;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.jodelXposed.utils.Log.xlog;
import static com.jodelXposed.utils.Utils.SettingsPath;

public class Options extends FileObserver {
    private static Options singleton;

    public static Options getInstance() {
        if (singleton == null)
            singleton = new Options();
        return singleton;
    }

    public void resetLocation() {
        options.location = new Location();
        options.location.setActive(true);
        save();
    }

    public static class OptionsObject {
        public UDI udi = new UDI();
        public Location location = new Location();
    }

    public Location getLocationObject(){
        return this.options.location;
    }

    public UDI getUDIObject(){
        return this.options.udi;
    }

    private File settingsFile;
    private JsonAdapter<OptionsObject> jsonAdapter;
    public OptionsObject options;

    public Options() {
        super(SettingsPath,MODIFY);
        Moshi moshi = new Moshi.Builder().build();
        jsonAdapter = moshi.adapter(OptionsObject.class);
        xlog("Init file object with path: " + SettingsPath);
        settingsFile = new File(SettingsPath);

        if (!settingsFile.exists()) {
            options = new OptionsObject();
            save();
        } else {
            load();
        }
        startWatching();
    }

    private void writeFile()  {
        String settingsJson = jsonAdapter.toJson(options);
        try {
            xlog(String.format("Writing %s to file", settingsJson));
            FileOutputStream output = FileUtils.openOutputStream(settingsFile);
            output.write(settingsJson.getBytes());
            output.close();
        } catch (IOException e) {
            xlog("Could not write to file");
            xlog(e.getMessage());
        }
    }

    public void save(){
        writeFile();
    }

    public void load() {
        xlog("Loading settings file");
        try {
            String json = FileUtils.readFileToString(settingsFile, Charsets.UTF_8);
            options = jsonAdapter.fromJson(json);
            xlog(json);

        } catch (IOException e) {
            xlog("Could not load options file");
        }
    }

    @Override
    public void onEvent(int event, String path) { load(); }

}
