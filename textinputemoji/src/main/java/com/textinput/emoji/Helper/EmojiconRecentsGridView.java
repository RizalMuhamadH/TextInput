package com.textinput.emoji.Helper;

import android.content.Context;
import android.widget.GridView;
import com.textinput.emoji.R;
import com.textinput.emoji.emoji.Emojicon;

public class EmojiconRecentsGridView  extends EmojiconGridView implements EmojiconRecents {
    private final EmojiAdapter mAdapter;


    EmojiconRecentsGridView(Context context, Emojicon[] emojicons,
                            EmojiconRecents recents, EmojiconsPopup emojiconsPopup, boolean useSystemDefault) {
        super(context, emojicons, recents, emojiconsPopup,useSystemDefault);
        EmojiconRecentsManager recents1 = EmojiconRecentsManager
                .getInstance(rootView.getContext());
        mAdapter = new EmojiAdapter(rootView.getContext(),  recents1, useSystemDefault);
        mAdapter.setEmojiClickListener(new OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (mEmojiconPopup.onEmojiconClickedListener != null) {
                    mEmojiconPopup.onEmojiconClickedListener.onEmojiconClicked(emojicon);
                }
            }
        });
        GridView gridView = rootView.findViewById(R.id.Emoji_GridView);
        gridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addRecentEmoji(Context context, Emojicon emojicon) {
        EmojiconRecentsManager recents = EmojiconRecentsManager
                .getInstance(context);
        recents.push(emojicon);

        // notify dataset changed
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

}