package com.example.socialqs.activities.create.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.socialqs.R;
import com.example.socialqs.models.Question;

public class TitleFragment extends Fragment {
    Question question = new Question();

    ImageView close, proceed;
    EditText title, category;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_title, container, false);

        close = view.findViewById(R.id.close);
        proceed = view.findViewById(R.id.proceed);

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

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question.setqTitle(title.getText().toString());
                question.setqCategory(spinner.getSelectedItem().toString());

                if (!question.getqTitle().isEmpty() && question.getqCategory().compareTo("Category") != 0) {
                    Navigation.findNavController(v).navigate(R.id.action_titleFragment_to_selectSourceFragment);
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.getSelectedItem();
                getActivity().finish();
            }
        });
    }

}