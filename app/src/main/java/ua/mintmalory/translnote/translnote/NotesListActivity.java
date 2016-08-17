package ua.mintmalory.translnote.translnote;

import android.support.v4.app.Fragment;

import ua.mintmalory.translnote.translnote.fragments.DeletingNotesListFragment;
import ua.mintmalory.translnote.translnote.fragments.NotesListFragment;

public class NotesListActivity extends SingleFragmentActivity {

        @Override
        protected Fragment createFragment() {
            return new
                    DeletingNotesListFragment();
                    //NotesListFragment();
        }
}
