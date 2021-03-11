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
import com.example.socialqs.models.CategoryModel;
import com.example.socialqs.models.QuestionModel;
import com.example.socialqs.utils.Utilities;

import java.util.ArrayList;

public class VideoDescription extends Fragment {

    QuestionModel question = new QuestionModel();

    private ArrayList<CategoryModel> categories;
    ImageView close, proceed;
    EditText title;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_video_description, container, false);

        this.categories = ((CreateActivity) getActivity()).categories;

        for (CategoryModel model : categories){
            if (model.name.equalsIgnoreCase("all")){
                categories.remove(model);
                break;
            }
        }

        ((CreateActivity) getActivity()).getSupportActionBar().hide();

        close = view.findViewById(R.id.close);
        proceed = view.findViewById(R.id.proceed);

        title = view.findViewById(R.id.title);
        spinner = view.findViewById(R.id.spinner);

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, categories);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //TODO: Set scrollbar onto the spinner

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question.setqTitle(title.getText().toString());
                question.setqCategory((CategoryModel)spinner.getSelectedItem());

                if (!question.getqTitle().isEmpty() && question.getqCategory() != null) {
                    Navigation.findNavController(v).navigate(R.id.action_titleFragment_to_selectSourceFragment);
                }else{
                    Utilities.getInstance().createSingleActionAlert("Please select the options", "Okay", getActivity(), null).show();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

}