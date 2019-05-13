package com.enteresanlikk.notdefteri;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecycleNoteAdapter extends RecyclerView.Adapter<RecycleNoteAdapter.MyViewHolder> {

    private Context mContext;
    private List<Note> noteList;
    ArrayList<HashMap<String, String>> notes;

    Functions f;
    Database db;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, date, reminder;
        LinearLayout card_layout, reminder_layout;
        ImageView download, delete;

        public MyViewHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.title);
            content = (TextView) v.findViewById(R.id.content);
            date = (TextView) v.findViewById(R.id.date);
            reminder = (TextView) v.findViewById(R.id.reminder);

            card_layout = (LinearLayout) v.findViewById(R.id.card_layout);
            reminder_layout = (LinearLayout) v.findViewById(R.id.reminder_layout);

            download = (ImageView) v.findViewById(R.id.download);
            delete = (ImageView) v.findViewById(R.id.delete);
        }
    }

    public RecycleNoteAdapter(Context mContext, List<Note> noteList) {
        this.mContext = mContext;
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_note_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {
        f = new Functions(mContext.getApplicationContext());
        db = new Database(mContext.getApplicationContext());
        Note note = noteList.get(i);

        notes = db.list("0");
        final Integer noteId = Integer.valueOf(notes.get(i).get("id"));

        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        holder.date.setText(note.getDate());

        if(note.getReminder() != null && note.getReminder().toString().equals("1")) {
            holder.reminder_layout.setVisibility(View.VISIBLE);
            holder.reminder.setText(note.getReminder_date()+" "+note.getReminder_time());
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle(mContext.getString(R.string.delete));
                alertDialogBuilder.setIcon(R.drawable.delete_icon);
                alertDialogBuilder
                        .setMessage(mContext.getString(R.string.delete_permanent_your_note))
                        .setCancelable(false)
                        .setNeutralButton(mContext.getString(R.string.yes),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Boolean status = db.permanentDelete(noteId);
                                if(status) {
                                    noteList.remove(i);
                                    notifyDataSetChanged();
                                    noteControl();
                                    f.message(mContext.getString(R.string.successDeleted));
                                } else {
                                    f.message(mContext.getString(R.string.errorDeleted));
                                }
                            }
                        })
                        .setNegativeButton(mContext.getString(R.string.no),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                alertDialogBuilder.show();
            }
        });

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle(mContext.getString(R.string.delete));
                alertDialogBuilder.setIcon(R.drawable.delete_icon);
                alertDialogBuilder
                        .setMessage(mContext.getString(R.string.restore_your_note))
                        .setCancelable(false)
                        .setNeutralButton(mContext.getString(R.string.yes),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Boolean status = db.active(noteId);
                                if(status) {
                                    noteList.remove(i);
                                    notifyDataSetChanged();
                                    noteControl();
                                    f.message(mContext.getString(R.string.successRestored));
                                } else {
                                    f.message(mContext.getString(R.string.errorRestored));
                                }
                            }
                        })
                        .setNegativeButton(mContext.getString(R.string.no),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                alertDialogBuilder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void noteControl() {
        if(noteList.size()<=0) {
            f.go(MainActivity.class);
        }
    }
}
