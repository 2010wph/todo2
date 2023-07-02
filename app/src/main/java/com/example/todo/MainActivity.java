package com.example.todo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.Adapter.TodoListAdapter;
import com.example.todo.Bean.TodoItem;
import com.example.todo.DatabaseHelper.TodoDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Button btnAdd, btnUpdate, btnDelete;
    private ListView listView;
    private TodoListAdapter adapter;

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    private List<TodoItem> todoItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTitle = findViewById(R.id.etTitle); // 标题输入框
        etDescription = findViewById(R.id.etDescription); // 描述输入框
        btnAdd = findViewById(R.id.btnAdd); // 添加按钮
        btnUpdate = findViewById(R.id.btnUpdate); // 更新按钮
        btnDelete = findViewById(R.id.btnDelete); // 删除按钮
        listView = findViewById(R.id.listView); // 列表视图

        dbHelper = new TodoDatabaseHelper(this); // 创建数据库帮助类实例
        db = dbHelper.getWritableDatabase(); // 获取可写的数据库对象

        todoItemList = new ArrayList<>(); // 初始化TODO项列表
        adapter = new TodoListAdapter(this, todoItemList); // 创建适配器
        listView.setAdapter(adapter); // 设置适配器

        showAllTodoItems(); // 显示所有的TODO项

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String description = etDescription.getText().toString();
                addTodoItem(title, description);
                clearInputFields();
                showAllTodoItems();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedItemId = adapter.getSelectedItemId();
                if (selectedItemId != -1) {
                    String title = etTitle.getText().toString();
                    String description = etDescription.getText().toString();
                    updateTodoItem(selectedItemId, title, description);
                    clearInputFields();
                    showAllTodoItems();
                } else {
                    Toast.makeText(MainActivity.this, "请先选择要更新的TODO项", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedItemId = adapter.getSelectedItemId();
                if (selectedItemId != -1) {
                    deleteTodoItem(selectedItemId);
                    clearInputFields();
                    showAllTodoItems();
                } else {
                    Toast.makeText(MainActivity.this, "请先选择要删除的TODO项", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TodoItem item = todoItemList.get(position);
                etTitle.setText(item.getTitle());
                etDescription.setText(item.getDescription());
                adapter.setSelectedItemId(item.getId());
            }
        });
    }

    private void addTodoItem(String title, String description) {
        ContentValues values = new ContentValues();
        values.put(TodoDatabaseHelper.COLUMN_TITLE, title);
        values.put(TodoDatabaseHelper.COLUMN_DESCRIPTION, description);
        db.insert(TodoDatabaseHelper.TABLE_NAME, null, values);
        Toast.makeText(this, "TODO项添加成功", Toast.LENGTH_SHORT).show();
    }

    private void updateTodoItem(int id, String title, String description) {
        ContentValues values = new ContentValues();
        values.put(TodoDatabaseHelper.COLUMN_TITLE, title);
        values.put(TodoDatabaseHelper.COLUMN_DESCRIPTION, description);
        String selection = TodoDatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.update(TodoDatabaseHelper.TABLE_NAME, values, selection, selectionArgs);
        Toast.makeText(this, "TODO项更新成功", Toast.LENGTH_SHORT).show();
    }

    private void deleteTodoItem(int id) {
        String selection = TodoDatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(TodoDatabaseHelper.TABLE_NAME, selection, selectionArgs);
        Toast.makeText(this, "TODO项删除成功", Toast.LENGTH_SHORT).show();
    }

    private void showAllTodoItems() {
        todoItemList.clear();
        Cursor cursor = db.query(
                TodoDatabaseHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(TodoDatabaseHelper.COLUMN_ID));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(TodoDatabaseHelper.COLUMN_TITLE));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(TodoDatabaseHelper.COLUMN_DESCRIPTION));
            TodoItem item = new TodoItem(id, title, description);
            todoItemList.add(item);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void clearInputFields() {
        etTitle.setText("");
        etDescription.setText("");
        adapter.setSelectedItemId(-1);
    }
}
