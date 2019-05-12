package com.enteresanlikk.notdefteri;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {

    private Context mContext;
    private List<Note> noteList;
    ArrayList<HashMap<String, String>> notes;

    Functions f;
    Database db;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, content, reminder;
        public ImageView open_menu, delete;
        LinearLayout card_layout, reminder_layout;

        public MyViewHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.title);
            content = (TextView) v.findViewById(R.id.content);
            reminder = (TextView) v.findViewById(R.id.reminder);

            open_menu = (ImageView) v.findViewById(R.id.open_menu);
            delete = (ImageView) v.findViewById(R.id.delete);

            card_layout = (LinearLayout) v.findViewById(R.id.card_layout);
            reminder_layout = (LinearLayout) v.findViewById(R.id.reminder_layout);
        }
    }

    public NoteAdapter(Context mContext, List<Note> noteList) {
        this.mContext = mContext;
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {
        f = new Functions(mContext.getApplicationContext());
        db = new Database(mContext.getApplicationContext());

        Note note = noteList.get(i);
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());

        if(note.getReminder() != null && note.getReminder().toString().equals("1")) {
            holder.reminder_layout.setVisibility(View.VISIBLE);
            holder.reminder.setText(note.getReminder_date()+" "+note.getReminder_time());
        }

        notes = db.list("1");

        final Integer noteId = Integer.valueOf(notes.get(i).get("id"));

        holder.open_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showPopupMenu(holder.open_menu, i);
            }
        });

        holder.card_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, EditActivity.class);
                i.putExtra("id", noteId);
                mContext.startActivity(i);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle(mContext.getString(R.string.delete));
                alertDialogBuilder.setIcon(R.drawable.delete_icon);
                alertDialogBuilder
                        .setMessage(mContext.getString(R.string.delete_your_note))
                        .setCancelable(false)
                        .setNeutralButton(mContext.getString(R.string.yes),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Boolean status = db.delete(noteId);
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
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void showPopupMenu(View view,int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext,view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.note_card_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenuItemClickListener(position));
        popup.show();
    }

    public void noteControl() {
        if(noteList.size()<=0) {
            f.go(MainActivity.class);
        }
    }

    class PopupMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int position;

        public PopupMenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            final HashMap<String, String> note = notes.get(position);
            final Integer noteId = Integer.valueOf(note.get("id"));

            switch (menuItem.getItemId()) {
                case R.id.edit:
                    Intent i = new Intent(mContext, EditActivity.class);
                    i.putExtra("id", noteId);
                    mContext.startActivity(i);
                    break;

                case R.id.share:

                    String message = String.format("%s\n\n%s\n\n%s",note.get("title"), note.get("content"), note.get("date"));

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                    sendIntent.setType("text/plain");
                    mContext.startActivity(sendIntent);

                    break;

                case R.id.delete:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setTitle(mContext.getString(R.string.delete));
                    alertDialogBuilder.setIcon(R.drawable.delete_icon);
                    alertDialogBuilder
                            .setMessage(mContext.getString(R.string.delete_your_note))
                            .setCancelable(false)
                            .setNeutralButton(mContext.getString(R.string.yes),new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    Boolean status = db.delete(noteId);
                                    if(status) {
                                        noteList.remove(position);
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

                    break;
            }

            return false;
        }
    }
}
