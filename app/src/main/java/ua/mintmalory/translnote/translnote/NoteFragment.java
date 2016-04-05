package ua.mintmalory.translnote.translnote;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.EditText;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public class NoteFragment extends Fragment {
    public static final String EXTRA_NOTE_ID = "translnote.NOTE_ID";

    Item mItem;
    EditText mTitleField;
    EditText mTextField;


    public static NoteFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_NOTE_ID, crimeId);

        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID noteId = (UUID) getArguments().getSerializable(EXTRA_NOTE_ID);
        mItem = NotesLab.get(getActivity()).getNote(noteId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NotesListFragment.RESULT_SPEECH: {
                if (resultCode == getActivity().RESULT_OK && null != data) {

                    List<String> speechToText = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    mTextField.setText(mTextField.getText() +" " + speechToText.get(0));
                }
                break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.note_fragment, parent, false);

        mTitleField = (EditText) v.findViewById(R.id.note_title);
        mTitleField.setText(mItem.getHeader());

        mTextField = (EditText) v.findViewById(R.id.note_text);
        mTextField.setText(mItem.getText());


        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {

            }

            public void afterTextChanged(Editable c) {
                mItem.setHeader(c.toString());
                NotesLab.get(getContext()).saveNotes();

            }
        });

        mTextField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {

            }

            public void afterTextChanged(Editable c) {
                mItem.setText(c.toString());
                NotesLab.get(getContext()).saveNotes();
            }
        });


        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.note_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           case R.id.menu_item_speech_to_text:
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
               InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);

               InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();

               String locale = ims.getLocale();
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, locale);

                try {
                    startActivityForResult(intent, NotesListFragment.RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
                return true;
            case R.id.menu_item_remove_note:
                NotesLab.get(getContext()).removeNote(mItem.getId());
                getActivity().finish();
                return true;
            case R.id.menu_item_translate_note:

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
