package ua.mintmalory.translnote.translnote;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotesLab {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private ArrayList<Note> notes;

    private static NotesLab sNotesLab;

    private NotesLab(Context appContext) {
        notes = new ArrayList<>();

        prefs = appContext.getSharedPreferences("NOTES_LIST", appContext.MODE_PRIVATE);
        editor = prefs.edit();

        Gson gson = new Gson();
        String json = prefs.getString("NOTES_LIST", "");
        if (json.isEmpty()) {
            notes = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<Note>>() {
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

    public void addNote(Note note) {
        notes.add(note);
    }

    public Note getNote(UUID id) {
        for (Note c : notes) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }

    public ArrayList<Note> getCrimes() {
        return notes;
    }

    public void saveNotes() {
        Gson gson = new Gson();
        String jsonNotes = gson.toJson(notes);

        editor.putString("NOTES_LIST", jsonNotes);
        editor.commit();
    }

    public void removeNote(UUID id) {
        for (Note c : notes) {
            if (c.getId().equals(id)) {
                notes.remove(c);
                return;
            }
        }
    }
}
