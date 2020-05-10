package com.rootekstudio.repeatsandroid.textrecognition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.SetsConfigHelper;
import com.rootekstudio.repeatsandroid.UIHelper;
import com.rootekstudio.repeatsandroid.activities.AddEditSetActivity;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.SingleSetInfo;
import com.rootekstudio.repeatsandroid.database.Values;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class TextRecognitionActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextInputEditText questionField;
    TextInputEditText answerField;
    LinearLayout textFields;
    ScrollView fieldsScrollView;
    TextInputEditText selectedField;

    static String selected = "0";
    String setID;
    int itemID;
    int defaultHeightScroll;
    int heightScroll190;
    int heightScroll220;

    RepeatsDatabase DB;
    GestureDetector gd;

    RecognizedStringsAdapter adapter;
    List<String> recognizedStrings;

    AlertDialog selectWhereSave;
    AlertDialog selectSet;
    AlertDialog loadingDialog;

    Uri photoURI;

    SetsConfigHelper setsConfigHelper;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setsConfigHelper = new SetsConfigHelper(this);

        fieldsScrollView = findViewById(R.id.scrollViewFieldsTR);
        recyclerView = findViewById(R.id.recyclerViewTextRecognition);
        questionField = findViewById(R.id.questionInputTR);
        answerField = findViewById(R.id.answerInputTR);
        textFields = findViewById(R.id.linearTextRecognitionEditTexts);

        loadingDialog = UIHelper.loadingDialog(getString(R.string.textDetectionInProgress), this);

        DB = RepeatsDatabase.getInstance(this);
        defaultHeightScroll = dpToPx(130, this);
        heightScroll190 = dpToPx(190, this);
        heightScroll220 = dpToPx(220, this);
        recognizedStrings = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recyclerView);

        Intent thisIntent = getIntent();

        if(thisIntent.getBooleanExtra("takePhoto", true)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss");
                    String imageDate = simpleDate.format(new Date());
                    String ImageName = imageDate + ".png";

                    photoFile = new File(getFilesDir(), "/shared/" + ImageName);
                    photoFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {

                    photoURI = FileProvider.getUriForFile(this,
                            "com.rootekstudio.repeatsandroid.fileprovider",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, RequestCodes.TAKE_IMAGE_FOR_RECOGNITION);
                }
            }
        }
        else {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(photoPickerIntent, RequestCodes.PICK_IMAGE_FOR_RECOGNITION);
        }

        questionField.setInputType(InputType.TYPE_NULL);
        answerField.setInputType(InputType.TYPE_NULL);

        gd = new GestureDetector(this, gestureListener);
        gd.setOnDoubleTapListener(doubleTapListener);

        questionField.setOnTouchListener(editTextTouchListener);
        questionField.setOnFocusChangeListener(editTextFocusChangeListener);
        answerField.setOnTouchListener(editTextTouchListener);
        answerField.setOnFocusChangeListener(editTextFocusChangeListener);

        questionField.requestFocus();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.PICK_IMAGE_FOR_RECOGNITION) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                FirebaseVisionImage image = null;
                try {
                    image = FirebaseVisionImage.fromFilePath(this, selectedImage);
                    recognizeText(image);
                } catch (IOException e) {
                    e.printStackTrace();
                    finish();
                }
                catch (SecurityException e) {
                    e.printStackTrace();

                    Toast.makeText(this, R.string.imageError, Toast.LENGTH_LONG).show();
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(photoPickerIntent, RequestCodes.PICK_IMAGE_FOR_RECOGNITION);
                }

            } else {
                finish();
            }
        }
        else if(requestCode == RequestCodes.TAKE_IMAGE_FOR_RECOGNITION) {
            if(resultCode == RESULT_OK) {
                FirebaseVisionImage image = null;
                try {
                    image = FirebaseVisionImage.fromFilePath(this, photoURI);
                    recognizeText(image);
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
            }
            else {
                finish();
            }
        }
    }

    void recognizeText(FirebaseVisionImage image) {
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        detector.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {

                        //get maximum count of lines from all text blocks
                        int mostLines = -1;

                        for (int i = 0; i < firebaseVisionText.getTextBlocks().size(); i++) {
                            FirebaseVisionText.TextBlock textBlock = firebaseVisionText.getTextBlocks().get(i);
                            if (i == 0) {
                                mostLines = textBlock.getLines().size();
                            } else {
                                if (textBlock.getLines().size() > mostLines) {
                                    mostLines = textBlock.getLines().size();
                                }
                            }
                        }

                        //get single line from every text block
                        for (int i = 0; i < mostLines; i++) {
                            for (int j = 0; j < firebaseVisionText.getTextBlocks().size(); j++) {
                                FirebaseVisionText.TextBlock textBlock = firebaseVisionText.getTextBlocks().get(j);
                                List<FirebaseVisionText.Line> lines = textBlock.getLines();
                                if (i < lines.size()) {
                                    List<FirebaseVisionText.Element> elements = lines.get(i).getElements();
                                    //get single element
                                    for (FirebaseVisionText.Element element : elements) {
                                        String text = element.getText();
                                        for (char c : text.toCharArray()) {
                                            if (Character.isLetterOrDigit(c)) {
                                                recognizedStrings.add(text);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        adapter = new RecognizedStringsAdapter(recognizedStrings);
                        recyclerView.setAdapter(adapter);

                        loadingDialog.cancel();

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
                        } else {
                            setID = setsConfigHelper.createTempSet();
                            itemID = 1;
                        }
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TextRecognitionActivity.this, R.string.cannotLaunchTR, Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                finish();
                            }
                        });
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
                String newSetID = createNewSet();
                Intent intent = new Intent(TextRecognitionActivity.this, AddEditSetActivity.class);
                intent.putExtra("ISEDIT", newSetID);
                intent.putExtra("NAME", "");
                startActivity(intent);

                selectWhereSave.cancel();
                finish();
            }
        });

        saveToExistingSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectWhereSave.cancel();

                MaterialAlertDialogBuilder dialogSelectSet = new MaterialAlertDialogBuilder(TextRecognitionActivity.this);
                dialogSelectSet.setBackground(getDrawable(R.drawable.dialog_shape));

                View mainView = LayoutInflater.from(TextRecognitionActivity.this).inflate(R.layout.linear_select_set_tr, null);
                LinearLayout linearLayout = mainView.findViewById(R.id.linearLayoutSelectSetTR);
                List<SingleSetInfo> setsInfo = DB.allSetsInfo(-1);

                for (int i = 0; i < setsInfo.size(); i++) {
                    SingleSetInfo setInfo = setsInfo.get(i);
                    View singleView = LayoutInflater.from(TextRecognitionActivity.this).inflate(R.layout.single_textview, null);
                    singleView.setTag(R.string.Tag_id_0, setInfo.getSetID());
                    singleView.setTag(R.string.Tag_id_1, setInfo.getSetName());

                    TextView textView = singleView.findViewById(R.id.singleTextView);
                    textView.setText(setInfo.getSetName());

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
                dialogSelectSet.setTitle(R.string.howSaveSet);
                selectSet = dialogSelectSet.create();
                selectSet.show();
            }
        });

        dialogBuilder.setTitle(R.string.howSaveSet);
        dialogBuilder.setView(layout);
        dialogBuilder.setNegativeButton(R.string.Cancel, null);
        selectWhereSave = dialogBuilder.create();
        selectWhereSave.show();
    }

    private String createNewSet() {

        String newSetID = setsConfigHelper.createNewSet(false, "");

        DB.copyQuestionsAndAnswersToAnotherTable(setID, newSetID);

        if (DB.itemsInSetCount(newSetID) == 0) {
            DB.addEmptyItemToSet(newSetID);
        }

        return newSetID;
    }

    public void previousItem(View view) {
        saveItems();
        removeAdditionalAnswersFields();

        itemID--;
        if (itemID == 1) {
            view.setVisibility(View.INVISIBLE);
        }

        List<String> questionAndAnswer = DB.getSingleQuestionAndAnswer(setID, itemID);
        questionField.setText(questionAndAnswer.get(0));
        String fullAnswer = questionAndAnswer.get(1);

        readAnotherAnswersAndAddToLayout(fullAnswer);
        setScrollViewHeight();

        questionField.requestFocus();
    }

    public void nextItem(View view) {
        saveItems();
        removeAdditionalAnswersFields();

        itemID++;
        if (itemID == 2) {
            ImageButton previous = findViewById(R.id.buttonBackTR);
            previous.setVisibility(View.VISIBLE);
        }

        questionField.setText("");
        answerField.setText("");

        List<String> questionAndAnswer = DB.getSingleQuestionAndAnswer(setID, itemID);
        if (questionAndAnswer == null) {
            DB.addEmptyItemToSet(setID);
        } else {
            questionField.setText(questionAndAnswer.get(0));
            String fullAnswer = questionAndAnswer.get(1);
            readAnotherAnswersAndAddToLayout(fullAnswer);
        }

        setScrollViewHeight();
        questionField.requestFocus();
    }

    private void removeAdditionalAnswersFields() {
        int childCount = textFields.getChildCount();
        for (int i = childCount - 1; i > 1; i--) {
            textFields.removeViewAt(i);
        }
    }

    private void readAnotherAnswersAndAddToLayout(String answer) {
        Scanner scanner = new Scanner(answer);
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

    private void setScrollViewHeight() {
        if (textFields.getChildCount() == 3) {
            fieldsScrollView.getLayoutParams().height = heightScroll190;
        } else if (textFields.getChildCount() > 3) {
            fieldsScrollView.getLayoutParams().height = heightScroll220;
        } else {
            fieldsScrollView.getLayoutParams().height = defaultHeightScroll;
        }
    }

    private void saveItems() {
        int allEditTexts = textFields.getChildCount();
        StringBuilder answerBuilder = new StringBuilder();
        answerBuilder.append(answerField.getText().toString().trim());

        if (allEditTexts > 2) {
            for (int i = 2; i < allEditTexts; i++) {
                TextInputEditText editText = textFields.getChildAt(i).findViewById(R.id.answerInputTR);
                String singleAnswer = editText.getText().toString();
                if (!singleAnswer.equals("")) {
                    singleAnswer = singleAnswer.trim();
                    answerBuilder.append(RepeatsHelper.breakLine).append(singleAnswer);
                }
            }
        }

        String question = questionField.getText().toString();
        String answer = answerBuilder.toString();
        answer = answer.trim();
        question = question.trim();

        //remove all break lines on beginning of string
        if (answer.length() > 2) {
            while (answer.substring(0, 2).equals(RepeatsHelper.breakLine)) {
                answer = answer.replaceFirst(RepeatsHelper.breakLine, "");
            }
        }

        DB.insertValueToSetByID(setID, itemID, Values.question, question);
        DB.insertValueToSetByID(setID, itemID, Values.answer, answer);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addAnotherAnswer(String text) {
        View field = LayoutInflater.from(this).inflate(R.layout.add_another_question_tr, textFields, false);
        ImageButton removeView = field.findViewById(R.id.removeAnswerTR);

        removeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View parent = (View) view.getParent();
                textFields.removeView(parent);
                if (textFields.getChildCount() == 3) {
                    fieldsScrollView.getLayoutParams().height = heightScroll190;
                } else if (textFields.getChildCount() == 2) {
                    fieldsScrollView.getLayoutParams().height = defaultHeightScroll;
                }
            }
        });

        TextInputEditText editText = field.findViewById(R.id.answerInputTR);
        editText.setInputType(InputType.TYPE_NULL);
        editText.setOnFocusChangeListener(editTextFocusChangeListener);
        editText.setOnTouchListener(editTextTouchListener);

        if (text != null) {
            editText.setText(text);
        }

        textFields.addView(field);
    }

    View.OnFocusChangeListener editTextFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                selectedField = (TextInputEditText) view;
                View parent = (View) view.getParent().getParent().getParent();
                selected = String.valueOf(textFields.indexOfChild(parent));
            } else {
                TextInputEditText editText = (TextInputEditText) view;
                editText.setInputType(InputType.TYPE_NULL);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }
    };

    public void addAnotherAnswerClick(View view) {
        int childCount = textFields.getChildCount();
        if (childCount == 2) {
            fieldsScrollView.getLayoutParams().height = heightScroll190;
        } else if (childCount == 3) {
            fieldsScrollView.getLayoutParams().height = heightScroll220;
        }

        addAnotherAnswer(null);
    }

    private int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    GestureDetector.OnDoubleTapListener doubleTapListener = new GestureDetector.OnDoubleTapListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            selectedField.setInputType(InputType.TYPE_CLASS_TEXT);
            selectedField.requestFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.showSoftInput(questionField, InputMethodManager.SHOW_FORCED);
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            return false;
        }
    };

    View.OnTouchListener editTextTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gd.onTouchEvent(motionEvent);
        }
    };

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

    GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }
    };

    @Override
    protected void onDestroy() {
        if (setID != null) {
            //Delete temporary set on exit
            DB.deleteSet(setID);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.set_from_photo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        else if(item.getItemId() == R.id.helpTR) {
            startActivity(new Intent(this, HelpTextRecActivity.class));
        }
        return true;
    }
}