package com.example.todo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.todo.R;
import com.example.todo.Bean.TodoItem;

import java.util.List;

public class TodoListAdapter extends BaseAdapter {

    private Context context;
    private List<TodoItem> itemList;
    private int selectedItemId = -1;

    public TodoListAdapter(Context context, List<TodoItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setSelectedItemId(int selectedItemId) {
        this.selectedItemId = selectedItemId;
        notifyDataSetChanged();
    }

    public int getSelectedItemId() {
        return selectedItemId;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_todo, parent, false);
        }

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvDescription = convertView.findViewById(R.id.tvDescription);

        TodoItem item = itemList.get(position);
        tvTitle.setText(item.getTitle());
        tvDescription.setText(item.getDescription());

        if (item.getId() == selectedItemId) {
            convertView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        return convertView;
    }
}
