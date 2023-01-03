package com.example.skillmatrix.ui.scheduler;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.skillmatrix.Login;
import com.example.skillmatrix.MainActivity;
import com.example.skillmatrix.R;
import com.example.skillmatrix.databinding.FragmentSlideshowBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SchedulerFragment extends Fragment{

    private FragmentSlideshowBinding binding;
    EditText meetTitle,meetDesc,meetLocation;
    TextView textView;
    boolean[] selectedEmail;
    StringBuilder stringBuilder = new StringBuilder();
    ArrayList<Integer> langList = new ArrayList<>();
    String[] emailArray=new String[10000];
    ArrayList<String> emailList=new  ArrayList<String>();
    Button btnDatePicker, btnTimePicker,btnDatePicker_end, btnTimePicker_end;
    EditText txtDate, txtTime,txtDate_end, txtTime_end;
    private int mYear, mMonth, mDay, mHour, mMinute,mYear_end, mMonth_end, mDay_end, mHour_end, mMinute_end;

    int j=0,k=0;
    public static String rootPath = "users";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SchedulerViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SchedulerViewModel.class);
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

      // final TextView textView = binding.textSlideshow;
      //  slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        View view = inflater.inflate(R.layout.fragment_scheduler, container, false);
        Button schedule_button;
        ArrayList<String> empEmailList = new ArrayList<String>();
        schedule_button = (Button) view.findViewById(R.id.scheduler_button);
        meetTitle=(EditText) view.findViewById(R.id.meeting_title);
        meetDesc=(EditText) view.findViewById(R.id.meeting_desc);
        meetLocation=(EditText) view.findViewById(R.id.meeting_location);
        btnDatePicker=(Button) view.findViewById(R.id.btn_date);
        btnTimePicker=(Button) view.findViewById(R.id.btn_time);
        txtDate=(EditText) view.findViewById(R.id.in_date);
        txtTime=(EditText) view.findViewById(R.id.in_time);

        btnDatePicker_end=(Button) view.findViewById(R.id.btn_date_end);
        btnTimePicker_end=(Button) view.findViewById(R.id.btn_time_end);
        txtDate_end=(EditText) view.findViewById(R.id.in_date_end);
        txtTime_end=(EditText) view.findViewById(R.id.in_time_end);
        textView = (TextView) view.findViewById(R.id.emailList);

        //  selectedEmail = new boolean[   emailArray.length];
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnTimePicker.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                txtTime.setText(hourOfDay + ":" + minute);
                                mHour=hourOfDay;
                                mMinute=minute;
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }});

        btnDatePicker_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear_end = c.get(Calendar.YEAR);
                mMonth_end = c.get(Calendar.MONTH);
                mDay_end = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtDate_end.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                mYear_end = year;
                                mMonth_end = monthOfYear;
                                mDay_end = dayOfMonth;

                            }
                        }, mYear_end, mMonth_end, mDay_end);
                datePickerDialog.show();
            }
        });
        btnTimePicker_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour_end = c.get(Calendar.HOUR_OF_DAY);
                mMinute_end = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                txtTime_end.setText(hourOfDay + ":" + minute);
                                mHour_end=hourOfDay;
                                mMinute_end=minute;
                            }
                        }, mHour_end, mMinute_end, false);
                timePickerDialog.show();
            }});

        ///

        textView.setText("");

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection(rootPath)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        int size = 0; //db.collection(document.getId()).get().getResult().size();
                                        empEmailList.add(document.getId());

                                        String empID = document.getId();
                                        //   Toast.makeText(getContext(),db.toString()+"->"+document.getId()+ " >> " + size + " => " +  db.collection(rootPath).document().collection(document.getId().toString()), Toast.LENGTH_LONG).show();
                                        Log.i("##########", document.getId() + " >> " +document.getString("email")+ " >> " + size + " => " + db.collection(rootPath).document().getPath());
                                        ///////////////////
                                        //  db.collection(rootPath).document().collection(empID).get().getResult();
                                        // emailArray[++j]=document.getString("email");
                                        emailList.add(document.getString("email").toString());
                                        // emailArray[++k]=document.getString("email").toString();
                                        Log.i("**", String.valueOf(emailList.size())+"-"+document.getString("email").toString());
                                        ////////////////////////////

                                    }

                                } else {
                                    Log.w("TAG", "Error getting documents.", task.getException());
                                }

                            }

                        });
                ///
                Log.i("**", String.valueOf(emailList.size()));
                emailArray = new String[emailList.size()];
                for(int i=0 ; i< emailList.size();i++){
                    emailArray[i] = emailList.get(i);
                    Log.i("**",emailList.get(i));
                    //getProductName or any suitable method
                }

                // assign variable
                // initialize selected language array
                selectedEmail = new boolean[emailList.size()];
                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                // set title
                builder.setTitle("Select Invitees");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(emailArray, selectedEmail, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position  in lang list
                            langList.add(i);
                            // Sort array list
                            Collections.sort(langList);
                        } else {
                            // when checkbox unselected
                            // Remove position from langList
                            langList.remove(Integer.valueOf(i));
                        }

                    }

                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        stringBuilder = new StringBuilder();
                        // use for loop
                        for (int j = 0; j < langList.size(); j++) {
                            // concat array value
                            stringBuilder.append(emailArray[langList.get(j)]);
                            //   stringBuilder.append(emailArray.get(langList.get(j)));

                            // check condition
                            if (j != langList.size() - 1) {
                                // When j value  not equal
                                // to lang list size - 1
                                // add comma
                                stringBuilder.append(", ");
                            }
                        }
                        // set text on textView
                        textView.setText(stringBuilder.toString());
                        empEmailList.clear();
                        emailArray = null;
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < selectedEmail.length; j++) {
                            // remove all selection
                            selectedEmail[j] = false;
                            // clear language list
                            langList.clear();
                            // clear text view value
                            textView.setText("");
                        }
                    }
                });
                // show dialog
                builder.show();
            }
        });
        schedule_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getContext(), "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
                String mTitle = meetTitle.getText().toString(), mDesc = meetDesc.getText().toString(), mLocation = meetLocation.getText().toString();
                Calendar beginTime = Calendar.getInstance();
                // beginTime.set(mYear, mMonth, mDay, mHour, mMinute);
                // beginTime.set(2022, 11, 1, 5, 0);
                beginTime.set(Calendar.YEAR, mYear);
                beginTime.set(Calendar.MONTH, mMonth); // Months are 0-based!
                beginTime.set(Calendar.DAY_OF_MONTH, mDay);
                beginTime.set(Calendar.HOUR_OF_DAY,mHour);
                beginTime.set(Calendar.MINUTE,mMinute);
                Calendar endTime = Calendar.getInstance();
                // endTime.set(mYear_end, mMonth_end, mDay_end, mHour_end, mMinute_end);
                endTime.set(Calendar.YEAR, mYear_end);
                endTime.set(Calendar.MONTH, mMonth_end); // Months are 0-based!
                endTime.set(Calendar.DAY_OF_MONTH, mDay_end);
                endTime.set(Calendar.HOUR_OF_DAY,mHour_end);
                endTime.set(Calendar.MINUTE,mMinute_end);
                Intent i =   new Intent(Intent.ACTION_INSERT);//Intent(Intent.ACTION_EDIT);//
                i.setData(CalendarContract.Events.CONTENT_URI);
                i.putExtra(CalendarContract.Events.TITLE, mTitle);
                i.putExtra(CalendarContract.Events.DESCRIPTION, mDesc);
                i.putExtra(CalendarContract.Events.EVENT_LOCATION, mLocation);
                i.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
                i.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
                i.putExtra(CalendarContract.Events.ALL_DAY, false);
                //   i.putExtra(Intent.EXTRA_EMAIL, "atishyareddy@gmail.com,and22wlu@gmail.com,onal0980@mylaurier.ca,sani1750@mylaurier.ca,prab0936@mylaurier.ca");
                //  i.putExtra(Intent.EXTRA_EMAIL, "sani1750@mylaurier.ca");
                i.putExtra(Intent.EXTRA_EMAIL,stringBuilder.toString() );
                stringBuilder = null;
             /*   if (validateDates(startDate + "T" + beginTime.getTimeInMillis() + ":00", endDate + "T" + endTime.getTimeInMillis() + ":00")) {
                    Toast.makeText(v.getContext(), "Invalid Start and End Dates", Toast.LENGTH_LONG).show();
                }
               */
                if(i.resolveActivity(getContext().getPackageManager()) != null){
                    startActivity(i);

                    meetDesc.setText("");meetLocation.setText("");txtDate.setText(""); txtTime.setText("");
                    txtDate_end.setText(""); txtTime_end.setText(""); meetTitle.setText("");
                    textView.setText("");
                    //  startActivityForResult(i, CalendarContract.Events.REQUEST_CODE_ADD_CALENDAR);
                } else {
                    Toast.makeText(getContext(), "Launch Error", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(), "Launch Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
               return view;
    }
    public Button button;

  /*
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView( inflater,  container,  savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_scheduler, container, false);
        button = view.findViewById(R.id.scheduler_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(getContext(), "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
            }

        });
        return view;
    }


    public void onClick(View view) {
        button.setVisibility(View.GONE);
    }
    */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /*     @Override
       public void onClick(View view) {
            Toast.makeText(getContext(), "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
            meetTitle=view.findViewById(R.id.meeting_title);
            meetDesc=view.findViewById(R.id.meeting_desc);
            meetLocation=view.findViewById(R.id.meeting_location);
            String mTitle=meetTitle.getText().toString(),mDesc=meetDesc.getText().toString(),mLocation=meetLocation.getText().toString();
            Intent i = new Intent(Intent.ACTION_INSERT);
            i.setData(CalendarContract.CONTENT_URI);
            i.putExtra(CalendarContract.Events.TITLE,mTitle);
            i.putExtra(CalendarContract.Events.DESCRIPTION,mDesc);
            i.putExtra(CalendarContract.Events.EVENT_LOCATION,mLocation);
            i.putExtra(CalendarContract.Events.ALL_DAY,true);
            i.putExtra(Intent.EXTRA_EMAIL, "atishyareddy@gmail.com,and22wlu@gmail.com");
            if(i.resolveActivity(getActivity().getPackageManager())!=null){
                startActivity(i);
            }
    else{
                Toast.makeText(getContext(), "Launch Error", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), "Launch Error", Toast.LENGTH_SHORT).show();
            }
        }*/
    private Boolean validateDates(String start, String end) {
        Date sDate = null, eDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            sDate = sdf.parse(start);
            eDate = sdf.parse(end);
            Log.i("StartD", sDate.toString());
            Log.i("EndD", eDate.toString());
            Log.i("Compareto", String.valueOf(sDate.compareTo(eDate) < 0));
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (sDate.compareTo(eDate) < 0)
            return false;
        else
            return true;
    }

    //Checked with DateTime parameters are double Digits

    private String validate(int time) {
        if (time < 10)
            return "0" + String.valueOf(time);
        else
            return String.valueOf(time);
    }


}