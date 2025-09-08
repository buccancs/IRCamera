package com.energy.commoncomponent.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import com.energy.commoncomponent.R;

/**
 * Created by fengjibo on 2023/5/29.
 */
public class ItemComponentView extends LinearLayout {

    /**
     * Private method description.
     */
    private OnItemClickListener mOnItemClickListener;
    private Button mPlayButtonView;
    private TextView mTitleTextView;
    private SwitchCompat mSwitchCompat;
    private TextView mValueTextView;

    /**
     * Method description.
     */
    public ItemComponentView(Context context) {
        super(context);
        initView(null);
    }

    /**
     * Method description.
     */
    public ItemComponentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    /**
     * Private method description.
     */
    private void initView(AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_item_component, this);
        mTitleTextView = findViewById(R.id.tv_title);
        mPlayButtonView = findViewById(R.id.play_button);
        mSwitchCompat = findViewById(R.id.switch_compat);
        mValueTextView = findViewById(R.id.text_value);

        if (attrs != null) {
            TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.item_component);

            CharSequence title_text = attributes.getText(R.styleable.item_component_item_title);
            mTitleTextView.setText(title_text);
            boolean switchEnable = attributes.getBoolean(R.styleable.item_component_item_switch_enable, false);
            mSwitchCompat.setVisibility(switchEnable? View.VISIBLE : View.GONE);

            boolean buttonEnable = attributes.getBoolean(R.styleable.item_component_item_button_enable, false);
            mPlayButtonView.setVisibility(buttonEnable? View.VISIBLE : View.GONE);

            CharSequence button_text = attributes.getText(R.styleable.item_component_item_button_text);
            mPlayButtonView.setText(button_text);

            boolean valueEnable = attributes.getBoolean(R.styleable.item_component_item_value_enable, false);
            mValueTextView.setVisibility(valueEnable? View.VISIBLE : View.GONE);
            
            CharSequence value_text = attributes.getText(R.styleable.item_component_item_value_text);
            mValueTextView.setText(value_text);

            attributes.recycle();
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick();
                }
            }
        });
    }

    /**
     * Method description.
     */
    public interface OnItemClickListener {
        void onItemClick();
    }

    /**
     * Method description.
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * Method description.
     */
    public void setTitle(String text) {
        mTitleTextView.setText(text);
    }

    /**
     * Method description.
     */
    public String getTitle() {
        return mTitleTextView.getText().toString();
    }

    /**
     * Method description.
     */
    public void setPlayButtonText(String text) {
        mPlayButtonView.setText(text);
    }

    /**
     * Method description.
     */
    public void setValueText(String text) {
        mValueTextView.setText(text);
    }

    /**
     * Method description.
     */
    public void setSwitchCompatEnable(boolean switchCompatEnable) {
        mSwitchCompat.setVisibility(switchCompatEnable? View.VISIBLE : View.GONE);
    }

    /**
     * Method description.
     */
    public void setPlayButtonEnable(boolean playButtonEnable) {
        mPlayButtonView.setVisibility(playButtonEnable? View.VISIBLE : View.GONE);
    }

    /**
     * Method description.
     */
    public void setValueTextViewEnable(boolean valueTextViewEnable) {
        mValueTextView.setVisibility(valueTextViewEnable? View.VISIBLE : View.GONE);
    }

    /**
     * Method description.
     */
    public void setSwitchCompatChecked(boolean switchCompatChecked) {
        mSwitchCompat.setChecked(switchCompatChecked);
    }

    /**
     * Method description.
     */
    public void setSwitchCompatCheckListener(OnSwitchCompatCheckListener onSwitchCompatCheckListener) {
        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onSwitchCompatCheckListener != null) {
                    onSwitchCompatCheckListener.onCheckedChanged(buttonView, isChecked, getId());
                }
            }
        });
    }

    /**
     * Method description.
     */
    public void setButtonClickListener(OnButtonClickListener onButtonClickListener) {
        mPlayButtonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onButtonClickListener != null) {
                    onButtonClickListener.onClick(v, getId());
                }
            }
        });
    }

    /**
     * Method description.
     */
    public interface OnSwitchCompatCheckListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int id);
    }

    /**
     * Method description.
     */
    public interface OnButtonClickListener {
        void onClick(View view, int id);
    }
}

