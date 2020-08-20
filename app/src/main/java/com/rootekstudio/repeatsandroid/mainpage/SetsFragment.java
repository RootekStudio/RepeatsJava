package com.rootekstudio.repeatsandroid.mainpage;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.transition.Hold;
import com.google.android.material.transition.MaterialArcMotion;
import com.google.android.material.transition.MaterialContainerTransform;
import com.google.android.material.transition.MaterialFadeThrough;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.activities.AddEditSetActivity;
import com.rootekstudio.repeatsandroid.community.RepeatsCommunityStartActivity;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.SingleSetInfo;
import com.rootekstudio.repeatsandroid.database.Values;
import com.rootekstudio.repeatsandroid.textrecognition.TextRecognitionActivity;

import java.util.List;

public class SetsFragment extends Fragment {
    private PopupWindow popupWindow;
    private PopupWindow chooseHowTR;
    private RepeatsDatabase DB;

    int width;
    int height;

    public SetsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DB = RepeatsDatabase.getInstance(requireContext());
        List<SingleSetInfo> repeatsList = DB.allSetsInfo(Values.ORDER_BY_ID_DESC);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.mainfragment_sets, null);

        if (repeatsList.size() == 0) {
            RelativeLayout emptyInfo = view.findViewById(R.id.EmptyHereText);
            emptyInfo.setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_main);
        RecyclerView.Adapter adapter = new MainActivityAdapter(repeatsList, getContext(), (AppCompatActivity) getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(fabClick);

        return view;
    }

    private View.OnClickListener fabClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            width = LinearLayout.LayoutParams.MATCH_PARENT;
            height = LinearLayout.LayoutParams.MATCH_PARENT;

            View popupView = LayoutInflater.from(getContext()).inflate(R.layout.add_set_window, null);

            popupView.findViewById(R.id.createNewSet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addEditActivityIntent = new Intent(getContext(), AddEditSetActivity.class);
                    addEditActivityIntent.putExtra("ISEDIT", "FALSE");
                    addEditActivityIntent.putExtra("IGNORE_CHARS", "false");
                    startActivity(addEditActivityIntent);
                    popupWindow.dismiss();
                }
            });

            popupView.findViewById(R.id.getSetFromImage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                    View chooseHowTextRecognition = LayoutInflater.from(getContext()).inflate(R.layout.choose_how_tr, null);
                    chooseHowTextRecognition.findViewById(R.id.takePhoto).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), TextRecognitionActivity.class);
                            intent.putExtra("takePhoto", true);
                            startActivity(intent);
                            chooseHowTR.dismiss();
                        }
                    });

                    chooseHowTextRecognition.findViewById(R.id.chooseFromGallery).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), TextRecognitionActivity.class);
                            intent.putExtra("takePhoto", false);
                            startActivity(intent);
                            chooseHowTR.dismiss();
                        }
                    });

                    chooseHowTextRecognition.findViewById(R.id.closePopupAddSet).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            chooseHowTR.dismiss();
                        }
                    });

                    chooseHowTR = new PopupWindow(chooseHowTextRecognition, width, height, true);
                    chooseHowTR.setAnimationStyle(R.style.animation);
                    chooseHowTR.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
            });

            popupView.findViewById(R.id.getSetFromZip).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent zipPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    zipPickerIntent.setType("application/*");
                    try {
                        getActivity().startActivityForResult(zipPickerIntent, RequestCodes.READ_SHARED);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), R.string.explorerNotFound, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    popupWindow.dismiss();
                }
            });

            popupView.findViewById(R.id.getSetFromCommunity).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentRC = new Intent(getContext(), RepeatsCommunityStartActivity.class);
                    startActivity(intentRC);
                    popupWindow.dismiss();
                }
            });

            popupView.findViewById(R.id.closePopupAddSet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                }
            });

            popupWindow = new PopupWindow(popupView, width, height, true);
            popupWindow.setAnimationStyle(R.style.animation);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    };
}
