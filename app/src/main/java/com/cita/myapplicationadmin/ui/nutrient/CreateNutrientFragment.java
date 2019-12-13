package com.cita.myapplicationadmin.ui.nutrient;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cita.myapplicationadmin.R;
import com.cita.myapplicationadmin.app.AppController;
import com.cita.myapplicationadmin.utils.Server;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateNutrientFragment extends Fragment {

    private ProgressDialog progressDialog;

    private TextInputLayout tilNutrientName, tilCarbohydrate, tilCalories, tilFat, tilProtein;
    private TextInputEditText tietNutrientName, tietCarbohydrate, tietCalories, tietFat, tietProtein;

    private final static String URL = Server.URL + "admin/store_nutrient.php", TAG = CreateNutrientFragment
            .class.getSimpleName(), TAG_MESSAGE = "message", TAG_JSON_OBJ = "json_obj_req",
            TAG_ADMIN_ID = "admin_id", TAG_NUTRIENT_NAME = "nutrient_name", TAG_CARBOHYDRATE = "carbohydrate",
            TAG_CALORIES = "calories", TAG_FAT = "fat", TAG_PROTEIN = "protein";

    private static int adminId;

    public CreateNutrientFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_create_nutrient, container, false);

        assert getArguments() != null;
        adminId = CreateNutrientFragmentArgs.fromBundle(getArguments()).getAdminId();

        tilNutrientName = root.findViewById(R.id.til_nutrient_name);
        tilCarbohydrate = root.findViewById(R.id.til_carbohydrate);
        tilCalories = root.findViewById(R.id.til_calories);
        tilFat = root.findViewById(R.id.til_fat);
        tilProtein = root.findViewById(R.id.til_protein);

        tietNutrientName = root.findViewById(R.id.tiet_nutrient_name);
        tietCarbohydrate = root.findViewById(R.id.tiet_carbohydrate);
        tietCalories = root.findViewById(R.id.tiet_calories);
        tietFat = root.findViewById(R.id.tiet_fat);
        tietProtein = root.findViewById(R.id.tiet_protein);

        tietCarbohydrate.setInputType(InputType.TYPE_CLASS_NUMBER);
        tietCalories.setInputType(InputType.TYPE_CLASS_NUMBER);
        tietFat.setInputType(InputType.TYPE_CLASS_NUMBER);
        tietProtein.setInputType(InputType.TYPE_CLASS_NUMBER);

        MaterialButton btnCancel = root.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        MaterialButton btnNext = root.findViewById(R.id.btn_next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.requireNonNull(tietNutrientName.getText()).toString().isEmpty()) {
                    tilNutrientName.setError("Kolom tidak boleh kosong");
                } else {
                    tilNutrientName.setError(null);
                }
                if (Objects.requireNonNull(tietCarbohydrate.getText()).toString().isEmpty()) {
                    tilCarbohydrate.setError("Kolom tidak boleh kosong");
                } else {
                    tilCarbohydrate.setError(null);
                }
                if (Objects.requireNonNull(tietCalories.getText()).toString().isEmpty()) {
                    tilCalories.setError("Kolom tidak boleh kosong");
                } else {
                    tilCalories.setError(null);
                }
                if (Objects.requireNonNull(tietFat.getText()).toString().isEmpty()) {
                    tilFat.setError("Kolom tidak boleh kosong");
                } else {
                    tilFat.setError(null);
                }
                if (Objects.requireNonNull(tietProtein.getText()).toString().isEmpty()) {
                    tilProtein.setError("Kolom tidak boleh kosong");
                } else {
                    tilProtein.setError(null);
                }

                textChangedListener(tietNutrientName, tilNutrientName);
                textChangedListener(tietCalories, tilCalories);
                textChangedListener(tietCarbohydrate, tilCarbohydrate);
                textChangedListener(tietFat, tilFat);
                textChangedListener(tietProtein, tilProtein);

                String nutrientName = tietNutrientName.getText().toString();
                String carbohydrate = tietCarbohydrate.getText().toString();
                String calories = tietCalories.getText().toString();
                String fat = tietFat.getText().toString();
                String protein = tietProtein.getText().toString();

                if (nutrientName.trim().length() > 0 && carbohydrate.trim().length() > 0
                        && calories.trim().length() > 0 && fat.trim().length() > 0
                        && protein.trim().length() > 0) {
                    store(nutrientName, carbohydrate, calories, fat, protein);
                }

            }
        });

        return root;
    }

    private void store(final String nutrientName, final String carbohydrate, final String calories,
                       final String fat, final String protein) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Memproses ...");
        showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Store Response: " + response);
                        hideProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getActivity(),
                                    jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                            Objects.requireNonNull(getActivity()).onBackPressed();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Store Error: " + error.getMessage());
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(TAG_ADMIN_ID, String.valueOf(adminId));
                params.put(TAG_NUTRIENT_NAME, nutrientName);
                params.put(TAG_CARBOHYDRATE, carbohydrate);
                params.put(TAG_CALORIES, calories);
                params.put(TAG_FAT, fat);
                params.put(TAG_PROTEIN, protein);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
    }

    private void textChangedListener(TextInputEditText textInputEditText, final TextInputLayout textInputLayout) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateEditText(editable, textInputLayout);
            }
        });

        textInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText((((EditText) view).getText()), textInputLayout);
                }
            }
        });
    }

    private void validateEditText(Editable editable, TextInputLayout textInputLayout) {
        if (TextUtils.isEmpty((editable))) {
            textInputLayout.setError("Kolom tidak boleh kosong");
        } else {
            textInputLayout.setError(null);
        }
    }

    private void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
