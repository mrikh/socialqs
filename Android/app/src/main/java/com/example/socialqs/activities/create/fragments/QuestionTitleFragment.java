package com.example.socialqs.activities.create.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.socialqs.R;
import com.example.socialqs.activities.create.CreateActivity;
import com.example.socialqs.models.Question;

public class QuestionTitleFragment extends Fragment {
    Question question = new Question();

    ImageView closeBtn, proceedBtn;
    EditText title, category;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_question_title, container, false);

        ((CreateActivity) getActivity()).getSupportActionBar().hide();

        closeBtn = view.findViewById(R.id.close);
        proceedBtn = view.findViewById(R.id.proceed);
        title = view.findViewById(R.id.title);
        spinner = view.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.category_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        proceedBtn.setOnClickListener(v -> {
            question.setqTitle(title.getText().toString());
            question.setqCategory(spinner.getSelectedItem().toString());

            if (!question.getqTitle().isEmpty() && question.getqCategory().compareTo("Category") != 0) {
                Navigation.findNavController(v).navigate(R.id.action_questionTitleFragment_to_selectVideoSourceFragment);
            }
        });

        closeBtn.setOnClickListener(v -> {
            spinner.getSelectedItem();
            getActivity().finish();
        });
    }

}