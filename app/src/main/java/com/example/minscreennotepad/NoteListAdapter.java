package com.example.minscreennotepad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minscreennotepad.NoteClasses.Note;
import com.example.minscreennotepad.NoteClasses.NoteAudio;
import com.example.minscreennotepad.NoteClasses.NoteImage;

import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder> {

    private List<Note> noteList;
    private LayoutInflater lInflater;
    private Context context;
    private OnNoteListener mOnNoteListener;

    public NoteListAdapter(List<Note> noteList, Context context, OnNoteListener onNoteListener) {
        this.noteList = noteList;
        this.lInflater = LayoutInflater.from(context);
        this.context = context;
        this.mOnNoteListener = onNoteListener;
    }

    @Override
    public int getItemCount() { return noteList.size(); }

    @Override
    public NoteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = lInflater.inflate(R.layout.notelist_element, null);
        return new NoteListAdapter.ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(final NoteListAdapter.ViewHolder holder, final int position) {
        holder.bindData(noteList.get(position));
    }

    public void setItems(List<Note> items) { this.noteList = items; }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView noteIcon;
        TextView noteTitle;
        OnNoteListener onNoteListener;

        ViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            noteIcon = itemView.findViewById(R.id.noteIconImageView);
            noteTitle = itemView.findViewById(R.id.noteTitleText);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        void bindData(final Note item) {
            noteTitle.setText(item.getTitle());
            if(item instanceof NoteImage) {
                noteIcon.setImageResource(R.drawable.imagenoteicon);
            }
            else if(item instanceof NoteAudio) {
                noteIcon.setImageResource(R.drawable.audionoteicon);
            }
            else {
                noteIcon.setImageResource(R.drawable.textnoteicon);
            }
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAbsoluteAdapterPosition());
        }
    }

    //sacado de https://www.youtube.com/watch?v=69C1ljfDvl0&list=WL&index=6
    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
