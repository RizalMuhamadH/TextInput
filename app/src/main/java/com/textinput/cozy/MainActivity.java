package com.textinput.cozy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.textinput.emoji.Actions.EmojIconActions;
import com.textinput.emoji.Helper.EmojiconEditText;
import com.textinput.emoji.Helper.EmojiconTextView;

public class MainActivity extends AppCompatActivity {

    private EmojiconEditText emojiconEditText;
    private EmojiconTextView textView;
    private EmojIconActions emojIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View rootView = findViewById(R.id.root_view);
        ImageView emojiButton = findViewById(R.id.emoji_btn);
        ImageView submitButton = findViewById(R.id.submit_btn);
        CheckBox mCheckBox = findViewById(R.id.use_system_default);
        emojiconEditText = findViewById(R.id.emojicon_edit_text);
        EmojiconEditText emojiconEditText2 = findViewById(R.id.emojicon_edit_text2);
        textView = findViewById(R.id.textView);
        emojIcon = new EmojIconActions(this, rootView, emojiconEditText, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                emojIcon.setUseSystemEmoji(b);
                textView.setUseSystemDefault(b);
            }
        });
        emojIcon.addEmojiconEditTextList(emojiconEditText2);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = emojiconEditText.getText().toString();
                textView.setText(newText);
            }
        });
    }
}