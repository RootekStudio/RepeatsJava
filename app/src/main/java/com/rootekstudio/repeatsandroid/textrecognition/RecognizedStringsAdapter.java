package com.rootekstudio.repeatsandroid.textrecognition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.statistics.SetStatsActivityAdapter;

import org.w3c.dom.Text;

import java.util.List;

public class RecognizedStringsAdapter extends RecyclerView.Adapter<RecognizedStringsAdapter.MainViewHolder> {
    List<String> recognizedStrings;

    RecognizedStringsAdapter (List<String> recognizedStrings) {
        this.recognizedStrings = recognizedStrings;
    }

    public List<String> getRecognizedStrings() {
        return recognizedStrings;
    }

    static class MainViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;

        MainViewHolder(LinearLayout linearLayout) {
            super(linearLayout);
            this.linearLayout = linearLayout;
        }
    }

    @Override
    public RecognizedStringsAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_textview, parent, false);

        RecognizedStringsAdapter.MainViewHolder mainViewHolder = new RecognizedStringsAdapter.MainViewHolder(linearLayout);
        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        LinearLayout linearLayout = holder.linearLayout;
        TextView textView = linearLayout.findViewById(R.id.singleTextView);
        String string = recognizedStrings.get(position);
        textView.setText(string);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView selectedText = view.findViewById(R.id.singleTextView);
                LinearLayout parent = (LinearLayout) view.getParent().getParent();

                if(TextRecognitionActivity.selected.equals("0")) {
                    TextInputEditText editText = parent.findViewById(R.id.questionInputTR);
                    String all;
                    String textInEditText = editText.getText().toString();

                    if(!textInEditText.equals("")) {
                        all = textInEditText + " " + selectedText.getText().toString();
                    }
                    else {
                        all = selectedText.getText().toString();
                    }

                    editText.setText(all);

                    recognizedStrings.remove(holder.getLayoutPosition());
                    notifyItemRemoved(holder.getLayoutPosition());
                }
                else if(TextRecognitionActivity.selected.equals("1")) {
                    TextInputEditText editText = parent.findViewById(R.id.answerInputTR);
                    String all;
                    String textInEditText = editText.getText().toString();

                    if(!textInEditText.equals("")) {
                        all = textInEditText + " " + selectedText.getText().toString();
                    }
                    else {
                        all = selectedText.getText().toString();
                    }

                    editText.setText(all);
                    recognizedStrings.remove(holder.getLayoutPosition());
                    notifyItemRemoved(holder.getLayoutPosition());
                }
                else  {
                    LinearLayout textFields = parent.findViewById(R.id.linearTextRecognitionEditTexts);
                    TextInputEditText editText = textFields.getChildAt(Integer.parseInt(TextRecognitionActivity.selected)).findViewById(R.id.answerInputTR);
                    String all;
                    String textInEditText = editText.getText().toString();

                    if(!textInEditText.equals("")) {
                        all = textInEditText + " " + selectedText.getText().toString();
                    }
                    else {
                        all = selectedText.getText().toString();
                    }

                    editText.setText(all);
                    recognizedStrings.remove(holder.getLayoutPosition());
                    notifyItemRemoved(holder.getLayoutPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recognizedStrings.size();
    }
}
