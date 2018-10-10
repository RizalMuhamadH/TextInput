package com.textinput.emoji.Helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.textinput.emoji.R;
import com.textinput.emoji.emoji.Emojicon;
import java.util.List;


class EmojiAdapter extends ArrayAdapter<Emojicon> {
    private final boolean mUseSystemDefault;
    private EmojiconGridView.OnEmojiconClickedListener emojiClickListener;


    EmojiAdapter(Context context, List<Emojicon> data, boolean useSystemDefault) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = useSystemDefault;
    }

    EmojiAdapter(Context context, Emojicon[] data, boolean useSystemDefault) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = useSystemDefault;
    }


    void setEmojiClickListener(EmojiconGridView.OnEmojiconClickedListener listener){
        this.emojiClickListener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.emojicon_item, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = v.findViewById(R.id.emojicon_icon);
            holder.icon.setUseSystemDefault(mUseSystemDefault);

            v.setTag(holder);
        }

         Emojicon emoji = getItem(position);
         ViewHolder holder = (ViewHolder) v.getTag();
        assert emoji != null;
        holder.icon.setText(emoji.getEmoji());
                holder.icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emojiClickListener.onEmojiconClicked(getItem(position));
                    }
                });

        return v;
    }

    class ViewHolder {
        EmojiconTextView icon;
    }
}