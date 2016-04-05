package ua.mintmalory.translnote.translnote;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class NotesListFragment extends ListFragment {
    private List<Item> mNotes;
    protected static final int RESULT_SPEECH = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mNotes = NotesLab.get(getActivity()).getCrimes();
        NotesListAdapter adapter = new NotesListAdapter(mNotes);
        setListAdapter(adapter);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {

        Item c = ((NotesListAdapter) getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), NoteActivity.class);
        i.putExtra(NoteFragment.EXTRA_NOTE_ID, c.getId());
        startActivityForResult(i, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == getActivity().RESULT_OK && null != data) {

                    List<String> speechToText = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    Item note = new Item(null, speechToText.get(0), new Date(System.currentTimeMillis()));
                    NotesLab.get(getActivity()).addNote(note);
                    Intent i = new Intent(getActivity(), NoteActivity.class);
                    i.putExtra(NoteFragment.EXTRA_NOTE_ID, note.getId());
                    startActivityForResult(i, 0);
                }
                break;
            }
            case 0:
                ((NotesListAdapter) getListAdapter()).notifyDataSetChanged();
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
                Item note = new Item(null, null, new Date(System.currentTimeMillis()));
               NotesLab.get(getActivity()).addNote(note);
                Intent i = new Intent(getActivity(), NoteActivity.class);
                i.putExtra(NoteFragment.EXTRA_NOTE_ID, note.getId());
                startActivityForResult(i, 0);
                return true;
            case R.id.menu_item_speech_to_text:
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);

                InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();

                String locale = ims.getLocale();

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, locale);

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
        private class NotesListAdapter extends ArrayAdapter<Item> {
            public NotesListAdapter(List<Item> notes) {
                super(getActivity(), R.layout.list_item, notes);
            }


            public View getView(int position, View convertView, ViewGroup parent) {

                if (null == convertView) {
                    convertView = getActivity().getLayoutInflater()
                            .inflate(R.layout.list_item, null);
                }


                Item c = getItem(position);

                TextView header = (TextView) convertView.findViewById(R.id.item_headerText);
                TextView subHeader = (TextView) convertView.findViewById(R.id.item_date);

                header.setText(c.getHeader());

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
                subHeader.setText(dateFormat.format(c.getCreationDate()));
                return convertView;
            }

        }
    }

