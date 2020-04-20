package com.farms.lakesyde;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    TextView tvUsername, tvPhone, tvEmail, tvLocation, tvSize, tvCrops, tvName;
    private PrefManager prefManager;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefManager = new PrefManager(getActivity());
        tvUsername = view.findViewById(R.id.tvUsername);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvSize = view.findViewById(R.id.size);
        tvCrops = view.findViewById(R.id.tvCrops);
        tvName = view.findViewById(R.id.tvName);


        if (TextUtils.isEmpty(prefManager.getCrops())) {

        } else {
            tvUsername.setText(prefManager.getUserName());
            tvPhone.setText(prefManager.getUserPhone());
            tvEmail.setText(prefManager.getUserEmail());
            tvLocation.setText(prefManager.getUserLocation());
            tvSize.setText(prefManager.getFarmSize());
            tvCrops.setText(prefManager.getCrops());
            tvName.setText(prefManager.getFarm());

        }
    }
}
