package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AddEditSetActivity extends AppCompatActivity {

    ViewParent imagePreview;
    Context context = null;
    DatabaseHelper DB;
    ViewGroup parent = null;
    int lastIndex = 0;
    static boolean IsDark;

    static String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsDark = RepeatsHelper.DarkTheme(this);

        setContentView(R.layout.activity_repeats_add_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        Intent intent = getIntent();
        final String editing = intent.getStringExtra("ISEDIT");
        String SetName = intent.getStringExtra("NAME");
        String ignore = intent.getStringExtra("IGNORE_CHARS");

        final EditText editName = findViewById(R.id.projectname);
        editName.setOnFocusChangeListener(newName);

        final Activity activity = this;

        BottomAppBar bottomAppBar = findViewById(R.id.AddQuestionBar);
        bottomAppBar.replaceMenu(R.menu.bottomappbar_addset);

        if(!IsDark)
        {
            bottomAppBar.setBackgroundTint(ContextCompat.getColorStateList(this, R.color.DayColorPrimaryDark));
        }

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.deleteButton) {
                    AlertDialog.Builder ALERTbuilder = new AlertDialog.Builder(context);

                    ALERTbuilder.setMessage(R.string.WantDelete);
                    ALERTbuilder.setNegativeButton(R.string.Cancel, null);

                    ALERTbuilder.setPositiveButton(R.string.Delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!editing.equals("FALSE")) {
                                EditSetOperations.DeleteSet(editing, context);

                                List<RepeatsListDB> a = DB.AllItemsLIST();
                                int size = a.size();

                                if (size == 0) {
                                    RepeatsHelper.CancelNotifications(context);
                                }
                            }

                            AddEditSetActivity.super.onBackPressed();
                        }
                    });
                    ALERTbuilder.show();
                } else if (item.getItemId() == R.id.ignoreCharsItem) {
                    if (item.isChecked()) {
                        DB.ignoreChars(id, "false");
                        item.setChecked(false);
                    } else {
                        DB.ignoreChars(id, "true");
                        item.setChecked(true);
                    }
                } else if (item.getItemId() == R.id.share) {

                    View current = getCurrentFocus();
                    if(current != null) {
                        current.clearFocus();
                    }
                    ShareButton.ShareClick(context, editName.getText().toString(), id, activity);
                }
                return false;
            }
        });

        if (ignore.equals("true")) {
            bottomAppBar.getMenu().findItem(R.id.ignoreCharsItem).setChecked(true);
        }

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");

        DB = new DatabaseHelper(context);

        parent = findViewById(R.id.AddRepeatsLinear);

        if (editing.equals("FALSE")) {
            String date = s.format(new Date());
            id = "R" + date;

            SimpleDateFormat createD = new SimpleDateFormat("dd.MM.yyyy");
            String createDate = createD.format(new Date());

            RepeatsListDB list = new RepeatsListDB("", id, createDate, "true", "", "false");
            DB.CreateSet(id);
            DB.AddName(list);

            addView(parent, context);
            DB.AddItem(id);
        } else {
            id = editing;
            readSetFromDatabase(editing, SetName);
        }

        FloatingActionButton fab = findViewById(R.id.AddQuestionFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addView(parent, context);
                DB.AddItem(id);
            }
        });
    }

    void readSetFromDatabase(final String SetID, String NAME) {
        final LayoutInflater inflater = LayoutInflater.from(this);
        EditText editName = findViewById(R.id.projectname);
        editName.setText(NAME);

        Thread readFromDatabase = new Thread(new Runnable() {
            @Override
            public void run() {
                List<RepeatsSingleSetDB> SET = DB.AllItemsSET(SetID);
                int ItemsCount = SET.size();

                for (int i = 0; i < ItemsCount; i++) {
                    RepeatsSingleSetDB Single = SET.get(i);
                    int ID = Single.getID();
                    String Question = Single.getQuestion();
                    String Answer = Single.getAnswer();
                    String Image = Single.getImag();

                    final View child = inflater.inflate(R.layout.addrepeatslistitem, parent, false);

                    RelativeLayout RL = child.findViewById(R.id.RelativeAddItem);
                    RL.setTag(ID);

                    if(IsDark)
                    {
                        RL.setBackgroundResource(R.drawable.layout_mainshape_dark);
                    }

                    EditText Q = child.findViewById(R.id.questionBox);
                    EditText A = child.findViewById(R.id.answerBox);
                    ImageButton B = child.findViewById(R.id.deleteItem);
                    final ImageButton I = child.findViewById(R.id.addImage);
                    ImageView img = child.findViewById(R.id.imageView);
                    ImageButton imgbut = child.findViewById(R.id.deleteImage);
                    ImageButton addAnswer = child.findViewById(R.id.addAnswerButton);


                    Q.setOnFocusChangeListener(newText);
                    A.setOnFocusChangeListener(newText);
                    B.setOnClickListener(deleteClick);
                    I.setOnClickListener(addImageClick);
                    imgbut.setOnClickListener(deleteImageClick);
                    addAnswer.setOnClickListener(addAnswerClick);

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
                    Q.setText(Question);
                    A.setText(Answer);

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
        answer.setOnFocusChangeListener(newText);

        if(IsDark)
        {
            rel.setBackgroundResource(R.drawable.layout_mainshape_dark);
        }

        //Adding item to root view
        viewGroup.addView(item);
    }

    View.OnFocusChangeListener newName = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus) {
                EditText projectname = (EditText) view;
                DB.setTableName(projectname.getText().toString(), id);
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

                //Checking if user has entered the question or the answer
                if (RelativeAddItem.findViewById(R.id.questionBox) == view) {
                    column = "question";
                } else if (RelativeAddItem.findViewById(R.id.answerBox) == view) {
                    column = "answer";
                }

                //Getting index of item in root view
                ViewGroup item = (ViewGroup) RelativeAddItem.getParent();
                int index = item.indexOfChild(RelativeAddItem);
                index++;


                DB.InsertValue(id, index, column, editText.getText().toString());
            }
        }
    };

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

                DB.deleteImage(id, imageName);
            }

            if (rootView.getChildCount() != 1) {
                rootView.removeView(parent);
                DB.deleteItem(id, index);
            }
        }
    };

    private View.OnClickListener addImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            imagePreview = view.getParent();
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(photoPickerIntent, 1);
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
                    DB.deleteImage(id, imageName);
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
            View pView = (View) view.getParent();
            final ViewGroup linearQA = pView.findViewById(R.id.LinearQA);
            LayoutInflater inflater = LayoutInflater.from(context);
            View answer = inflater.inflate(R.layout.answerbox, linearQA, false);
            ImageButton delAnswer = answer.findViewById(R.id.delAnswer);

            delAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View p = (View) view.getParent();

                    ViewGroup linear = (ViewGroup) p.getParent();
                    linear.removeView(p);
                }
            });

            linearQA.addView(answer);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
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
                    options.inSampleSize = RepeatsHelper.calculateInSampleSize(options, 500, 500);
                    options.inJustDecodeBounds = false;
                    InputStream is = getContentResolver().openInputStream(selectedImage);
                    final Bitmap selected = BitmapFactory.decodeStream(is, null, options);

                    imageView.setImageBitmap(selected);

                    final ImageButton imgbut = rel.findViewById(R.id.deleteImage);
                    final ImageButton addimg = rel.findViewById(R.id.addImage);
                    addimg.setEnabled(false);
                    imgbut.setVisibility(View.VISIBLE);

                    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss");
                    String imageDate = simpleDate.format(new Date());
                    String ImageName = "I" + imageDate + ".png";
                    imageView.setTag(ImageName);

                    try {
                        File control = new File(context.getFilesDir(), ImageName);
                        boolean bool = control.createNewFile();

                        FileOutputStream out = new FileOutputStream(control);
                        selected.compress(Bitmap.CompressFormat.PNG, 100, out);

                        ViewGroup parent = (ViewGroup) rel.getParent();
                        int index = Integer.parseInt(rel.getTag().toString());

                        DB.InsertValue(id, index, "image", ImageName);

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
                    startActivityForResult(photoPickerIntent, 1);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        View current = getCurrentFocus();
        if(current != null) {
            current.clearFocus();
        }

        AddEditSetActivity.super.onBackPressed();
    }
}
