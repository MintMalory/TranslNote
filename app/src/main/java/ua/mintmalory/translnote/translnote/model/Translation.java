package ua.mintmalory.translnote.translnote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Translation {
    @SerializedName("text")
    List<String> translation;

    public List<String> getTranslation() {
        return translation;
    }

    public String getTitle() {
        return translation.get(0);
    }

    public String getText() {
        return translation.get(1);
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }


}
