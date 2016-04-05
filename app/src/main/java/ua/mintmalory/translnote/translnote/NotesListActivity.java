package ua.mintmalory.translnote.translnote;

import android.support.v4.app.Fragment;

public class NotesListActivity extends SingleFragmentActivity {

        @Override
        protected Fragment createFragment() {
            return new NotesListFragment();
        }
}
