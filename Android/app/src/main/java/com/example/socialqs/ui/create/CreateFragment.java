package com.example.socialqs.ui.create;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.socialqs.R;
import com.example.socialqs.models.Question;

public class CreateFragment extends Fragment {
    Question question = new Question();

    EditText questionDescription;
    EditText questionCategory;

    ImageView proceed;
    ImageView close;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState) {

        questionDescription = view.findViewById(R.id.titleDescription);
        questionCategory = view.findViewById(R.id.categoryDescription);

        proceed = view.findViewById(R.id.proceed);

        questionDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question.setqTitle(questionDescription.getText().toString());
            }
        });

        questionCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question.setqCategory(questionCategory.getText().toString());
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!question.getqTitle().isEmpty() && !question.getqCategory().isEmpty()) {
                    Navigation.findNavController(v).navigate(R.id.action_navigation_create_to_videoFragment);
                }
            }
        });
    }
}