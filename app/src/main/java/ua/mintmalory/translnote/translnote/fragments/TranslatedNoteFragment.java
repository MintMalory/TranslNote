package ua.mintmalory.translnote.translnote.fragments;

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
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.mintmalory.translnote.translnote.model.Note;
import ua.mintmalory.translnote.translnote.model.NotesLab;
import ua.mintmalory.translnote.translnote.R;
import ua.mintmalory.translnote.translnote.api.YandexTranslateService;
import ua.mintmalory.translnote.translnote.model.Translation;

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
				
				String fromLng = languages.get(spinnerFromLng.getSelectedItem().toString());
				String toLng = languages.get(spinnerToLng.getSelectedItem().toString());
				
				/*if(fromLng.isEmpty()){
					 Call<Translation> call = service.detectLanguageOfText(YandexTranslateService.API_KEY,
                        mTextField.getText().toString());
						
				call.enqueue(new Callback<DetectedLanguage>() {
                    @Override
                    public void onResponse(Call<DetectedLanguage> call, Response<DetectedLanguage> response) {
                        if (response.code() == 200) {
                            mTranslatedTitle.setText(response.body().getTitle());
                            mTranslatedText.setText(response.body().getText());
                        } else
                            Toast.makeText(getContext(), "Oops! Code " + response.code(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Translation> call, Throwable t) {
                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_LONG).show();
                    }
                });
            		
						
				}
				TODO:Добавить AsyncTask и request внутри него!
				*/
				
                Call<Translation> call = service.getTranslation(YandexTranslateService.API_KEY,
                        mTitleField.getText().toString(),
                        mTextField.getText().toString(),
                        fromLng+ "-" + toLng);

                call.enqueue(new Callback<Translation>() {
                    @Override
                    public void onResponse(Call<Translation> call, Response<Translation> response) {
                        if (response.code() == 200) {
                            mTranslatedTitle.setText(response.body().getTitle());
                            mTranslatedText.setText(response.body().getText());
                        } else
                            Toast.makeText(getContext(), "Oops! Code " + response.code(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Translation> call, Throwable t) {
                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        return v;
    }
}
