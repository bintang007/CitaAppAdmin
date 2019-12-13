package com.cita.myapplicationadmin.ui.child;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cita.myapplicationadmin.LoginActivity;
import com.cita.myapplicationadmin.R;
import com.cita.myapplicationadmin.adapter.ChildAdapter;
import com.cita.myapplicationadmin.app.AppController;
import com.cita.myapplicationadmin.model.Child;
import com.cita.myapplicationadmin.utils.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ChildFragment extends Fragment {

    private ProgressDialog progressDialog;

    private SharedPreferences sharedPreferences;
    private final static String TAG_ADMIN_ID = "admin_id", TAG = ChildFragment.class.getSimpleName();
    private static final String URL = Server.URL + "admin/read_child.php", TAG_SUCCESS = "success",
            TAG_CHILD_NAME = "child_name", TAG_DATE_OF_BIRTH = "date_of_birth", TAG_GENDER = "gender",
            TAG_MESSAGE = "message", TAG_JSON_OBJ = "json_obj_req", TAG_CHILD_ID = "child_id";
    private ArrayList<Child> childArrayList;
    private int adminId;
    private TextView tvEmptyChild;
    private ChildAdapter childAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_child, container, false);
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(LoginActivity.MY_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        adminId = sharedPreferences.getInt(TAG_ADMIN_ID, 0);
        tvEmptyChild = root.findViewById(R.id.tv_empty_child);

        getAllChild();

        RecyclerView rvChild = root.findViewById(R.id.rv_childs);
        childAdapter = new ChildAdapter(childArrayList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvChild.setLayoutManager(layoutManager);
        rvChild.setAdapter(childAdapter);

        return root;
    }

    private void getAllChild() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Tunggu sebentar ...");
        showProgressDialog();

        childArrayList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.e(TAG, "Read response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt(TAG_SUCCESS);
                            if (success == 1) {
                                JSONArray jsonArrayChildId = jsonObject.getJSONArray(TAG_CHILD_ID);
                                JSONArray jsonArrayChildName = jsonObject.getJSONArray(TAG_CHILD_NAME);
                                JSONArray jsonArrayGender = jsonObject.getJSONArray(TAG_GENDER);
                                JSONArray jsonArrayDateOfBirth = jsonObject.getJSONArray(TAG_DATE_OF_BIRTH);
                                for (int i = 0; i < jsonArrayChildName.length(); i++) {
                                    Child child = new Child();
                                    child.setChildId(jsonArrayChildId.getInt(i));
                                    child.setChildName(jsonArrayChildName.getString(i));
                                    child.setGender(jsonArrayGender.getString(i));
                                    child.setDateOfBirth(jsonArrayDateOfBirth.getString(i));
                                    childArrayList.add(child);
                                    childAdapter.setItems(childArrayList);
                                }
                            } else {
                                tvEmptyChild.setText(jsonObject.getString(TAG_MESSAGE));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
    }

    private void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

}
