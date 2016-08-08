package ua.mintmalory.translnote.translnote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.mintmalory.translnote.translnote.api.YandexTranslateService;

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
    private Retrofit mRetrofit;

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

        mRetrofit = new Retrofit.Builder()
                .baseUrl(YandexTranslateService.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        translateBtn = (Button) v.findViewById(R.id.translate_btn);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YandexTranslateService service = mRetrofit.create(YandexTranslateService.class);
                Call<List<String>> call = service.getTranslation(YandexTranslateService.API_KEY,
                        mTitleField.getText().toString(),
                        mTextField.getText().toString(),
                        spinnerFromLng.getSelectedItem().toString() + "-" + spinnerToLng.getSelectedItem().toString());

                call.enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        if (response.code() == 200) {
                            mTranslatedTitle.setText(response.body().get(0));
                            mTranslatedText.setText(response.body().get(1));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {

                    }
                });
            }
        });
        return v;
    }
}
