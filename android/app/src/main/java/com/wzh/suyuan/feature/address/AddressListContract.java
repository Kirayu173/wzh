package com.wzh.suyuan.feature.address;

import java.util.List;

import com.wzh.suyuan.network.model.Address;
import com.wzh.suyuan.ui.mvp.BaseView;

public interface AddressListContract {
    interface View extends BaseView {
        void showLoading(boolean loading);

        void showAddresses(List<Address> addresses);

        void showEmpty(String message);
    }
}
