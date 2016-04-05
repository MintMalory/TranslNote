package ua.mintmalory.translnote.translnote;

import android.support.v4.app.Fragment;

/**
 * Created by User on 04.04.2016.
 */
public class NotesListActivity extends SingleFragmentActivity {

        @Override
        protected Fragment createFragment() {
            return new NotesListFragment();
        }

}
