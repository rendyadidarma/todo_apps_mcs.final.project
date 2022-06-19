package com.example.mcsproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class profileFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextView usernameTitle, emailText;
    Button editButton, saveButton, signOutButton;
    EditText editTextUsername;
    LinearLayout editLinear, editTextLinear, saveLinear;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        emailText = view.findViewById(R.id.email_text_value);


        if(firebaseUser == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            emailText.setText(firebaseUser.getEmail());
        }

        usernameTitle = view.findViewById(R.id.username_title);

        editButton = view.findViewById(R.id.buttonEdit);
        saveButton = view.findViewById(R.id.buttonUpdate);
        signOutButton = view.findViewById(R.id.sign_out);

        editTextUsername = view.findViewById(R.id.editUpdate);

        editLinear = view.findViewById(R.id.LinearButtonEdit);
        editTextLinear = view.findViewById(R.id.LinearEditUpdate);
        saveLinear = view.findViewById(R.id.LinearButtonUpdate);




        editButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);




        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonEdit) {
            saveLinear.setVisibility(View.VISIBLE);
            editLinear.setVisibility(View.GONE);

            editTextLinear.setVisibility(View.VISIBLE);

        } else if(view.getId() == R.id.buttonUpdate) {

            String usernameInput = editTextUsername.getText().toString();

            if(usernameInput.equals("")) {
                Toast.makeText(getActivity(), "Failed to Update Username..", Toast.LENGTH_SHORT).show();
            } else {
                firebaseFirestore.collection("users").document(firebaseUser.getUid())
                        .update("name", usernameInput)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Success to Update...", Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            saveLinear.setVisibility(View.GONE);
            editLinear.setVisibility(View.VISIBLE);

            editTextLinear.setVisibility(View.GONE);
        } else if(view.getId() == R.id.sign_out) {
            AuthUI.getInstance()
                    .signOut(getActivity())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), "Every new beginning comes from some other beginning's end. Farewell.", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getActivity(), LoginActivity.class));
                        }
                    });

            FirebaseAuth.getInstance().signOut();
        }
    }
}