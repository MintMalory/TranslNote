package ua.mintmalory.translnote.translnote.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.UUID;

import ua.mintmalory.translnote.translnote.model.Note;
import ua.mintmalory.translnote.translnote.model.NotesLab;
import ua.mintmalory.translnote.translnote.R;

public class NoteFragment extends Fragment {
    public static final String EXTRA_NOTE_ID = "translnote.NOTE_ID";

    private Note mNote;
    private EditText mTitleField;
    private EditText mTextField;

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

        UUID noteId = (UUID) getArguments().getSerializable(EXTRA_NOTE_ID);
        mNote = NotesLab.get(getActivity()).getNote(noteId);
    }

    public void concatToNoteText(String newText) {
        mTextField.setText(mTextField.getText() + " " + newText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.note_fragment, parent, false);

        mTitleField = (EditText) v.findViewById(R.id.note_title);
        mTextField = (EditText) v.findViewById(R.id.note_text);
        mTitleField.setText(mNote.getHeader());
        mTextField.setText(mNote.getText());

        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
                mNote.setHeader(c.toString());
                NotesLab.get(getContext()).saveNotes();
            }
        });

        mTextField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
                mNote.setText(c.toString());
                NotesLab.get(getContext()).saveNotes();
            }
        });

        return v;
    }
}
