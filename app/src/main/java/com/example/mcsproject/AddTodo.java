package com.example.mcsproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddTodo extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private View view;
    private EditText titleEt, dateEt, timeEt;
    private Button submit;
    private Spinner typeSpinner;
    private final Calendar dateCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dateSetListener;

    // data get from edit text, spinner
    private String titleValue;
    private String timeValue;
    private Date dateValue;
    private String typeValue;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_todo_add, container, false);
        init();
        return view;
    }

    private void init() {
        dateEt = view.findViewById(R.id.dateActivity);
        titleEt = view.findViewById(R.id.titleActivity);
        timeEt = view.findViewById(R.id.timeActivity);
        typeSpinner = view.findViewById(R.id.typeActivity);
        submit = view.findViewById(R.id.button_submit);

        Log.wtf("Title", titleValue);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.type_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(this);

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                dateCalendar.set(Calendar.YEAR, i);
                dateCalendar.set(Calendar.MONTH, i1);
                dateCalendar.set(Calendar.DAY_OF_MONTH, i2);
                dateValue = dateCalendar.getTime();
                updateLabel();
            }
        };
        dateEt.setOnClickListener(this);
        timeEt.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    private void updateLabel(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dateEt.setText(dateFormat.format(dateCalendar.getTime()));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.dateActivity) {
            new DatePickerDialog(getActivity(), dateSetListener, dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if(view.getId() == R.id.timeActivity) {
            int currentHour = dateCalendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = dateCalendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    String amPm;
                    if(i >= 12) {
                        amPm = "PM";
                    } else {
                        amPm = "AM";
                    }
                    String timeT = String.format("%02d:%02d ", i, i1);
                    Log.wtf("TAG", timeT);
                    timeValue = timeT;
                    timeEt.setText(timeT + amPm);
                }
            }, currentHour, currentMinute, false);
            timePickerDialog.show();
        } else if(view.getId() == R.id.button_submit) {
            titleValue = titleEt.getText().toString();
            Log.wtf("TAG", titleValue + " " + dateValue + " " +timeValue + " " + typeValue);

            if(titleValue == null) {
                Toast.makeText(getActivity(), "Please fill title for activity.", Toast.LENGTH_SHORT).show();
            } else if(dateValue == null) {
                Toast.makeText(getActivity(), "Please fill date for activity.", Toast.LENGTH_SHORT).show();
            } else if(timeValue == null) {
                Toast.makeText(getActivity(), "Please fill time for activity.", Toast.LENGTH_SHORT).show();
            } else {
                // store to firestore
                Map<String, Object> data = new HashMap<>();
                data.put("title", titleValue);
                data.put("date", dateValue);
                data.put("time", timeValue);
                data.put("type", typeValue);
                data.put("createdAt", FieldValue.serverTimestamp());
                data.put("flag", true);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    FirebaseFirestore.getInstance().collection("users")
                            .document(user.getUid())
                            .collection("tasks")
                            .add(data);
                }

                Toast.makeText(getActivity(), "Successfully Made The Task", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

//        Toast.makeText(getActivity(), "" + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
        typeValue = (String) adapterView.getItemAtPosition(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}