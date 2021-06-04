package com.example.minscreennotepad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    /**
     * Constructor de NoteListAdapter
     * @param noteList Lista de notas
     * @param context   Contexto de la actividad
     * @param onNoteListener OnNoteListener para escuchar los clicks
     */
    public NoteListAdapter(List<Note> noteList, Context context, OnNoteListener onNoteListener) {
        this.noteList = noteList;
        this.lInflater = LayoutInflater.from(context);
        this.context = context;
        this.mOnNoteListener = onNoteListener;
    }

    /**
     * Getter del numero de items de la lista
     * @return int del numero de items en noteList
     */
    @Override
    public int getItemCount() { return noteList.size(); }

    /**
     * Metodo llamado al crear el ViewHolder
     * @param parent ViewGroup de la actividad padre
     * @param viewType int del tipo de View
     * @return ViewHolder
     */
    @Override
    public NoteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = lInflater.inflate(R.layout.notelist_element, null);
        return new NoteListAdapter.ViewHolder(view, mOnNoteListener);
    }

    /**
     * Metodo que recorre la noteList, añadiendo sus datos al ViewHolder
     * @param holder ViewHolder al que se añaden los datos
     * @param position int de la posición actual de la noteList
     */
    @Override
    public void onBindViewHolder(final NoteListAdapter.ViewHolder holder, final int position) {
        holder.bindData(noteList.get(position));
    }

    /**
     * Setter de la Lista de items
     * @param items Nuevos items
     */
    public void setItems(List<Note> items) { this.noteList = items; }

    /**
     * Clase ViewHolder implementando OnClickListener
     * sacado de https://www.youtube.com/watch?v=69C1ljfDvl0&list=WL&index=6
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView noteIcon;
        TextView noteTitle;
        OnNoteListener onNoteListener;

        /**
         * Constructor de viewHolder
         * @param itemView itemView de la app
         * @param onNoteListener interfaz OnNoteListener
         */
        ViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            noteIcon = itemView.findViewById(R.id.noteIconImageView);
            noteTitle = itemView.findViewById(R.id.noteTitleText);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        /**
         * Agrega datos a las cards del adapter
         * @param item
         */
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

        /**
         * Gestiona lo que ocurre al hacer click en una de las cards
         * @param v
         */
        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAbsoluteAdapterPosition());
        }
    }

    /**
     * Interfaz OnNoteListener, para comunicar la MainActivity con el NoteListAdapter
     */
    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
