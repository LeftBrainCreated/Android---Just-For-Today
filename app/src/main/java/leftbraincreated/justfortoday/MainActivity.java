package leftbraincreated.justfortoday;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Date mCleanDate;
    String mCleanDateString;
    public static Boolean dialogVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Decide whether or not to keep toolbar
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        setFloatingActionButtonActivities(R.id.fab_menu);
        setFloatingActionButtonActivities(R.id.fab_daily_bread);
        setFloatingActionButtonActivities(R.id.fab_emergency);

        //If dialog is currently open, we don't want to reopen it
        if (savedInstanceState != null){
            dialogVisible = savedInstanceState.getBoolean(getString(R.string.MainActivity_dialog_open_bundle_key));
        }

        setmCleanDate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //If dialog is open when config changes, we don't want to open another one...
        outState.putBoolean(getString(R.string.MainActivity_dialog_open_bundle_key), dialogVisible);


    }

    private void setmCleanDate() {
        SharedPreferences sharedPreferences =
                getSharedPreferences(
                        getString(R.string.shared_pref_key),
                        Context.MODE_PRIVATE
                );

        //CleanDate is stored as SharedPref
        mCleanDateString = sharedPreferences.getString(getString(R.string.clean_date_key), "");

        if (mCleanDateString.equals("")){
            showDatePickerDialog(findViewById(R.id.textViewCleanTime));
        } else {
            //cleanDate has not been set yet
            TextView txtViewCleanDate = (TextView) findViewById(R.id.textViewCleanDate);
            txtViewCleanDate.setText(mCleanDateString);

            setCleanTime();
        }
    }

    public void showDatePickerDialog(View v) {
        if (!dialogVisible) {
            DialogFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.setCancelable(false);
            datePickerFragment.show(getFragmentManager(), "datePicker");
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        dialogVisible = false;
        mCleanDate = getDateFromDatePicker(datePicker);

        DateFormat dateFormat = DateFormat.getDateInstance();
        mCleanDateString = dateFormat.format(mCleanDate).toString();
        TextView txtViewCleanDate = (TextView) findViewById(R.id.textViewCleanDate);
        txtViewCleanDate.setText(mCleanDateString);

        //Add cleanDate to shared Prefs
        setCleanDateSharedPref();

        setCleanTime();
    }

    private void setLastOpenSharedPref() {

        Calendar today = Calendar.getInstance();


    }

    private void setCleanDateSharedPref(){
        SharedPreferences sharedPreferences =
                getSharedPreferences(
                        getString(R.string.shared_pref_key),
                        Context.MODE_PRIVATE
                );

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.clean_date_key), mCleanDateString);
        editor.apply();
    }

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    private void setCleanTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        try {
            Date cleanDate = dateFormat.parse(mCleanDateString);

            Calendar calendarCleanDate = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
            calendarCleanDate.setTime(sdf.parse(cleanDate.toString()));

            Calendar today = Calendar.getInstance();

            int yearsClean;
            int monthsClean;
            int daysClean;
            Calendar tmpCalender;

            yearsClean = today.get(Calendar.YEAR) - calendarCleanDate.get(Calendar.YEAR);

            //A bit of finagling to get cleanTime correct
            if (today.get(Calendar.MONTH) > calendarCleanDate.get(Calendar.MONTH)) {
                monthsClean = today.get(Calendar.MONTH) - calendarCleanDate.get(Calendar.MONTH);
            } else if (today.get(Calendar.MONTH) == calendarCleanDate.get(Calendar.MONTH)) {
                if (yearsClean > 0 && (today.get(Calendar.DATE) < calendarCleanDate.get(Calendar.DATE))) {
                    monthsClean = 11;
                    yearsClean = yearsClean - 1;
                } else {
                    monthsClean = 0;
                }
            } else {
                monthsClean = (today.get(Calendar.MONTH) + 12) - (calendarCleanDate.get(Calendar.MONTH));
                yearsClean = yearsClean - 1;
            }

            if (today.get(Calendar.DATE) >= calendarCleanDate.get(Calendar.DATE)) {
                daysClean = today.get(Calendar.DATE) - calendarCleanDate.get(Calendar.DATE);
            } else {
                tmpCalender = Calendar.getInstance();
                tmpCalender.set(
                        calendarCleanDate.get(Calendar.YEAR),
                        calendarCleanDate.get(Calendar.MONTH) + 1,
                        today.get(Calendar.DATE)
                );
                Long diff = tmpCalender.getTimeInMillis() - calendarCleanDate.getTimeInMillis();
                daysClean = (int) (diff / (24 * 60 * 60 * 1000));
            }

            String cleanTime = "";
            if (yearsClean > 0) {
                if (yearsClean == 1) {
                    cleanTime += yearsClean + " Year, ";
                } else {
                    cleanTime += yearsClean + " Years, ";
                }
                if (monthsClean == 0) {
                    cleanTime = cleanTime.replace(",", "");
                }
            }
            if (monthsClean > 0) {
                if (monthsClean == 1) {
                    cleanTime += monthsClean + " Month ";
                } else {
                    cleanTime += monthsClean + " Months ";
                }
            }
            if (daysClean > 0) {
                if (!cleanTime.equals("")) {
                    cleanTime += "\nand ";
                }
                if (daysClean == 1) {
                    cleanTime += daysClean + " Day";
                } else {
                    cleanTime += daysClean + " Days";
                }
            }

            TextView txtViewCleanTime = (TextView) findViewById(R.id.textViewCleanTime);
            txtViewCleanTime.setText(cleanTime);

            Long fullDaysDiff = today.getTimeInMillis() - calendarCleanDate.getTimeInMillis();
            int daysFullCount = (int) (fullDaysDiff / (24 * 60 * 60 * 1000));

            int keyTagColor = keyTagColor(daysFullCount, yearsClean, monthsClean);
            TextView txtViewCleanDate = (TextView) findViewById(R.id.textViewCleanDate);
            txtViewCleanDate.setBackgroundColor(keyTagColor);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Launch DailyMeditation Activity
    }

    private int keyTagColor(int daysFullCount, int yearsClean, int monthsClean) {
        int keyTagColor;
        Resources res = getResources();

        switch (yearsClean){
            case 0:
                if (monthsClean >= 9) {
                    keyTagColor = res.getColor(R.color.nineMonthsYellow);
                } else if (monthsClean >= 6) {
                    keyTagColor = res.getColor(R.color.sixMonthsBlue);
                } else {
                    if (daysFullCount >= 90) {
                        keyTagColor = res.getColor(R.color.ninetyDaysRed);
                    } else if (daysFullCount >= 60) {
                        keyTagColor = res.getColor(R.color.sixtyDaysGreen);
                    } else if (daysFullCount >= 30) {
                        keyTagColor = res.getColor(R.color.thirtyDaysOrange);
                    } else {
                        keyTagColor = res.getColor(R.color.oneDayWhite);
                }
            }
                break;
            case 1:
                if (monthsClean >= 6) {
                    keyTagColor = res.getColor(R.color.eighteenMonthsGrey);
                } else {
                    keyTagColor = res.getColor(R.color.oneYearGlowInTheDark);
                }
                break;
            default:
                keyTagColor = res.getColor(R.color.multipleYearsBlack);
                break;
        }
        return keyTagColor;
    }

    private void setFloatingActionButtonActivities(final int viewId) {

        //TODO: Maybe use this action button for "Emergency "I need to call someone now" Activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(viewId);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (viewId) {
                    case R.id.fab_emergency:
                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        break;
                    case R.id.fab_daily_bread:

                        Intent intent = new Intent(MainActivity.this, DailyBreadActivity.class);
                        startActivity(intent);

                        break;
                    case R.id.fab_menu:
                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        break;
                    default:
                        //Shouldn't ever happen
                        break;
                }
            }
        });
    }
}
