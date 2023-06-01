package com.example.project.fragment;

import static com.example.project.api.LoginAPI.checkLogin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.R;
import com.example.project.cache.UserCache;
import com.example.project.model.User;


/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {

    LinearLayout linearLayout1;
    LinearLayout linearLayout2;
    TextView go_to_sign_up;
    Button signIn;
    TextView textUser;
    TextView textPass;
    View view;

    public Login() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        this.init();
        this.addAction();
        return view;
    }
    public void init() {
        linearLayout1 = view.findViewById(R.id.container_login);
        linearLayout2 = view.findViewById(R.id.container_goToSignup);
        textUser = view.findViewById(R.id.editTextText_login);
        textPass = view.findViewById(R.id.editTextTextPassword_login);
        go_to_sign_up = view.findViewById(R.id.go_to_sign_up);
        signIn = view.findViewById(R.id.Sign_In);
    }
    public void addAction() {
        go_to_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register registerFragment = new Register();
                getFragmentManager().beginTransaction().replace(R.id.container, registerFragment).addToBackStack(null).commit();
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = textUser.getText().toString();
                String password = textPass.getText().toString();
                checkLogin(username, password,getContext());
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                String token = UserCache.getToken(getContext());
                Log.e("result", token);
                if (!token.equals("")) {
                    Toast.makeText(getActivity(), "Dang nhap thanh cong", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Tai khoan hoac mat khau khong dung", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment Login.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static Login newInstance(String param1, String param2) {
//        Login fragment = new Login();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

}