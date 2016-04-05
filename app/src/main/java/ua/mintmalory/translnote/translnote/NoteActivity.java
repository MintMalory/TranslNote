package ua.mintmalory.translnote.translnote;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.UUID;

/**
 * Created by User on 04.04.2016.
 */
public class NoteActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        UUID noteId = (UUID)getIntent()
                .getSerializableExtra(NoteFragment.EXTRA_NOTE_ID);
        return NoteFragment.newInstance(noteId);
    }
}
