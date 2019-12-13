package com.cita.myapplicationadmin.ui.nutrient;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cita.myapplicationadmin.LoginActivity;
import com.cita.myapplicationadmin.R;
import com.cita.myapplicationadmin.app.AppController;
import com.cita.myapplicationadmin.utils.Server;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowNutrientFragment extends Fragment {
    private TextView tvNutrientName, tvCarbohydrate, tvCalories, tvFat, tvProtein;

    private static final String TAG = ShowNutrientFragment.class.getSimpleName(),
            TAG_JSON_OBJ = "json_obj_req", URL = Server.URL + "admin/show_nutrient.php", TAG_SUCCESS = "success",
            TAG_NUTRIENT_NAME = "nutrient_name", TAG_CARBOHYDRATE = "carbohydrate", TAG_CALORIES = "calories",
            TAG_FAT = "fat", TAG_PROTEIN = "protein",
            TAG_MESSAGE = "message", TAG_NUTRIENT_ID = "nutrient_id";

    private int nutrientId;

    public ShowNutrientFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_show_nutrient, container, false);
        nutrientId = ShowNutrientFragmentArgs.fromBundle(getArguments()).getNutrientId();
        MaterialCardView mcvNutrientName = root.findViewById(R.id.mcv_nutrient_name);
        MaterialCardView mcvCarbohydrate = root.findViewById(R.id.mcv_carbohydrate);
        MaterialCardView mcvCalories = root.findViewById(R.id.mcv_calories);
        MaterialCardView mcvFat = root.findViewById(R.id.mcv_fat);
        MaterialCardView mcvProtein = root.findViewById(R.id.mcv_protein);

        tvNutrientName = root.findViewById(R.id.tv_nutrient_name);
        tvCarbohydrate = root.findViewById(R.id.tv_carbohydrate);
        tvCalories = root.findViewById(R.id.tv_calories);
        tvFat = root.findViewById(R.id.tv_fat);
        tvProtein = root.findViewById(R.id.tv_protein);


        showNutrient();

        MaterialButton btnDelete = root.findViewById(R.id.btn_delete);
        MaterialButton btnBack = root.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destroyNutrient();
            }
        });

        mcvNutrientName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                final EditText input = new EditText(getActivity());
                FrameLayout container = new FrameLayout(getActivity());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.
                        LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                input.setSingleLine();
                input.setLayoutParams(params);
                input.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                input.setText(tvNutrientName.getText().toString());
                container.addView(input);
                alert.setTitle("Nama Kandungan Gizi");
                alert.setView(container);

                alert.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String nutrientName = input.getText().toString();
                        if (!nutrientName.equals(tvNutrientName.getText().toString())) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                    Server.URL + "admin/update_nutrient_nutrient_name.php",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.e(TAG, "Update response: " + response);
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                Toast.makeText(getActivity(),
                                                        jsonObject.getString(LoginActivity.TAG_MESSAGE),
                                                        Toast.LENGTH_SHORT).show();
                                                showNutrient();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put(TAG_NUTRIENT_NAME, nutrientName);
                                    params.put(TAG_NUTRIENT_ID, String.valueOf(nutrientId));
                                    return params;
                                }
                            };
                            AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
                        }

                    }
                });
                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        View view = getActivity().getCurrentFocus();
                        if (view == null) {
                            view = new View(getActivity());
                        }
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                });
                alert.show();
            }
        });
        mcvCarbohydrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                final EditText input = new EditText(getActivity());
                FrameLayout container = new FrameLayout(getActivity());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.
                        LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                input.setSingleLine();
                input.setLayoutParams(params);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText(tvCarbohydrate.getText().toString());
                container.addView(input);
                alert.setTitle("Karbohidrat");
                alert.setView(container);

                alert.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String carbohydrate = input.getText().toString();
                        if (!carbohydrate.equals(tvCarbohydrate.getText().toString())) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                    Server.URL + "admin/update_nutrient_carbohydrate.php",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.e(TAG, "Update response: " + response);
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                Toast.makeText(getActivity(),
                                                        jsonObject.getString(LoginActivity.TAG_MESSAGE),
                                                        Toast.LENGTH_SHORT).show();
                                                showNutrient();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put(TAG_CARBOHYDRATE, carbohydrate);
                                    params.put(TAG_NUTRIENT_ID, String.valueOf(nutrientId));
                                    return params;
                                }
                            };
                            AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
                        }

                    }
                });
                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        View view = getActivity().getCurrentFocus();
                        if (view == null) {
                            view = new View(getActivity());
                        }
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                });
                alert.show();
            }
        });
        mcvCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                final EditText input = new EditText(getActivity());
                FrameLayout container = new FrameLayout(getActivity());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.
                        LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                input.setSingleLine();
                input.setLayoutParams(params);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText(tvCalories.getText().toString());
                container.addView(input);
                alert.setTitle("Kalori");
                alert.setView(container);

                alert.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String calories = input.getText().toString();
                        if (!calories.equals(tvCalories.getText().toString())) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                    Server.URL + "admin/update_nutrient_calories.php",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.e(TAG, "Update response: " + response);
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                Toast.makeText(getActivity(),
                                                        jsonObject.getString(LoginActivity.TAG_MESSAGE),
                                                        Toast.LENGTH_SHORT).show();
                                                showNutrient();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put(TAG_CALORIES, calories);
                                    params.put(TAG_NUTRIENT_ID, String.valueOf(nutrientId));
                                    return params;
                                }
                            };
                            AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
                        }

                    }
                });
                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        View view = getActivity().getCurrentFocus();
                        if (view == null) {
                            view = new View(getActivity());
                        }
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                });
                alert.show();
            }
        });
        mcvFat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                final EditText input = new EditText(getActivity());
                FrameLayout container = new FrameLayout(getActivity());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.
                        LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                input.setSingleLine();
                input.setLayoutParams(params);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText(tvNutrientName.getText().toString());
                container.addView(input);
                alert.setTitle("Lemak");
                alert.setView(container);

                alert.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String fat = input.getText().toString();
                        if (!fat.equals(tvFat.getText().toString())) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                    Server.URL + "admin/update_nutrient_fat.php",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.e(TAG, "Update response: " + response);
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                Toast.makeText(getActivity(),
                                                        jsonObject.getString(LoginActivity.TAG_MESSAGE),
                                                        Toast.LENGTH_SHORT).show();
                                                showNutrient();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put(TAG_FAT, fat);
                                    params.put(TAG_NUTRIENT_ID, String.valueOf(nutrientId));
                                    return params;
                                }
                            };
                            AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
                        }

                    }
                });
                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        View view = getActivity().getCurrentFocus();
                        if (view == null) {
                            view = new View(getActivity());
                        }
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                });
                alert.show();
            }
        });
        mcvProtein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                final EditText input = new EditText(getActivity());
                FrameLayout container = new FrameLayout(getActivity());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.
                        LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                input.setSingleLine();
                input.setLayoutParams(params);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText(tvProtein.getText().toString());
                container.addView(input);
                alert.setTitle("Protein");
                alert.setView(container);

                alert.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String protein = input.getText().toString();
                        if (!protein.equals(tvProtein.getText().toString())) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                    Server.URL + "admin/update_nutrient_protein.php",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.e(TAG, "Update response: " + response);
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                Toast.makeText(getActivity(),
                                                        jsonObject.getString(LoginActivity.TAG_MESSAGE),
                                                        Toast.LENGTH_SHORT).show();
                                                showNutrient();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put(TAG_PROTEIN, protein);
                                    params.put(TAG_NUTRIENT_ID, String.valueOf(nutrientId));
                                    return params;
                                }
                            };
                            AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
                        }

                    }
                });
                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        View view = getActivity().getCurrentFocus();
                        if (view == null) {
                            view = new View(getActivity());
                        }
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                });
                alert.show();
            }
        });
        return root;
    }

    private void showNutrient() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Show response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            tvNutrientName.setText(jsonObject.getString(TAG_NUTRIENT_NAME));
                            tvCarbohydrate.setText(jsonObject.getString(TAG_CARBOHYDRATE));
                            tvCalories.setText(jsonObject.getString(TAG_CALORIES));
                            tvFat.setText(jsonObject.getString(TAG_FAT));
                            tvProtein.setText(jsonObject.getString(TAG_PROTEIN));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(TAG_NUTRIENT_ID, String.valueOf(nutrientId));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());

    }

    private void destroyNutrient() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage("Apakah anda yakin?");
        builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Server.URL + "admin/destroy_nutrient.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e(TAG, "Destroy response: " + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getActivity(), jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                                    Objects.requireNonNull(getActivity()).onBackPressed();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put(TAG_NUTRIENT_ID, String.valueOf(nutrientId));
                        return params;
                    }
                };
                AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
            }
        });
        builder.setNegativeButton("batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();


    }

}
