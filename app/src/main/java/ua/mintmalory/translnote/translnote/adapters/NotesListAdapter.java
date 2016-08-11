package ua.mintmalory.translnote.translnote.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import ua.mintmalory.translnote.translnote.R;
import ua.mintmalory.translnote.translnote.model.Note;

public class NotesListAdapter extends ArrayAdapter<Note> {

    private Context mContext;

    public NotesListAdapter(Context context, List<Note> notes) {
        super(context, R.layout.list_item, notes);
        mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.noteHeaderView = (TextView) convertView.findViewById(R.id.item_headerText);
            holder.noteSubHeaderView = (TextView) convertView.findViewById(R.id.item_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = getItem(position);

        holder.noteHeaderView.setText(note.getHeader());

        //dateFormat перенести в строковые ресурсы и менять в зависимости от локализации
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        holder.noteSubHeaderView.setText(dateFormat.format(note.getCreationDate()));
        return convertView;
    }

    private static class ViewHolder {
        TextView noteHeaderView;
        TextView noteSubHeaderView;
    }

}