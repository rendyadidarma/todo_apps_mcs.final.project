package com.example.mcsproject;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Delayed;

public class todoFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private RecyclerView recyclerView;
    private FloatingActionButton fabButton;
    private FirestoreRecyclerAdapter<Tasks, TasksViewHolder> adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid()).collection("tasks")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("TAG", "Listen failed.", error);
                        }

                        if (value != null) {
                            for(DocumentChange dc : value.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        Log.d("DC GET TYPE", "New city: " + dc.getDocument().getData());
                                        break;
                                    case MODIFIED:
                                        break;
                                    case REMOVED:
                                        break;
                                }
                            }
                        }
                    }
                });

        View v= inflater.inflate(R.layout.fragment_todo, container, false);

        recyclerView = v.findViewById(R.id.todoRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("users").document(firebaseUser.getUid()).collection("tasks")
                .orderBy("date");

        Log.wtf("query value", query.toString());
        FirestoreRecyclerOptions<Tasks> options = new FirestoreRecyclerOptions.Builder<Tasks>()
                .setQuery(query, Tasks.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Tasks, TasksViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TasksViewHolder holder, int position, @NonNull Tasks model) {
                String documentId = getSnapshots().getSnapshot(position).getId();

                holder.getDocumentId(documentId);
                holder.setProductName(model.getTitle());
                holder.setProductDate(model.getDate());
                holder.setProductType(model.getType());
            }

            @NonNull
            @Override
            public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_todo, parent, false);
                return new TasksViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

        fabButton = v.findViewById(R.id.fabButton);
        fabButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fabButton) {
            Fragment fragment = new AddTodo();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.frame_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View view;
        private CheckBox checkBox;
        private String document;

        TasksViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            checkBox = view.findViewById(R.id.checkBox);
            if(checkBox.isChecked()) {
                checkBox.setChecked(false);
            }

            checkBox.setOnClickListener(this);
        }

        void setProductName(String titleParams) {
            TextView title = view.findViewById(R.id.cardTask);
            title.setText(titleParams);
        }

        void setProductDate(Date dateParams) {
            TextView date = view.findViewById(R.id.cardDate);
            DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            date.setText(dateFormat.format(dateParams));
        }

        void setProductType(String typeParams) {
            TextView type = view.findViewById(R.id.cardType);
            type.setText(typeParams);
        }

        void getDocumentId(String document) {
            this.document = document;
        }

        @Override
        public void onClick(View view) {
            FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if(checkBox.isChecked()) {
                checkBox.setChecked(true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (user != null) {
                            firestoreDB.collection("users").document(user.getUid()).collection("tasks").document(document)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getActivity(), "Completed Task", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }, 2000);

            } else {

            }

        }
    }
}