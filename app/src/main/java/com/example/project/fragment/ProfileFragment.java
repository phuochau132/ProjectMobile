package com.example.project.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.R;
import com.example.project.adapter.AdapterProfile;
import com.example.project.api.SongAPI;
import com.example.project.cache.UserCache;
import com.example.project.event.CallbackAPI;
import com.example.project.model.Subject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final int FILE_REQUEST_CODE = 1;

    private Button btnList;
    private Button btnLogout, btn_swap;
    private TextView tvUser;

    private String selectedFilePath;
    private OkHttpClient client;
    private SharedPreferences sharedPreferences;
    private String accessToken;
    private UserCache ucache;
    private RecyclerView rcvSong;
    private AdapterProfile adapter;
    private List<Subject> dataList;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btn_swap = view.findViewById(R.id.btn_swap);
        btnLogout = view.findViewById(R.id.btn_logout);
        tvUser = view.findViewById(R.id.tv_username);
        rcvSong = view.findViewById(R.id.rcv_so);
        dataList = new ArrayList<>();
        adapter = new AdapterProfile(dataList);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        rcvSong.setLayoutManager(linearLayout);
        rcvSong.setAdapter(adapter);
        btnList = view.findViewById(R.id.btn_list);
        btnList.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                // Hiển thị RecyclerView
                rcvSong.setVisibility(View.VISIBLE);

                // Gửi yêu cầu và lấy dữ liệu từ API để cập nhật RecyclerView
                // (theo ví dụ đã được trình bày trước đó)
                getListSong();
            }
        });

        view.findViewById(R.id.btn_manager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SongAPI.getSongByUsername(UserCache.getUser(view.getContext()), new CallbackAPI() {
                        @Override
                        public <T> void callback(T data) {
                            ArrayList<Subject> subjects = (ArrayList<Subject>) data;
                            ManagerFragment managerFragment = new ManagerFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("subjects", subjects);
                            managerFragment.setArguments(bundle);
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container, managerFragment).addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        client = new OkHttpClient();
        ucache = new UserCache();


        fetchUsername();

        btn_swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swap();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


        return view;
    }

    private void getListSong() {
        accessToken = ucache.getToken(getContext());
        OkHttpClient client = new OkHttpClient();
        Request request1 = new Request.Builder()
                .url("https://backend-clone-zing-mp3.vercel.app/auth/favourite")
                .header("Authorization", "Bearer " + accessToken) // accessToken1 là access token của API 1
                .build();

        client.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Xử lý lỗi khi gặp vấn đề trong quá trình gửi yêu cầu
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Đọc dữ liệu phản hồi từ API 1
                    String responseData = response.body().string();

                    try {
                        // Chuyển đổi dữ liệu JSON thành đối tượng JSONObject
                        JSONObject jsonObject = new JSONObject(responseData);

                        // Lấy idSong từ đối tượng JSONObject
                        String idSong = jsonObject.getString("idSong");

                        // Gửi yêu cầu đến API 2 để lấy thông tin bài hát
                        Request request2 = new Request.Builder()
                                .url("https://backend-clone-zing-mp3.vercel.app/api/search?encodeID=" + idSong)

                                .build();

                        client.newCall(request2).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                // Xử lý lỗi khi gặp vấn đề trong quá trình gửi yêu cầu
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    // Đọc dữ liệu phản hồi từ API 2
                                    String responseData = response.body().string();

                                    try {
                                        // Chuyển đổi dữ liệu JSON thành đối tượng JSONObject
                                        JSONObject jsonObject = new JSONObject(responseData);

                                        // Lấy thông tin tên bài hát và ca sĩ từ đối tượng JSONObject
                                        String name = jsonObject.getString("name");
                                        String artist = jsonObject.getString("artist");


                                        dataList.add(new Subject(idSong, name, artist));

                                        // Cập nhật RecyclerView trên luồng giao diện chính
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        // Xử lý lỗi khi có vấn đề xảy ra trong quá trình xử lý dữ liệu JSON
                                    }
                                } else {
                                    // Xử lý lỗi khi nhận phản hồi không thành công từ API 2
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Xử lý lỗi khi có vấn đề xảy ra trong quá trình xử lý dữ liệu JSON
                    }
                } else {
                    // Xử lý lỗi khi nhận phản hồi không thành công từ API 1
                }
            }
        });
    }

    private void logout() {
       ucache.clearToken(getContext());;

        // Hiển thị thông báo đăng xuất thành công
        Toast.makeText(getActivity(), "Đăng xuất thành công.", Toast.LENGTH_SHORT).show();
        // Chuyển sang Fragment hoặc
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new HomeFragment());
        fragmentTransaction.commit();
    }

    private void swap() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Thay thế fragment hiện tại bằng fragment mới
        fragmentTransaction.replace(R.id.container, new UploadFragment()).addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void fetchUsername() {
        // Tạo request để gửi đến API với access token
        accessToken = ucache.getToken(getContext());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://backend-clone-zing-mp3.vercel.app/auth/favourite")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        // Thực hiện request bất đồng bộ
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Xử lý khi có lỗi xảy ra
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Xử lý kết quả trả về từ API
                if (response.isSuccessful()) {
                    // Lấy dữ liệu username từ response body
                    final String username = response.body().string();

                    // Cập nhật UI trong luồng giao diện (main thread)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Gắn dữ liệu username vào TextView
                            tvUser.setText(username);
                        }
                    });
                }
            }
        });
    }


}