package com.example.hams;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private ArrayList<StudentList> mStudentList;
    private onItemClickListener mListener;

    public StudentAdapter(ArrayList<StudentList> studentLists) {
        this.mStudentList = studentLists;
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        StudentViewHolder studentViewHolder = new StudentViewHolder(view, mListener);
        return studentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentList currentStudent = mStudentList.get(position);
        holder.imageView.setImageResource(currentStudent.getmImageRes());
        holder.textView.setText(currentStudent.getString());
    }

    @Override
    public int getItemCount() {
        return mStudentList.size();
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView;

        public StudentViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgIcon);
            textView = itemView.findViewById(R.id.txtView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
