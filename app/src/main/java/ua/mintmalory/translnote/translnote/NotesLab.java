package ua.mintmalory.translnote.translnote;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by User on 04.04.2016.
 */
public class NotesLab {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private ArrayList<Item> notes;

    private static NotesLab sNotesLab;
    private Context mAppContext;

    private NotesLab(Context appContext) {
        mAppContext = appContext;
        notes = new ArrayList<Item>();

        prefs = appContext.getSharedPreferences("NOTES_LIST", appContext.MODE_PRIVATE);
        editor = prefs.edit();

        Gson gson = new Gson();
        String json = prefs.getString("NOTES_LIST", "");
        if (json.isEmpty()) {
            notes = new ArrayList<Item>();
        } else {
            Type type = new TypeToken<List<Item>>() {
            }.getType();
            notes = gson.fromJson(json, type);
        }
    }

    public static NotesLab get(Context c) {
        if (sNotesLab == null) {
            sNotesLab = new NotesLab(c.getApplicationContext());
        }
        return sNotesLab;
    }

    public void addNote(Item note) {
        notes.add(note);
    }

    public Item getNote(UUID id) {
        for (Item c : notes) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }

    public ArrayList<Item> getCrimes() {
        return notes;
    }

    public void saveNotes() {
        Gson gson = new Gson();
        String jsonNotes = gson.toJson(notes);

        editor.putString("NOTES_LIST", jsonNotes);
        editor.commit();
    }

    public void removeNote(UUID id) {
        for (Item c : notes) {
            if (c.getId().equals(id)) {
                notes.remove(c);
                return;
            }
        }
    }
}
