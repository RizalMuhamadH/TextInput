package com.textinput.emoji.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.textinput.emoji.emoji.Emojicon;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class EmojiconRecentsManager extends ArrayList<Emojicon> {

    private static final String PREFERENCE_NAME = "emojicon";
    private static final String PREF_RECENTS = "recent_emojis";
    private static final String PREF_PAGE = "recent_page";

    private static final Object LOCK = new Object();
    private static EmojiconRecentsManager sInstance;

    private final Context mContext;

    private EmojiconRecentsManager(Context context) {
        mContext = context.getApplicationContext();
        loadRecents();
    }

    static EmojiconRecentsManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new EmojiconRecentsManager(context);
                }
            }
        }
        return sInstance;
    }

    int getRecentPage() {
        return getPreferences().getInt(PREF_PAGE, 0);
    }

    void setRecentPage(int page) {
        getPreferences().edit().putInt(PREF_PAGE, page).apply();
    }

    void push(Emojicon object) {
        // FIXME totally inefficient way of adding the emoji to the adapter
        // TODO this should be probably replaced by a deque
        if (contains(object)) {
            super.remove(object);
        }
        add(0, object);
    }

    @Override
    public boolean add(Emojicon object) {
        return super.add(object);
    }

    @Override
    public void add(int index, Emojicon object) {
        super.add(index, object);
    }

    @Override
    public boolean remove(Object object) {
        return super.remove(object);
    }

    private SharedPreferences getPreferences() {
        return mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    private void loadRecents() {
        SharedPreferences prefs = getPreferences();
        String str = prefs.getString(PREF_RECENTS, "");
        StringTokenizer tokenizer = new StringTokenizer(str, "~");
        while (tokenizer.hasMoreTokens()) {
            try {
                add(new Emojicon(tokenizer.nextToken()));
            }
            catch (NumberFormatException e) {
                // ignored
            }
        }
    }

    void saveRecents() {
        StringBuilder str = new StringBuilder();
        int c = size();
        for (int i = 0; i < c; i++) {
            Emojicon e = get(i);
            str.append(e.getEmoji());
            if (i < (c - 1)) {
                str.append('~');
            }
        }
        SharedPreferences prefs = getPreferences();
        prefs.edit().putString(PREF_RECENTS, str.toString()).apply();
    }

}
