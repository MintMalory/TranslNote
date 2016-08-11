package ua.mintmalory.translnote.translnote.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import ua.mintmalory.translnote.translnote.model.Note;
import ua.mintmalory.translnote.translnote.NoteActivity;
import ua.mintmalory.translnote.translnote.model.NotesLab;
import ua.mintmalory.translnote.translnote.R;

public class NotesListFragment extends Fragment {
    private List<Note> mNotes;
    public static final int RESULT_SPEECH = 1;
	private ListView mNotesListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mNotes = NotesLab.get(getActivity()).getCrimes();
    }
	
	    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
                             Bundle savedInstanceState) {
         
        View viewHierarchy = inflater.inflate(R.layout.notes_list_fragment, false);
        mNotesListView = (ListView) container.findViewById(R.id.notes_listView);
		NotesListAdapter adapter = new NotesListAdapter(mNotes);
        mNotesListView.setListAdapter(adapter);
		
        return viewHierarchy;
    }
	

    public void onListItemClick(ListView l, View v, int position, long id) {
        Note note = ((NotesListAdapter) mNotesListView.getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), NoteActivity.class);
        i.putExtra(NoteFragment.EXTRA_NOTE_ID, note.getId());
        startActivityForResult(i, 0);
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
                ((NotesListAdapter) mNotesListView.getListAdapter()).notifyDataSetChanged();
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
                Note note = new Note(null, null, new Date(System.currentTimeMillis()));
                NotesLab.get(getActivity()).addNote(note);
                Intent i = new Intent(getActivity(), NoteActivity.class);
                i.putExtra(NoteFragment.EXTRA_NOTE_ID, note.getId());
                startActivityForResult(i, 0);
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

    private class NotesListAdapter extends ArrayAdapter<Note> {
        public NotesListAdapter(List<Note> notes) {
            super(getActivity(), R.layout.list_item, notes);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
ViewHolder holder;
            
			
			if (null == convertView) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.noteHeaderView= (TextView) convertView.findViewById(R.id.item_headerText);
                holder.noteTextView = (TextView) convertView.findViewById(R.id.item_date);
				convertView.setTag(holder);
            }else{
				holder = (ViewHolder) convertView.getTag();
			}

            Note note = getItem(position);



            holder.noteHeaderView.setText(note.getHeader());

			//dateFormat перенести в строковые ресурсы и менять в зависимости от локализации
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
            holder.noteSubHeaderView.setText(dateFormat.format(note.getCreationDate()));
            return convertView;
        }
		
	static class ViewHolder {
		public TextView noteHeaderView;
		public TextView noteSubHeaderView;
	}
	
    }
}

