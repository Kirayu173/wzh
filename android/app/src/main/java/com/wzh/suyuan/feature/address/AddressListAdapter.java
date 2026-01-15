package com.wzh.suyuan.feature.address;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wzh.suyuan.R;
import com.wzh.suyuan.network.model.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.AddressViewHolder> {

    interface AddressActionListener {
        void onSetDefault(Address address);

        void onEdit(Address address);

        void onDelete(Address address);
    }

    private final List<Address> items = new ArrayList<>();
    private AddressActionListener listener;

    void setAddressActionListener(AddressActionListener listener) {
        this.listener = listener;
    }

    void setItems(List<Address> addresses) {
        items.clear();
        if (addresses != null) {
            items.addAll(addresses);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        private final TextView receiverView;
        private final TextView phoneView;
        private final TextView detailView;
        private final TextView defaultTag;
        private final Button setDefaultButton;
        private final Button editButton;
        private final Button deleteButton;

        AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverView = itemView.findViewById(R.id.address_receiver);
            phoneView = itemView.findViewById(R.id.address_phone);
            detailView = itemView.findViewById(R.id.address_detail);
            defaultTag = itemView.findViewById(R.id.address_default_tag);
            setDefaultButton = itemView.findViewById(R.id.address_set_default);
            editButton = itemView.findViewById(R.id.address_edit);
            deleteButton = itemView.findViewById(R.id.address_delete);
        }

        void bind(Address address, AddressActionListener listener) {
            if (address == null) {
                return;
            }
            receiverView.setText(address.getReceiver() == null ? "" : address.getReceiver());
            phoneView.setText(address.getPhone() == null ? "" : address.getPhone());
            detailView.setText(buildAddress(address));
            boolean isDefault = Boolean.TRUE.equals(address.getIsDefault());
            defaultTag.setVisibility(isDefault ? View.VISIBLE : View.GONE);
            setDefaultButton.setVisibility(isDefault ? View.GONE : View.VISIBLE);
            setDefaultButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSetDefault(address);
                }
            });
            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(address);
                }
            });
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(address);
                }
            });
        }

        private String buildAddress(Address address) {
            StringBuilder sb = new StringBuilder();
            if (address.getProvince() != null && !address.getProvince().isEmpty()) {
                sb.append(address.getProvince());
            }
            if (address.getCity() != null && !address.getCity().isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(address.getCity());
            }
            if (address.getDetail() != null && !address.getDetail().isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(address.getDetail());
            }
            return sb.toString();
        }
    }
}
