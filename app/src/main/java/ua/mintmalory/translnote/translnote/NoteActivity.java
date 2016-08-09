package ua.mintmalory.translnote.translnote;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import ua.mintmalory.translnote.translnote.fragments.NoteFragment;
import ua.mintmalory.translnote.translnote.fragments.NotesListFragment;
import ua.mintmalory.translnote.translnote.fragments.TranslatedNoteFragment;
import ua.mintmalory.translnote.translnote.model.NotesLab;

public class NoteActivity extends SingleFragmentActivity {
    private UUID noteId;
    private NoteFragment noteFragment;
    private TranslatedNoteFragment translatedNoteFragment;
    private MenuItem translated;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        translated = menu.findItem(R.id.menu_item_translate_note);
        return true;
    }

    @Override
    protected Fragment createFragment() {
        noteId = (UUID) getIntent()
                .getSerializableExtra(NoteFragment.EXTRA_NOTE_ID);
        noteFragment = NoteFragment.newInstance(noteId);
        translatedNoteFragment = TranslatedNoteFragment.newInstance(noteId);
        return noteFragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_speech_to_text:
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();

                String locale = ims.getLocale();
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, locale);

                try {
                    startActivityForResult(intent, NotesListFragment.RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(this,
                            "Oops! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
                return true;
            case R.id.menu_item_remove_note:
                NotesLab.get(this).removeNote(noteId);
                finish();
                return true;
            case R.id.menu_item_translate_note:
                if (item.isChecked()) {
                    item.setChecked(false);
                    noteFragment = NoteFragment.newInstance(noteId);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, noteFragment).commit();

                } else {
                    item.setChecked(true);
                    translatedNoteFragment = TranslatedNoteFragment.newInstance(noteId);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, translatedNoteFragment).commit();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NotesListFragment.RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    List<String> speechToText = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if (translated.isChecked()) {
                        translatedNoteFragment.concatToNoteText(speechToText.get(0));
                    } else {
                        noteFragment.concatToNoteText(speechToText.get(0));
                    }
                }
                break;
            }
        }
    }

}
