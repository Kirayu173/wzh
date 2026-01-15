package com.wzh.suyuan.feature.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.wzh.suyuan.BuildConfig;
import com.wzh.suyuan.R;
import com.wzh.suyuan.data.auth.AuthManager;
import com.wzh.suyuan.feature.auth.AuthActivity;
import com.wzh.suyuan.network.model.AuthUser;

public class ProfileFragment extends Fragment {

    private TextView userNameView;
    private TextView userIdView;
    private TextView userRoleView;
    private TextView versionView;
    private Button ordersButton;
    private Button addressesButton;
    private Button logoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        userNameView = root.findViewById(R.id.text_user_name);
        userIdView = root.findViewById(R.id.text_user_id);
        userRoleView = root.findViewById(R.id.text_user_role);
        versionView = root.findViewById(R.id.text_version);
        ordersButton = root.findViewById(R.id.button_orders);
        addressesButton = root.findViewById(R.id.button_addresses);
        logoutButton = root.findViewById(R.id.button_logout);
        ordersButton.setOnClickListener(v ->
                startActivity(new Intent(getContext(), com.wzh.suyuan.feature.order.OrderListActivity.class)));
        addressesButton.setOnClickListener(v ->
                startActivity(new Intent(getContext(), com.wzh.suyuan.feature.address.AddressListActivity.class)));
        logoutButton.setOnClickListener(v -> confirmLogout());
        refreshUserInfo();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUserInfo();
    }

    public void refreshUserInfo() {
        if (getContext() == null) {
            return;
        }
        AuthUser user = AuthManager.getUser(getContext());
        if (userNameView != null) {
            String username = user != null ? user.getUsername() : "";
            userNameView.setText(TextUtils.isEmpty(username) ? "未获取" : username);
        }
        if (userIdView != null) {
            if (user != null && user.getId() != null && user.getId() > 0) {
                userIdView.setText(String.valueOf(user.getId()));
            } else {
                userIdView.setText("未获取");
            }
        }
        if (userRoleView != null) {
            String role = user != null ? user.getRole() : "";
            userRoleView.setText(TextUtils.isEmpty(role) ? "未获取" : role);
        }
        if (versionView != null) {
            versionView.setText(BuildConfig.VERSION_NAME);
        }
    }

    private void confirmLogout() {
        if (getContext() == null) {
            return;
        }
        new AlertDialog.Builder(getContext())
                .setTitle("确认退出登录")
                .setMessage("退出后需要重新登录")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", (dialog, which) -> {
                    AuthManager.clearSession(getContext());
                    Intent intent = new Intent(getContext(), AuthActivity.class);
                    startActivity(intent);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                })
                .show();
    }
}
