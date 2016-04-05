package ua.mintmalory.translnote.translnote;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class TranslatedNoteFragment extends Fragment {
    public static final String EXTRA_TRANSLATED_NOTE_ID = "translnote.TRANSLATED_NOTE_ID";
    private static HashMap<String, String> languages = new HashMap<>();
    private TextView mTranslatedTitle;
    private TextView mTranslatedText;
    private Note mNote;
    private EditText mTitleField;
    private EditText mTextField;
    private Spinner spinnerFromLng;
    private Spinner spinnerToLng;
    private Button translateBtn;
    private FetchTranslationTask translationTask;

    static {
        languages.put("English", "en");
        languages.put("Українська", "uk");
        languages.put("Русский", "ru");
    }

    public static TranslatedNoteFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TRANSLATED_NOTE_ID, crimeId);
        TranslatedNoteFragment fragment = new TranslatedNoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID noteId = (UUID) getArguments().getSerializable(EXTRA_TRANSLATED_NOTE_ID);
        mNote = NotesLab.get(getActivity()).getNote(noteId);
    }

    public void concatToNoteText(String newText) {
        mTextField.setText(mTextField.getText() + " " + newText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.note_translate_fragment, parent, false);

        mTitleField = (EditText) v.findViewById(R.id.note_untranslated_title);
        mTextField = (EditText) v.findViewById(R.id.note_untranslated_text);
        mTitleField.setText(mNote.getHeader());
        mTextField.setText(mNote.getText());
        mTranslatedTitle = (TextView) v.findViewById(R.id.note_translated_title);
        mTranslatedText = (TextView) v.findViewById(R.id.note_translated_text);

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

        String[] data = new String[languages.size()];

        int i = 0;
        for (String s : languages.keySet()) {
            data[i++] = s;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFromLng = (Spinner) v.findViewById(R.id.spinner_from_lng);
        spinnerToLng = (Spinner) v.findViewById(R.id.spinner_to_lng);
        spinnerFromLng.setAdapter(adapter);
        spinnerToLng.setAdapter(adapter);
        translateBtn = (Button) v.findViewById(R.id.translate_btn);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translationTask = new FetchTranslationTask();
                translationTask.execute(mTitleField.getText().toString(),
                        mTextField.getText().toString(),
                        languages.get(spinnerFromLng.getSelectedItem().toString()),
                        languages.get(spinnerToLng.getSelectedItem().toString()));
                String[] result = null;
                try {
                    result = translationTask.get();
                } catch (InterruptedException | ExecutionException e) {

                }
                if (result != null) {
                    mTranslatedTitle.setText(result[0]);
                    mTranslatedText.setText(result[1]);
                }
            }
        });
        return v;
    }


    public class FetchTranslationTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchTranslationTask.class.getSimpleName();

        private String[] getTranslatedTextFromJson(String translationJsonStr)
                throws JSONException {
            JSONObject translatedJson = new JSONObject(translationJsonStr);
            JSONArray textArray = translatedJson.getJSONArray("text");


            String[] resultStrs = new String[2];

            resultStrs[0] = textArray.getString(0);
            resultStrs[1] = textArray.getString(1);

            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String translationJsonStr = null;

            try {
                final String TRANSLATE_BASE_URL =
                        "https://translate.yandex.net/api/v1.5/tr.json/translate?";
                final String KEY = "key";
                final String APIkey = "trnsl.1.1.20160331T065039Z.998c0e81c56fb0fe.8b1ab0611b1b6c7f28d15d8bd563a86977b7889a";
                final String TITLE = "text";
                final String TEXT = "text";
                final String LANGUAGE = "lang";
                final String FORMAT = "format";
                final String plainFormat = "plain";

                Uri builtUri = Uri.parse(TRANSLATE_BASE_URL).buildUpon()
                        .appendQueryParameter(KEY, APIkey)
                        .appendQueryParameter(TITLE, params[0])
                        .appendQueryParameter(TEXT, params[1])
                        .appendQueryParameter(LANGUAGE, params[2] + "-" + params[3])
                        .appendQueryParameter(FORMAT, plainFormat)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                translationJsonStr = buffer.toString();

            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getTranslatedTextFromJson(translationJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
    }
}
