package ua.mintmalory.translnote.translnote.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import java.util.Date;
import java.util.List;

import ua.mintmalory.translnote.translnote.adapters.NotesListAdapter;
import ua.mintmalory.translnote.translnote.model.Note;
import ua.mintmalory.translnote.translnote.NoteActivity;
import ua.mintmalory.translnote.translnote.model.NotesLab;
import ua.mintmalory.translnote.translnote.R;

public class NotesListFragment extends Fragment {
    private List<Note> mNotes;
    public static final int RESULT_SPEECH = 1;
    private ListView mNotesListView;
    private FloatingActionButton mAddNewNoteFab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mNotes = NotesLab.get(getActivity()).getCrimes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewHierarchy = inflater.inflate(R.layout.notes_list_fragment, container, false);
        mNotesListView = (ListView) viewHierarchy.findViewById(R.id.notes_listView);
        NotesListAdapter adapter = new NotesListAdapter(getContext(), mNotes);
        mNotesListView.setAdapter(adapter);
        mNotesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note note = ((NotesListAdapter) mNotesListView.getAdapter()).getItem(i);
                Intent intent = new Intent(getActivity(), NoteActivity.class);
                intent.putExtra(NoteFragment.EXTRA_NOTE_ID, note.getId());
                startActivityForResult(intent, 0);
            }
        });
        mAddNewNoteFab = (FloatingActionButton) viewHierarchy.findViewById(R.id.fab);
        mAddNewNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewNote();
            }
        });
        return viewHierarchy;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == getActivity().RESULT_OK && null != data) {

                    List<String> speechToText = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    Note note = new Note(null, speechToText.get(0), new Date(System.currentTimeMillis()));
                    NotesLab.get(getActivity()).addNote(note);
                    Intent i = new Intent(getActivity(), NoteActivity.class);
                    i.putExtra(NoteFragment.EXTRA_NOTE_ID, note.getId());
                    startActivityForResult(i, 0);
                }
                break;
            }
            case 0:
                ((NotesListAdapter) mNotesListView.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_note:
                createNewNote();
                return true;
            case R.id.menu_item_speech_to_text:
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                //  InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something...");
                //String locale = ims.getLocale();
                //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, locale);

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewNote() {
        Note note = new Note(null, null, new Date(System.currentTimeMillis()));
        NotesLab.get(getActivity()).addNote(note);
        Intent i = new Intent(getActivity(), NoteActivity.class);
        i.putExtra(NoteFragment.EXTRA_NOTE_ID, note.getId());
        startActivityForResult(i, 0);
    }
}

