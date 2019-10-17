package com.example.consumerapps.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.consumerapps.CustomOnClickListener;
import com.example.consumerapps.NoteAddUpdateActivity;
import com.example.consumerapps.R;
import com.example.consumerapps.entity.Note;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private ArrayList<Note> listNote = new ArrayList<>();
    private Activity activity;

    public NoteAdapter(Activity activity){
        this.activity = activity;
    }
    public ArrayList<Note> getListNotes(){
        return listNote;
    }
    public void setListNote(ArrayList<Note> listNotes){
        if (listNotes.size() > 0){
            this.listNote.clear();
        }
        this.listNote.addAll(listNotes);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.tvTitle.setText(listNote.get(position).getTitle());
        holder.tvDate.setText(listNote.get(position).getDate());
        holder.tvDescription.setText(listNote.get(position).getDescription());
        holder.itemView.setOnClickListener(new CustomOnClickListener(position, new CustomOnClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent mIntent = new Intent(activity, NoteAddUpdateActivity.class);
                mIntent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position);
                mIntent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, listNote.get(position));
                activity.startActivityForResult(mIntent, NoteAddUpdateActivity.REQUEST_UPDATE);
            }
        }));

    }

    @Override
    public int getItemCount() {
        return listNote.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDate;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            tvDate = itemView.findViewById(R.id.tv_item_date);
        }
    }
    public void addItem(Note note){
        this.listNote.add(note);
        notifyItemInserted(listNote.size() -1);
    }
    public void updateItem(int position, Note note){
        this.listNote.set(position, note);
        notifyItemChanged(position, note);
    }
    public void removeItem(int position){
        this.listNote.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listNote.size());

    }
}
