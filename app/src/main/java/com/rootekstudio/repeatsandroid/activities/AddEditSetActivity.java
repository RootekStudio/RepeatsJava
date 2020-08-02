package com.rootekstudio.repeatsandroid.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.SetsConfigHelper;
import com.rootekstudio.repeatsandroid.UIHelper;
import com.rootekstudio.repeatsandroid.database.SetSingleItem;
import com.rootekstudio.repeatsandroid.database.Values;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class AddEditSetActivity extends AppCompatActivity {

    //View that contains ImageView
    ViewParent imagePreview;
    ViewGroup parent = null;
    boolean deleted = false;

    //Last index from database
    int lastIndex = 0;
    static boolean IsDark;

    //id of the table
    static String id;
    SetsConfigHelper setsConfigHelper;

    //region onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IsDark = UIHelper.DarkTheme(this, false);
        setContentView(R.layout.activity_repeats_add_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setsConfigHelper = new SetsConfigHelper(this);

        //Getting basic information about set (if new or  already existing, id, and ignore chars option)
        Intent intent = getIntent();
        final String editing = intent.getStringExtra("ISEDIT");
        String SetName = intent.getStringExtra("NAME");

        //Project name edit text
        final EditText editName = findViewById(R.id.projectname);
        editName.setOnFocusChangeListener(newName);

        if (!IsDark) {
            //editName.setBackgroundResource(R.drawable.editname_light);
        }

        parent = findViewById(R.id.AddRepeatsLinear);

        //if this is a new set
        if (editing.equals("FALSE")) {
            addView(parent, this);
            //Adding single item (with question and answer) to database
            id = setsConfigHelper.createNewSet(true, "");

        } else {
            id = editing;
            readSetFromDatabase(editing, SetName);
        }

        FloatingActionButton fab = findViewById(R.id.AddQuestionFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addView(parent, AddEditSetActivity.this);
                setsConfigHelper.getRepeatsDatabase().addEmptyItemToSet(id);
            }
        });
    }
    //endregion

    //region readSetFromDatabase
    void readSetFromDatabase(final String SetID, String NAME) {
        final LayoutInflater inflater = LayoutInflater.from(this);
        EditText editName = findViewById(R.id.projectname);
        editName.setText(NAME);

        Thread readFromDatabase = new Thread(new Runnable() {
            @Override
            public void run() {
                List<SetSingleItem> SET = setsConfigHelper.getRepeatsDatabase().allItemsInSet(SetID, -1);
                int ItemsCount = SET.size();

                for (int i = 0; i < ItemsCount; i++) {
                    SetSingleItem Single = SET.get(i);
                    int ID = Single.getItemID();
                    String Question = Single.getQuestion();
                    String Answer = Single.getAnswer();
                    String Image = Single.getImage();

                    final View child = inflater.inflate(R.layout.addrepeatslistitem, parent, false);

                    RelativeLayout RL = child.findViewById(R.id.RelativeAddItem);
                    RL.setTag(ID);

                    EditText Q = child.findViewById(R.id.questionBox);
                    EditText A = child.findViewById(R.id.answerBox);
                    ImageButton B = child.findViewById(R.id.deleteItem);
                    final ImageButton I = child.findViewById(R.id.addImage);
                    ImageView img = child.findViewById(R.id.imageView);
                    ImageButton imgbut = child.findViewById(R.id.deleteImage);
                    ImageButton addAnswer = child.findViewById(R.id.addAnswerButton);


                    Q.setOnFocusChangeListener(newText);
                    A.setOnFocusChangeListener(newAnswer);
                    B.setOnClickListener(deleteClick);
                    I.setOnClickListener(addImageClick);
                    imgbut.setOnClickListener(deleteImageClick);
                    addAnswer.setOnClickListener(addAnswerClick);

                    A.setFilters(new InputFilter[]{inputFilter});

                    Q.setText(Question);

                    Scanner scanner = new Scanner(Answer);
                    int an = 0;

                    if (scanner.hasNextLine()) {
                        while (scanner.hasNextLine()) {
                            String nextLine = scanner.nextLine();
                            if (an != 0) {
                                addAnswer(RL.findViewById(R.id.LinearQA), nextLine);
                            } else {
                                A.setText(nextLine);
                                an++;
                            }
                        }
                    } else {
                        A.setText(Answer);
                    }

                    scanner.close();

                    if (!Image.equals("")) {
                        I.setEnabled(false);

                        img.setVisibility(View.VISIBLE);
                        img.setTag(Image);
                        imgbut.setVisibility(View.VISIBLE);
                        try {
                            File file = new File(getFilesDir(), Image);
                            FileInputStream inputStream = new FileInputStream(file);

                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            img.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        imgbut.setOnClickListener(deleteImageClick);
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parent.addView(child);
                        }
                    });

                    if (i == ItemsCount - 1) {
                        lastIndex = ID;
                    }
                }
            }
        });
        readFromDatabase.start();
    }

    //endregion
    //region addView
    void addView(ViewGroup viewGroup, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.addrepeatslistitem, viewGroup, false);
        RelativeLayout rel = item.findViewById(R.id.RelativeAddItem);

        lastIndex++;
        rel.setTag(lastIndex);

        //Finding views
        ImageButton delete = item.findViewById(R.id.deleteItem);
        ImageButton addImage = item.findViewById(R.id.addImage);
        ImageButton deleteImage = item.findViewById(R.id.deleteImage);
        ImageButton addAnswer = item.findViewById(R.id.addAnswerButton);
        LinearLayout linear = item.findViewById(R.id.LinearQA);
        EditText question = linear.findViewById(R.id.questionBox);
        EditText answer = linear.findViewById(R.id.answerBox);

        //Setting listeners for views
        delete.setOnClickListener(deleteClick);
        addImage.setOnClickListener(addImageClick);
        deleteImage.setOnClickListener(deleteImageClick);
        addAnswer.setOnClickListener(addAnswerClick);
        question.setOnFocusChangeListener(newText);
        answer.setOnFocusChangeListener(newAnswer);

        answer.setFilters(new InputFilter[]{inputFilter});

        //Adding item to root view
        viewGroup.addView(item);
    }
    //endregion

    //region clicks
    private View.OnClickListener deleteClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            ViewGroup rootView = (ViewGroup) parent.getParent();

            int index = Integer.parseInt(parent.getTag().toString());
            ImageView image = parent.findViewById(R.id.imageView);
            if (image.getVisibility() == View.VISIBLE) {
                String imageName = image.getTag().toString();

                File file = new File(getFilesDir(), imageName);
                boolean del = file.delete();

                setsConfigHelper.getRepeatsDatabase().deleteImage(id, imageName);
            }

            if (rootView.getChildCount() != 1) {
                rootView.removeView(parent);
                setsConfigHelper.getRepeatsDatabase().deleteItemFromSet(id, index);
            }
        }
    };

    private View.OnClickListener addImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            imagePreview = view.getParent();
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(photoPickerIntent, RequestCodes.PICK_IMAGE);
        }
    };

    private final View.OnClickListener deleteImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View pView = (View) view.getParent();

            ImageView img = pView.findViewById(R.id.imageView);
            ImageButton imgBut = pView.findViewById(R.id.deleteImage);
            ImageButton imgAdd = pView.findViewById(R.id.addImage);

            final String imageName = img.getTag().toString();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(getFilesDir(), imageName);
                    boolean del = file.delete();
                    setsConfigHelper.getRepeatsDatabase().deleteImage(id, imageName);
                }
            });
            thread.start();

            img.setVisibility(View.GONE);
            img.setImageBitmap(null);
            img.setTag(null);
            imgBut.setVisibility(View.GONE);
            imgAdd.setEnabled(true);
        }
    };

    private View.OnClickListener addAnswerClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            addAnswer(view, "");
        }
    };
    //endregion

    //region focus
    View.OnFocusChangeListener newName = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus) {
                EditText projectName = (EditText) view;
                setsConfigHelper.getRepeatsDatabase().setSetName(projectName.getText().toString(), id);
            }
        }
    };

    View.OnFocusChangeListener newText = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {

            if (!hasFocus) {
                EditText editText = (EditText) view;

                //Getting parent of EditText
                ViewGroup RelativeAddItem = (ViewGroup) view.getParent().getParent();

                String column = "";

                //Getting index of item in root view
                int index = Integer.parseInt(RelativeAddItem.getTag().toString());

                if (RelativeAddItem.findViewById(R.id.questionBox) == view) {
                    column = Values.question;
                }

                String question = editText.getText().toString().trim();

                setsConfigHelper.getRepeatsDatabase().insertValueToSetByID(id, index, column, question);

                editText.setText(question);
            }
        }
    };

    View.OnFocusChangeListener newAnswer = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            EditText editText = (EditText) view;
            String answer = editText.getText().toString();

            if (hasFocus) {
                editText.setTag(answer);
            } else {
                answer = editText.getText().toString().trim();
                ViewGroup RelativeAddItem = (ViewGroup) view.getParent().getParent().getParent();

                if (RelativeAddItem.getTag() == null) {
                    RelativeAddItem = (ViewGroup) view.getParent().getParent();
                }

                int index = Integer.parseInt(RelativeAddItem.getTag().toString());

                String allAnswers = setsConfigHelper.getRepeatsDatabase().getAnswer(id, index);

                String newAnswer = "";

                String oldAnswer = editText.getTag().toString();

                if (allAnswers.contains(oldAnswer) && !oldAnswer.equals("")) {
                    newAnswer = allAnswers.replace(oldAnswer, answer);
                } else if (!allAnswers.equals("")) {
                    newAnswer = allAnswers + RepeatsHelper.breakLine + answer;
                } else {
                    newAnswer = answer;
                }

                setsConfigHelper.getRepeatsDatabase().insertValueToSetByID(id, index, Values.answer, newAnswer);

                editText.setText(answer);
            }
        }
    };

    //endregion
    //region addAnswer
    void addAnswer(View view, String text) {
        View pView = (View) view.getParent();
        final ViewGroup linearQA = pView.findViewById(R.id.LinearQA);
        LayoutInflater inflater = LayoutInflater.from(this);

        View answer = inflater.inflate(R.layout.answerbox, linearQA, false);
        EditText answerBoxPlus = answer.findViewById(R.id.answerBoxPlus);
        ImageButton delAnswer = answer.findViewById(R.id.delAnswer);

        answerBoxPlus.setFilters(new InputFilter[]{inputFilter});
        answerBoxPlus.setText(text);
        answerBoxPlus.setOnFocusChangeListener(newAnswer);

        delAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFocus();
                View p = (View) view.getParent();
                EditText answerBoxPlus = p.findViewById(R.id.answerBoxPlus);
                String answer = answerBoxPlus.getText().toString();

                ViewGroup linear = (ViewGroup) p.getParent();

                ViewGroup RelativeAddItem = (ViewGroup) linear.getParent();
                int index = Integer.parseInt(RelativeAddItem.getTag().toString());

                String allAnswers = setsConfigHelper.getRepeatsDatabase().getAnswer(id, index);

                if (!answer.isEmpty()) {
                    String delAnswer = allAnswers.replace(RepeatsHelper.breakLine + answer, "");
                    setsConfigHelper.getRepeatsDatabase().insertValueToSetByID(id, index, Values.answer, delAnswer);
                }

                if (allAnswers.contains(RepeatsHelper.breakLine + RepeatsHelper.breakLine)) {
                    String delAnswer = allAnswers.replace(RepeatsHelper.breakLine + RepeatsHelper.breakLine, RepeatsHelper.breakLine);
                    setsConfigHelper.getRepeatsDatabase().insertValueToSetByID(id, index, Values.answer, delAnswer);
                }
                linear.removeView(p);
            }
        });

        linearQA.addView(answer);
    }

    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                final InputStream imageStream;
                try {
                    RelativeLayout rel = (RelativeLayout) imagePreview;
                    final ImageView imageView = rel.findViewById(R.id.imageView);

                    imageView.setVisibility(View.VISIBLE);
                    imageStream = getContentResolver().openInputStream(selectedImage);

                    //Creating and resizing image
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(imageStream, null, options);
                    options.inSampleSize = calculateInSampleSize(options, 500, 500);
                    options.inJustDecodeBounds = false;
                    InputStream is = getContentResolver().openInputStream(selectedImage);
                    final Bitmap selected = BitmapFactory.decodeStream(is, null, options);

                    imageView.setImageBitmap(selected);

                    //Unlocking delete image button and locking add image button
                    final ImageButton imgbut = rel.findViewById(R.id.deleteImage);
                    final ImageButton addimg = rel.findViewById(R.id.addImage);
                    addimg.setEnabled(false);
                    imgbut.setVisibility(View.VISIBLE);

                    //Id of the image
                    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss");
                    String imageDate = simpleDate.format(new Date());
                    String ImageName = "I" + imageDate + ".png";
                    imageView.setTag(ImageName);

                    //Saving image
                    try {
                        File control = new File(getFilesDir(), ImageName);
                        boolean bool = control.createNewFile();

                        FileOutputStream out = new FileOutputStream(control);
                        selected.compress(Bitmap.CompressFormat.PNG, 100, out);

                        ViewGroup parent = (ViewGroup) rel.getParent();
                        int index = Integer.parseInt(rel.getTag().toString());

                        setsConfigHelper.getRepeatsDatabase().insertValueToSetByID(id, index, Values.image, ImageName);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();

                    Toast.makeText(this, R.string.imageError, Toast.LENGTH_LONG).show();
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(photoPickerIntent, RequestCodes.PICK_IMAGE);
                }
            }
        }
    }

    private InputFilter inputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            if (charSequence != null && "\\".contains(("" + charSequence))) {
                return "";
            }
            return null;
        }
    };

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    void resetFocus() {
        View current = getCurrentFocus();
        if (current != null) {
            current.clearFocus();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        resetFocus();
        AddEditSetActivity.super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!deleted) {
            resetFocus();
        }

    }
}
