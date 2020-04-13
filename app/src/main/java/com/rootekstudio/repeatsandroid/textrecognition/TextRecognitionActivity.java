package com.rootekstudio.repeatsandroid.textrecognition;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.rootekstudio.repeatsandroid.JsonFile;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RepeatsSetInfo;
import com.rootekstudio.repeatsandroid.RepeatsSingleItem;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.activities.AddEditSetActivity;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class TextRecognitionActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecognizedStringsAdapter adapter;
    List<String> recognizedStrings;
    static String selected = "0";
    DatabaseHelper DB;
    String setID;
    int itemID;
    TextInputEditText questionField;
    TextInputEditText answerField;
    LinearLayout textFields;
    AlertDialog dialog;
    AlertDialog selectSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DB = new DatabaseHelper(this);

        recognizedStrings = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewTextRecognition);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.getRecognizedStrings().remove(viewHolder.getLayoutPosition());
                adapter.notifyItemRemoved(viewHolder.getLayoutPosition());
            }
        };

        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recyclerView);

        textFields = findViewById(R.id.linearTextRecognitionEditTexts);

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, RequestCodes.PICK_IMAGE_FOR_RECOGNITION);

        questionField = findViewById(R.id.questionInputTR);
        answerField = findViewById(R.id.answerInputTR);

        questionField.setInputType(InputType.TYPE_NULL);
        answerField.setInputType(InputType.TYPE_NULL);

        questionField.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                questionField.setInputType(InputType.TYPE_CLASS_TEXT);
                questionField.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(questionField, InputMethodManager.SHOW_FORCED);
                return true;
            }
        });

        answerField.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                answerField.setInputType(InputType.TYPE_CLASS_TEXT);
                answerField.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(answerField, InputMethodManager.SHOW_FORCED);

                return true;
            }
        });

        questionField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    selected = "0";
                } else {
                    questionField.setInputType(InputType.TYPE_NULL);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(questionField.getWindowToken(), 0);
                }
            }
        });

        answerField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    selected = "1";
                } else {
                    answerField.setInputType(InputType.TYPE_NULL);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(answerField.getWindowToken(), 0);
                }
            }
        });

        questionField.requestFocus();
    }

    public void endTRClick(View view) {
        saveItems();

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setBackground(getDrawable(R.drawable.dialog_shape));
        View layout = LayoutInflater.from(this).inflate(R.layout.choose_where_save_set_tr, null);
        LinearLayout saveAsNew = layout.findViewById(R.id.saveAsNew);
        LinearLayout saveToExistingSet = layout.findViewById(R.id.saveToExistingSet);

        saveAsNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
                String date = s.format(new Date());
                String newSetID = "R" + date;

                SimpleDateFormat createD = new SimpleDateFormat("dd.MM.yyyy");
                String createDate = createD.format(new Date());

                RepeatsSetInfo list;
                if (Locale.getDefault().toString().equals("pl_PL")) {
                    list = new RepeatsSetInfo("", newSetID, createDate, "true", "", "false", "pl_PL", "en_GB");
                } else {
                    list = new RepeatsSetInfo("", newSetID, createDate, "true", "", "false", "en_US", "es_ES");
                }

                //Registering set in database
                DB.CreateSet(newSetID);
                DB.AddName(list);
                JsonFile.putSetToJSON(TextRecognitionActivity.this, newSetID);

                Intent intent = new Intent(TextRecognitionActivity.this, AddEditSetActivity.class);
                DB.copyQuestionsAndAnswersToAnotherTable(setID, newSetID);
                List<RepeatsSingleItem> set = DB.AllItemsSET(newSetID, -1);
                if(set.size() == 0) {
                    DB.AddItem(newSetID);
                }
                intent.putExtra("ISEDIT", newSetID);
                intent.putExtra("NAME", "");
                startActivity(intent);
                dialog.cancel();
                finish();
            }
        });

        saveToExistingSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

                MaterialAlertDialogBuilder dialogSelectSet = new MaterialAlertDialogBuilder(TextRecognitionActivity.this);
                dialogSelectSet.setBackground(getDrawable(R.drawable.dialog_shape));

                View mainView = LayoutInflater.from(TextRecognitionActivity.this).inflate(R.layout.linear_select_set_tr, null);
                LinearLayout linearLayout = mainView.findViewById(R.id.linearLayoutSelectSetTR);
                List<RepeatsSetInfo> setsInfo = DB.AllItemsLIST(-1);

                for (int i = 0; i < setsInfo.size(); i++) {
                    RepeatsSetInfo setInfo = setsInfo.get(i);
                    View singleView = LayoutInflater.from(TextRecognitionActivity.this).inflate(R.layout.single_textview, null);
                    singleView.setTag(R.string.Tag_id_0, setInfo.getTableName());
                    singleView.setTag(R.string.Tag_id_1, setInfo.getitle());

                    TextView textView = singleView.findViewById(R.id.singleTextView);
                    textView.setText(setInfo.getitle());

                    singleView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String pasteSetID = view.getTag(R.string.Tag_id_0).toString();
                            String pasteSetName = view.getTag(R.string.Tag_id_1).toString();
                            DB.copyQuestionsAndAnswersToAnotherTable(setID, pasteSetID);
                            Intent intent = new Intent(TextRecognitionActivity.this, AddEditSetActivity.class);
                            intent.putExtra("ISEDIT", pasteSetID);
                            intent.putExtra("NAME", pasteSetName);
                            startActivity(intent);
                            selectSet.cancel();
                            finish();
                        }
                    });
                    linearLayout.addView(singleView);
                }

                dialogSelectSet.setView(mainView);
                dialogSelectSet.setNegativeButton(R.string.Cancel, null);
                dialogSelectSet.setTitle("Gdzie zapisać zestaw?");
                selectSet = dialogSelectSet.create();
                selectSet.show();
            }
        });

        dialogBuilder.setTitle("Gdzie zapisać zestaw?");
        dialogBuilder.setView(layout);
        dialogBuilder.setNegativeButton(R.string.Cancel, null);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.PICK_IMAGE_FOR_RECOGNITION) {
            if (resultCode == RESULT_OK) {
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
                dialogBuilder.setBackground(getDrawable(R.drawable.dialog_shape));
                dialogBuilder.setView(LayoutInflater.from(this).inflate(R.layout.loading_tr, null));
                dialogBuilder.setCancelable(false);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                Uri selectedImage = data.getData();
                FirebaseVisionImage image = null;
                try {
                    image = FirebaseVisionImage.fromFilePath(this, selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();

                Task<FirebaseVisionText> result =
                        detector.processImage(image)
                                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                    @Override
                                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                        int textBlockIndex = 0;
                                        int lineIndex = 0;
                                        int mostLines = -1;

                                        for(int i = 0; i < firebaseVisionText.getTextBlocks().size(); i++) {
                                            FirebaseVisionText.TextBlock textBlock = firebaseVisionText.getTextBlocks().get(i);
                                            if(i == 0) {
                                                mostLines = textBlock.getLines().size();
                                            }
                                            else {
                                                if(textBlock.getLines().size() > mostLines) {
                                                    mostLines = textBlock.getLines().size();
                                                }
                                            }
                                        }

                                        for (int i = 0; i < mostLines; i++) {
                                            for(int j = 0; j < firebaseVisionText.getTextBlocks().size(); j++) {
                                                FirebaseVisionText.TextBlock textBlock = firebaseVisionText.getTextBlocks().get(j);
                                                List<FirebaseVisionText.Line> lines = textBlock.getLines();
                                                if(i < lines.size()) {
                                                    List<FirebaseVisionText.Element> elements = lines.get(i).getElements();
                                                    StringBuilder text = new StringBuilder();
                                                    for(FirebaseVisionText.Element element : elements) {
                                                        text.append(element.getText()).append(" ");
                                                    }

                                                    recognizedStrings.add(text.toString());
                                                }
                                            }
                                        }

                                        adapter = new RecognizedStringsAdapter(recognizedStrings);
                                        recyclerView.setAdapter(adapter);

                                        dialog.cancel();

                                        if (recognizedStrings.size() == 0) {
                                            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(TextRecognitionActivity.this);
                                            dialogBuilder.setBackground(getDrawable(R.drawable.dialog_shape));
                                            dialogBuilder.setView(LayoutInflater.from(TextRecognitionActivity.this).inflate(R.layout.not_recognize_text, null));
                                            dialogBuilder.setCancelable(false);
                                            dialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                    finish();
                                                }
                                            });

                                            dialogBuilder.show();
                                        }
                                        else {
                                            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
                                            String date = s.format(new Date());
                                            setID = "R" + date;
                                            //Registering set in database
                                            DB.CreateSet(setID);

                                            //Adding single item (with question and answer) to database
                                            DB.AddItem(setID);
                                            itemID = 1;
                                        }
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
            }
            else {
                finish();
            }
        }
    }

    public void previousItem(View view) {
        saveItems();
        int childCount = textFields.getChildCount();
        for (int i = childCount - 1; i > 1; i--) {
            textFields.removeViewAt(i);
        }

        itemID--;

        if (itemID == 1) {
            view.setVisibility(View.INVISIBLE);
        }

        List<String> questionAndAnswer = DB.getSingleQuestionAndAnswer(setID, itemID);
        questionField.setText(questionAndAnswer.get(0));
        String fullAnswer = questionAndAnswer.get(1);

        Scanner scanner = new Scanner(fullAnswer);
        int line = 0;
        if (scanner.hasNextLine()) {
            while (scanner.hasNextLine()) {
                if (line == 0) {
                    answerField.setText(scanner.nextLine());
                    line++;
                } else {
                    addAnotherAnswer(scanner.nextLine());
                }
            }
        }

        scanner.close();
        questionField.requestFocus();
    }

    public void nextItem(View view) {
        saveItems();

        if (itemID == 1) {
            ImageButton previous = findViewById(R.id.buttonBackTR);
            previous.setVisibility(View.VISIBLE);
        }

        itemID++;

        int childCount = textFields.getChildCount();
        for (int i = childCount - 1; i > 1; i--) {
            textFields.removeViewAt(i);
        }

        questionField.setText("");
        answerField.setText("");

        List<String> questionAndAnswer = DB.getSingleQuestionAndAnswer(setID, itemID);
        if (questionAndAnswer == null) {
            DB.AddItem(setID);
        } else {
            questionField.setText(questionAndAnswer.get(0));
            String fullAnswer = questionAndAnswer.get(1);

            Scanner scanner = new Scanner(fullAnswer);
            int line = 0;
            if (scanner.hasNextLine()) {
                while (scanner.hasNextLine()) {
                    if (line == 0) {
                        answerField.setText(scanner.nextLine());
                        line++;
                    } else {
                        addAnotherAnswer(scanner.nextLine());
                    }
                }
            }
            scanner.close();
        }

        questionField.requestFocus();
    }

    private void saveItems() {
        int allEditTexts = textFields.getChildCount();
        StringBuilder answer = new StringBuilder();
        answer.append(answerField.getText().toString());

        if (allEditTexts > 2) {
            for (int i = 2; i < allEditTexts; i++) {
                TextInputEditText editText = textFields.getChildAt(i).findViewById(R.id.answerInputTR);
                String singleAnswer = editText.getText().toString();
                if (!singleAnswer.equals("")) {
                    answer.append(RepeatsHelper.breakLine).append(singleAnswer);
                }
            }
        }

        DB.InsertValueByID(setID, itemID, "question", questionField.getText().toString());
        DB.InsertValueByID(setID, itemID, "answer", answer.toString());
    }

    private void addAnotherAnswer(String text) {
        View field = LayoutInflater.from(this).inflate(R.layout.add_another_question_tr, textFields, false);
        ImageButton removeView = field.findViewById(R.id.removeAnswerTR);

        removeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View parent = (View) view.getParent();
                textFields.removeView(parent);
            }
        });

        TextInputEditText editText = field.findViewById(R.id.answerInputTR);
        editText.setInputType(InputType.TYPE_NULL);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    View parent = (View) view.getParent().getParent().getParent();
                    selected = String.valueOf(textFields.indexOfChild(parent));
                } else {
                    editText.setInputType(InputType.TYPE_NULL);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            }
        });
        editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(editText, InputMethodManager.SHOW_FORCED);

                return true;
            }
        });

        if (text != null) {
            editText.setText(text);
        }

        textFields.addView(field);
    }

    public void addAnotherAnswerClick(View view) {
        addAnotherAnswer(null);
    }

    @Override
    protected void onDestroy() {
        if(setID != null) {
            DB.DeleteSet(setID);
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
