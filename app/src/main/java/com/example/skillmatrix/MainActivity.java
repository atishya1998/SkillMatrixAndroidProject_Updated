package com.example.skillmatrix;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.skillmatrix.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavigationView navigationView;
    private NavController navController;
    private Bundle bundle;
    public static String rootPath = "users";
    public static String hr_email ;
    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        Intent intent = getIntent(); //get intent from login activity
        Log.i("************",intent.getStringExtra("id").toString());
        fireStore.collection(rootPath).document(intent.getStringExtra("id").toString()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    Log.i("************",task.getResult().get("hr").toString());
                    hr_email=task.getResult().get("hr").toString();
                }
            }});
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Notify HR", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                final String body,subject,cc;
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Connect with HR");

                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText subject_textbox = new EditText(MainActivity.this);
                subject_textbox.setHint("Enter Email Subject");
                layout.addView(subject_textbox);

                final EditText body_textbox = new EditText(MainActivity.this);
                body_textbox.setHint("Enter Email Body");
                layout.addView(body_textbox);

                final EditText to_textbox = new EditText(MainActivity.this);
                to_textbox.setText(hr_email);
                layout.addView(to_textbox);  //hr_email

                final EditText cc_textbox = new EditText(MainActivity.this);
                cc_textbox.setHint("Enter CC recepients");
                layout.addView(cc_textbox);  //hr_email

                alert.setView(layout);
                alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String subject = subject_textbox.getText().toString();
                        String body = body_textbox.getText().toString();
                        String cc = cc_textbox.getText().toString();
                        String hr = to_textbox.getText().toString();
                        String mailto = "mailto:"+ hr +
                                "?cc=" + cc +
                                "&subject=" + Uri.encode(subject) +
                                "&body=" + Uri.encode(body);

                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse(mailto));
                        try {
                            startActivity(emailIntent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(getApplicationContext(), "Error to open email app", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(MainActivity.this, "Sent Sucessfully", Toast.LENGTH_LONG).show();

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();

            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_notify_hr, R.id.nav_scheduler)
                .setOpenableLayout(drawer)
                .build();
       // Intent intent = getIntent(); //get intent from login activity
        String email = intent.getStringExtra("email");
        String id = intent.getStringExtra("id");
        String role = intent.getStringExtra("role");
        View header =  navigationView.getHeaderView(0);
        TextView navEmail = header.findViewById(R.id.navEmail);
        TextView navRole = header.findViewById(R.id.navRole);
        navEmail.setText(email);
        navRole.setText(role);

        if (role.equals("employee")) { //if employee, hide nav items that are not for employees
            Menu menu = navigationView.getMenu();
            MenuItem nav_reportees = menu.findItem(R.id.nav_gallery);
            MenuItem nav_projects = menu.findItem(R.id.nav_slideshow);
            MenuItem nav_notify_hr = menu.findItem(R.id.nav_notify_hr);
            nav_notify_hr.setVisible(false);
            nav_reportees.setVisible(false);
            nav_projects.setVisible(false);
        }
        else  if (role.equals("hr")) { //if employee, hide nav items that are not for employees
            Menu menu = navigationView.getMenu();
            MenuItem nav_reportees = menu.findItem(R.id.nav_gallery);
            MenuItem nav_projects = menu.findItem(R.id.nav_slideshow);
            //MenuItem nav_notify_hr = menu.findItem(R.id.nav_notify_hr);
           // nav_notify_hr.setVisible(false);
            nav_reportees.setVisible(false);
            nav_projects.setVisible(false);
        }
        else
        {
            //If Manager Logs in
            //add this part later
            Menu menu = navigationView.getMenu();

            MenuItem nav_notify_hr = menu.findItem(R.id.nav_notify_hr);
            nav_notify_hr.setVisible(false);

        }
        bundle = new Bundle();
//        bundle.putString("email", message);
        bundle.putString("id", id);
        bundle.putString("role", role);
        //
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.navigate(R.id.nav_home, bundle); //navigate to home fragment with bundle data

        //set listener for navigation drawer to pass bundle to all the pages
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        navController.navigate(R.id.nav_home, bundle);
                        if (drawer.isDrawerOpen(GravityCompat.START)) { //close drawer if open
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                    case R.id.nav_gallery:
                        navController.navigate(R.id.nav_gallery, bundle);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                    case R.id.nav_slideshow:
                        navController.navigate(R.id.nav_slideshow, bundle);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                    case R.id.nav_notify_hr:
                        navController.navigate(R.id.nav_notify_hr, bundle);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                    case R.id.nav_scheduler:
                        navController.navigate(R.id.nav_scheduler, bundle);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (navController.getCurrentDestination().getId() != R.id.nav_home) {
            navController.navigate(R.id.nav_home, bundle);
        } else {
            finish();
        }

    }
}