package ua.mintmalory.translnote.translnote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetectedLanguage {
    @SerializedName("lang")
    String detectedLang;

    public String getDetectedLang() {
        return detectedLang;
    }

    public String setDetectedLang(String newDetectedLang) {
        detectedLang = newDetectedLang;
    }
}
