package com.textinput.emoji.Helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.textinput.emoji.R;
import com.textinput.emoji.emoji.Cars;
import com.textinput.emoji.emoji.Electronics;
import com.textinput.emoji.emoji.Emojicon;
import com.textinput.emoji.emoji.Food;
import com.textinput.emoji.emoji.Nature;
import com.textinput.emoji.emoji.People;
import com.textinput.emoji.emoji.Sport;
import com.textinput.emoji.emoji.Symbols;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EmojiconsPopup extends PopupWindow implements ViewPager.OnPageChangeListener, EmojiconRecents {
    private int mEmojiTabLastSelectedIndex = -1;
    private View[] mEmojiTabs;
    private PagerAdapter mEmojisAdapter;
    private EmojiconRecentsManager mRecentsManager;
    private int keyBoardHeight = 0;
    private Boolean pendingOpen = false;
    private Boolean isOpened = false;
    EmojiconGridView.OnEmojiconClickedListener onEmojiconClickedListener;
    private OnEmojiconBackspaceClickedListener onEmojiconBackspaceClickedListener;
    private OnSoftKeyboardOpenCloseListener onSoftKeyboardOpenCloseListener;
    private View rootView;
    private Context mContext;
    private boolean mUseSystemDefault;
    private View view;
    private String iconPressedColor="#495C66";
    private String tabsColor="#DCE1E2";
    private String backgroundColor="#E6EBEF";

    private ViewPager emojisPager;

    public EmojiconsPopup(View rootView, Context mContext,boolean useSystemDefault, String iconPressedColor,String tabsColor,String backgroundColor){
        super(mContext);
        boolean setColor = true;
        this.backgroundColor=backgroundColor;
        this.iconPressedColor=iconPressedColor;
        this.tabsColor=tabsColor;
        this.mUseSystemDefault=useSystemDefault;
        this.mContext = mContext;
        this.rootView = rootView;
        View customView = createCustomView();
        setContentView(customView);
        setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setSize(255);
        setBackgroundDrawable(null);


    }

    public EmojiconsPopup(View rootView, Context mContext,boolean useSystemDefault){
        super(mContext);
        this.mUseSystemDefault=useSystemDefault;
        this.mContext = mContext;
        this.rootView = rootView;
        View customView = createCustomView();
        setContentView(customView);
        setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setSize(255);
        setBackgroundDrawable(null);

    }
    /**
     * Set the listener for the event of keyboard opening or closing.
     */
    public void setOnSoftKeyboardOpenCloseListener(OnSoftKeyboardOpenCloseListener listener){
        this.onSoftKeyboardOpenCloseListener = listener;
    }

    /**
     * Set the listener for the event when any of the emojicon is clicked
     */
    public void setOnEmojiconClickedListener(EmojiconGridView.OnEmojiconClickedListener listener){
        this.onEmojiconClickedListener = listener;
    }

    /**
     * Set the listener for the event when backspace on emojicon popup is clicked
     */
    public void setOnEmojiconBackspaceClickedListener(OnEmojiconBackspaceClickedListener listener){
        this.onEmojiconBackspaceClickedListener = listener;
    }

    /**
     * Use this function to show the emoji popup.
     * NOTE: Since, the soft keyboard sizes are variable on different android devices, the
     * library needs you to open the soft keyboard atleast once before calling this function.
     * If that is not possible see showAtBottomPending() function.
     *
     */
    public void showAtBottom(){
        showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }
    /**
     * Use this function when the soft keyboard has not been opened yet. This
     * will show the emoji popup after the keyboard is up next time.
     * Generally, you will be calling InputMethodManager.showSoftInput function after
     * calling this function.
     */
    public void showAtBottomPending(){
        if(isKeyBoardOpen())
            showAtBottom();
        else
            pendingOpen = true;
    }

    /**
     *
     * @return Returns true if the soft keyboard is open, false otherwise.
     */
    public Boolean isKeyBoardOpen(){
        return isOpened;
    }

    /**
     * Dismiss the popup
     */
    @Override
    public void dismiss() {
        super.dismiss();
        EmojiconRecentsManager
                .getInstance(mContext).saveRecents();
    }

    /**
     * Call this function to resize the emoji popup according to your soft keyboard size
     */
    public void setSizeForSoftKeyboard(){
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int screenHeight = getUsableScreenHeight();
                int heightDifference = screenHeight
                        - (r.bottom - r.top);
                int resourceId = mContext.getResources()
                        .getIdentifier("status_bar_height",
                                "dimen", "android");
                if (resourceId > 0) {
                    heightDifference -= mContext.getResources()
                            .getDimensionPixelSize(resourceId);
                }
                if (heightDifference > 100) {
                    keyBoardHeight = heightDifference;
                    setSize(keyBoardHeight);
                    if (!isOpened) {
                        if (onSoftKeyboardOpenCloseListener != null)
                            onSoftKeyboardOpenCloseListener.onKeyboardOpen(keyBoardHeight);
                    }
                    isOpened = true;
                    if (pendingOpen) {
                        showAtBottom();
                        pendingOpen = false;
                    }
                } else {
                    isOpened = false;
                    if (onSoftKeyboardOpenCloseListener != null)
                        onSoftKeyboardOpenCloseListener.onKeyboardClose();
                }
            }
        });
    }

    private int getUsableScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();

            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            assert windowManager != null;
            windowManager.getDefaultDisplay().getMetrics(metrics);

            return metrics.heightPixels;

        } else {
            return rootView.getRootView().getHeight();
        }
    }

    /**
     * Manually set the popup window size
     * @param height Height of the popup
     */
    private void setSize(int height){
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(height);
    }
    public  void updateUseSystemDefault(boolean mUseSystemDefault) {
        if (view != null) {
            mEmojisAdapter=null;
            int positionPager = emojisPager.getCurrentItem();
            dismiss();

            this.mUseSystemDefault = mUseSystemDefault;
            setContentView(createCustomView());
            //mEmojisAdapter.notifyDataSetChanged();
            mEmojiTabs[positionPager].setSelected(true);
            emojisPager.setCurrentItem(positionPager);
            onPageSelected(positionPager);
            if(!isShowing()){

                //If keyboard is visible, simply show the emoji popup
                if(isKeyBoardOpen()){
                    showAtBottom();
                    // changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                }

                //else, open the text keyboard first and immediately after that show the emoji popup
                else{
                    showAtBottomPending();
                    // changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                }
            }




        }
    }



    private View createCustomView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(R.layout.emojicons, null, false);
        emojisPager = view.findViewById(R.id.emojis_pager);
        LinearLayout tabs= view.findViewById(R.id.emojis_tab);

        emojisPager.setOnPageChangeListener(this);
        EmojiconRecents recents = this;
        mEmojisAdapter = new EmojisPagerAdapter(
                Arrays.asList(
                        new EmojiconRecentsGridView(mContext, null, null, this,mUseSystemDefault),
                        new EmojiconGridView(mContext, People.DATA, recents, this,mUseSystemDefault),
                        new EmojiconGridView(mContext, Nature.DATA, recents, this,mUseSystemDefault),
                        new EmojiconGridView(mContext, Food.DATA, recents, this,mUseSystemDefault),
                        new EmojiconGridView(mContext, Sport.DATA, recents, this,mUseSystemDefault),
                        new EmojiconGridView(mContext, Cars.DATA, recents, this,mUseSystemDefault),
                        new EmojiconGridView(mContext, Electronics.DATA, recents, this,mUseSystemDefault),
                        new EmojiconGridView(mContext, Symbols.DATA, recents, this,mUseSystemDefault)

                )
        );
        emojisPager.setAdapter(mEmojisAdapter);
        mEmojiTabs = new View[8];

        mEmojiTabs[0] = view.findViewById(R.id.emojis_tab_0_recents);
        mEmojiTabs[1] = view.findViewById(R.id.emojis_tab_1_people);
        mEmojiTabs[2] = view.findViewById(R.id.emojis_tab_2_nature);
        mEmojiTabs[3] = view.findViewById(R.id.emojis_tab_3_food);
        mEmojiTabs[4] = view.findViewById(R.id.emojis_tab_4_sport);
        mEmojiTabs[5] = view.findViewById(R.id.emojis_tab_5_cars);
        mEmojiTabs[6] = view.findViewById(R.id.emojis_tab_6_elec);
        mEmojiTabs[7] = view.findViewById(R.id.emojis_tab_7_sym);
        for (int i = 0; i < mEmojiTabs.length; i++) {
            final int position = i;
            mEmojiTabs[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    emojisPager.setCurrentItem(position);
                }
            });
        }


            emojisPager.setBackgroundColor(Color.parseColor(backgroundColor));
            tabs.setBackgroundColor(Color.parseColor(tabsColor));
        for (View mEmojiTab : mEmojiTabs) {
            ImageButton btn = (ImageButton) mEmojiTab;
            btn.setColorFilter(Color.parseColor(iconPressedColor));
        }

            ImageButton imgBtn= view.findViewById(R.id.emojis_backspace);
            imgBtn.setColorFilter(Color.parseColor(iconPressedColor));
            imgBtn.setBackgroundColor(Color.parseColor(backgroundColor));


        view.findViewById(R.id.emojis_backspace).setOnTouchListener(new RepeatListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(onEmojiconBackspaceClickedListener != null)
                    onEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(v);
            }
        }));

        // get last selected page
        mRecentsManager = EmojiconRecentsManager.getInstance(view.getContext());
        int page = mRecentsManager.getRecentPage();
        // last page was recents, check if there are recents to use
        // if none was found, go to page 1
        if (page == 0 && mRecentsManager.size() == 0) {
            page = 1;
        }

        if (page == 0) {
            onPageSelected(page);
        }
        else {
            emojisPager.setCurrentItem(page, false);
        }
        return view;
    }

    @Override
    public void addRecentEmoji(Context context, Emojicon emojicon) {
        EmojiconRecentsGridView fragment = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            fragment = ((EmojisPagerAdapter)Objects.requireNonNull(emojisPager.getAdapter())).getRecentFragment();
        }
        assert fragment != null;
        fragment.addRecentEmoji(context, emojicon);
    }


    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        if (mEmojiTabLastSelectedIndex == i) {
            return;
        }
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:

                if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < mEmojiTabs.length) {
                    mEmojiTabs[mEmojiTabLastSelectedIndex].setSelected(false);
                }
                mEmojiTabs[i].setSelected(true);
                mEmojiTabLastSelectedIndex = i;
                mRecentsManager.setRecentPage(i);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private static class EmojisPagerAdapter extends PagerAdapter {
        private List<EmojiconGridView> views;
        EmojiconRecentsGridView getRecentFragment(){
            for (EmojiconGridView it : views) {
                if(it instanceof EmojiconRecentsGridView)
                    return (EmojiconRecentsGridView)it;
            }
            return null;
        }
        EmojisPagerAdapter(List<EmojiconGridView> views) {
            super();
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View v = views.get(position).rootView;
            container.addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
            container.removeView((View)view);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object key) {
            return key == view;
        }
    }

    /**
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     * <p/>
     * <p>Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     */
    static class RepeatListener implements View.OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final OnClickListener clickListener;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView == null) {
                    return;
                }
                handler.removeCallbacksAndMessages(downView);
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
                clickListener.onClick(downView);
            }
        };

        private View downView;

        /**
         * @param clickListener   The OnClickListener, that will be called
         */
        RepeatListener(OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");

            this.initialInterval = 500;
            this.normalInterval = 50;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downView = view;
                    handler.removeCallbacks(handlerRunnable);
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    handler.removeCallbacksAndMessages(downView);
                    downView = null;
                    return true;
            }
            return false;
        }
    }

    public interface OnEmojiconBackspaceClickedListener {
        void onEmojiconBackspaceClicked(View v);
    }

    public interface OnSoftKeyboardOpenCloseListener{
        void onKeyboardOpen(int keyBoardHeight);
        void onKeyboardClose();
    }




}