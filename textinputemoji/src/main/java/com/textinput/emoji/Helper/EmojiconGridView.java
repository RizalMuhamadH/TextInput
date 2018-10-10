package com.textinput.emoji.Helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import com.textinput.emoji.R;
import com.textinput.emoji.emoji.Emojicon;
import com.textinput.emoji.emoji.People;

import java.util.Arrays;

public class EmojiconGridView{
    View rootView;
    final EmojiconsPopup mEmojiconPopup;
    private EmojiconRecents mRecents;
    private boolean mUseSystemDefault = false;


    EmojiconGridView(Context context, Emojicon[] emojicons, EmojiconRecents recents, EmojiconsPopup emojiconPopup, boolean useSystemDefault) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mEmojiconPopup = emojiconPopup;
        assert inflater != null;
        rootView = inflater.inflate(R.layout.emojicon_grid, null);
        setRecents(recents);
        GridView gridView = rootView.findViewById(R.id.Emoji_GridView);
        Emojicon[] mData;
        if (emojicons== null) {
            mData = People.DATA;
        } else {
            mData = Arrays.asList((Object[]) emojicons).toArray(new Emojicon[emojicons.length]);
        }
        EmojiAdapter mAdapter = new EmojiAdapter(rootView.getContext(), mData,useSystemDefault);
        mAdapter.setEmojiClickListener(new OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (mEmojiconPopup.onEmojiconClickedListener != null) {
                    mEmojiconPopup.onEmojiconClickedListener.onEmojiconClicked(emojicon);
                }
                if (mRecents != null) {
                    mRecents.addRecentEmoji(rootView.getContext(), emojicon);
                }
            }
        });
        gridView.setAdapter(mAdapter);
    }

    private void setRecents(EmojiconRecents recents) {
        mRecents = recents;
    }

    public interface OnEmojiconClickedListener {
        void onEmojiconClicked(Emojicon emojicon);
    }

}