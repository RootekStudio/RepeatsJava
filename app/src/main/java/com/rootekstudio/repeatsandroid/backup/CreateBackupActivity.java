package com.rootekstudio.repeatsandroid.backup;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

//import com.opencsv.bean.HeaderColumnNameMappingStrategy;
//import com.opencsv.bean.StatefulBeanToCsv;
//import com.opencsv.bean.StatefulBeanToCsvBuilder;
//import com.opencsv.exceptions.CsvDataTypeMismatchException;
//import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;

//import org.apache.commons.collections.ComparatorUtils;
//import org.apache.commons.collections.comparators.ComparableComparator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Comparator;
import java.util.List;

public class CreateBackupActivity extends AppCompatActivity {

    boolean allData = true;
    boolean sets = true;
    boolean stats = true;
    boolean setSettings = true;
    boolean deliveryRules = true;
    boolean communityID = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_backup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RadioButton allDataRadio = findViewById(R.id.backupAllRadio);
        CheckBox setsCheck = findViewById(R.id.setsCheck);
        CheckBox statsCheck = findViewById(R.id.statsCheck);
        CheckBox setSettingsCheck = findViewById(R.id.setSettingsCheck);
        CheckBox deliveryRulesCheck = findViewById(R.id.deliveryRulesCheck);
        CheckBox communityIdCheck = findViewById(R.id.communityIdCheck);
        Button selectSetsButton = findViewById(R.id.selectSets);

        setsCheck.setEnabled(false);
        selectSetsButton.setEnabled(false);
        statsCheck.setEnabled(false);
        setSettingsCheck.setEnabled(false);
        deliveryRulesCheck.setEnabled(false);
        communityIdCheck.setEnabled(false);

        allDataRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            allData = isChecked;

            setsCheck.setEnabled(!isChecked);
            selectSetsButton.setEnabled(!isChecked);
            statsCheck.setEnabled(!isChecked);
            setSettingsCheck.setEnabled(!isChecked);
            deliveryRulesCheck.setEnabled(!isChecked);
            communityIdCheck.setEnabled(!isChecked);
        });

        setsCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sets = isChecked;

            statsCheck.setEnabled(isChecked);
            statsCheck.setChecked(isChecked);
            setSettingsCheck.setEnabled(isChecked);
            setSettingsCheck.setChecked(isChecked);
            deliveryRulesCheck.setEnabled(isChecked);
            deliveryRulesCheck.setChecked(isChecked);
            selectSetsButton.setEnabled(isChecked);
        });
        statsCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            stats = isChecked;
        });
        setSettingsCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setSettings = isChecked;
        });
        deliveryRulesCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            deliveryRules = isChecked;
        });
        communityIdCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            communityID = isChecked;
        });
    }

    public void selectSetsClick(View view) {

    }

    public void createBackupClick(View view) throws IOException {
//        List<SetFullInfo> setsFullInfo = RepeatsDatabase.getInstance(this).getAllSetsFullInfo();
//        File file = new File(getFilesDir(), "sets_info.csv");
//        Writer writer = new FileWriter(file);
//        StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
//
//        beanToCsv.write(setsFullInfo);
//        writer.close();
//
//        FileInputStream jsonStream = new FileInputStream(file);
//        BufferedReader jReader = new BufferedReader(new InputStreamReader(jsonStream));
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = jReader.readLine()) != null) {
//            sb.append(line);
//        }
//        String values = sb.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
