package com.amassfreight.widget;

import com.amassfreight.warehouse.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 公用dialog
 * @author U11001548
 *
 */
public class ApDialog extends Dialog implements View.OnClickListener{

	private static final String ARGUMENT_GLOBAL = "Global";//全局对话框

    private static final String ARGUMENT_TITLE = "title";

    private static final String ARGUMENT_TITLE_GRAVITY = "gravity";

    private static final String ARGUMENT_MESSGE_GRAVITY = "message gravity";

    private static final String ARGUMENT_TITLE_ICON = "icon";

    private static final String ARGUMENT_MESSAGE = "message";

    private static final String ARGUMENT_VIEW = "view";

    private static final String ARGUMENT_MESSAGEVIEW_ID = "message view id";

    private static final String ARGUMENT_POSITIVE_BUTTON = "positive_button";

    private static final String ARGUMENT_NEUTRAL_BUTTON = "neutral_button";

    private static final String ARGUMENT_NEGATIVE_BUTTON = "negative_button";

    private static final String ARGUMENT_POSITIVE_BUTTON_COLOR = "positive_button_color";

    private static final String ARGUMENT_NEUTRAL_BUTTON_COLOR = "neutral_button_color";

    private static final String ARGUMENT_NEGATIVE_BUTTON_COLOR = "negative_button_color";

    private static final String ARGUMENT_SET_ONCLICK_DISMISS = "onclick_dismiss";

    private static final String ARGUMENT_SET_BACKGROUND_RESOURCE = "setBackground Resource";

    private static final String ARGUMENT_SET_BACKGROUND_COLOR = "setBackground color";

    private Drawable icon;

    private View content_view;

    private ViewGroup contentGroup;

    private boolean onClickDismiss = true;// 是否需要点击按钮后自动退出对话框

    private Bundle mArguments;

    private int tag;

    private TextView txtTitle;

    private TextView txtMessage;

    private Object extra;

    protected ApDialog(Context context) {
        super(context, R.style.ApDialog);
    }

    private OnDialogClickListener mOnDialogClickListener;
    private OnDialogCreateListener mOnDialogCreateListener;

    public interface OnDialogClickListener {
        void onDialogClick(int tag, int which, Dialog dlg);
    }

    public interface OnDialogCreateListener {
        void onDialogCreate(int tag, Dialog dlg);
        void onDialogStart(int tag, Dialog dlg);
    }

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        mOnDialogClickListener = listener;
    }

    public OnDialogClickListener getOnDialogClickListener() {
        return mOnDialogClickListener;
    }

    public void setOnDialogCreateListener(OnDialogCreateListener listener) {
        mOnDialogCreateListener = listener;
    }

    public OnDialogCreateListener getOnDialogCreateListener() {
        return mOnDialogCreateListener;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public Object getExtra() {
        return extra;
    }

    public void show(int tag) {
        this.tag = tag;
        super.show();
    }

    public void show() {
        super.show();
    }

    @Override
    protected void onStart() {
        Window window = this.getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.70f;//设置背景的明暗程度
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;//再此窗口下的背景要变暗
        window.setAttributes(windowParams);
        if(mOnDialogCreateListener != null){
            mOnDialogCreateListener.onDialogStart(tag, this);
        }
        super.onStart();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //获取bundle参数，设置相应属性
        Bundle arguments = getArguments();
        if (arguments.containsKey(ARGUMENT_GLOBAL)
                && arguments.getBoolean(ARGUMENT_GLOBAL)) {
        	//检索当前activity的窗口，设置当前窗口 always on top of application windows
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        setContentView(R.layout.widget_apdialog_view);
        contentGroup = (ViewGroup) findViewById(R.id.content_panel);

        //标题
        if (arguments.containsKey(ARGUMENT_TITLE)) {
            findViewById(R.id.top_panel).setVisibility(View.VISIBLE);
            txtTitle = (TextView) findViewById(R.id.alert_title);
            Object value = arguments.get(ARGUMENT_TITLE);
            if (value instanceof Integer) {
                txtTitle.setText((Integer) value);
            } else if (value instanceof CharSequence) {
                txtTitle.setText((CharSequence) value);
            }
            if (arguments.containsKey(ARGUMENT_TITLE_GRAVITY)) {
                txtTitle.setGravity(arguments.getInt(ARGUMENT_TITLE_GRAVITY,
                        Gravity.LEFT));
            }
        }

        //标题icon
        if (arguments.containsKey(ARGUMENT_TITLE_ICON)) {
            findViewById(R.id.top_panel).setVisibility(View.VISIBLE);
            ImageView icon = (ImageView) findViewById(R.id.icon);
            icon.setVisibility(View.VISIBLE);
            int value = (Integer) arguments.get(ARGUMENT_TITLE_ICON);
            if (value == -1) {
                icon.setImageDrawable(this.icon);
            } else {
                icon.setImageResource(value);
            }
        }

        if (arguments.containsKey(ARGUMENT_VIEW)) {
            contentGroup.removeAllViews();
            int value = (Integer) arguments.get(ARGUMENT_VIEW);
            if (value == -1) {
                contentGroup.addView(content_view);
            } else {
                View.inflate(getContext(), value, contentGroup);
            }
        }

        //提示信息
        if (arguments.containsKey(ARGUMENT_MESSAGE)) {
            if (!arguments.containsKey(ARGUMENT_VIEW)) {
                contentGroup.setVisibility(View.VISIBLE);
                txtMessage = (TextView) findViewById(R.id.message);
            } else {
                int viewId = (Integer) arguments.get(ARGUMENT_MESSAGEVIEW_ID);
                if (viewId != -1) {
                    txtMessage = (TextView) findViewById(viewId);
                } else {
                    txtMessage = (TextView) findViewById(R.id.message);
                }

            }
            if (txtMessage != null) {
                Object value = arguments.get(ARGUMENT_MESSAGE);
                if (value instanceof Integer) {
                    txtMessage.setText((Integer) value);
                } else if (value instanceof CharSequence) {
                    txtMessage.setText((CharSequence) value);
                }
            }

            if (arguments.containsKey(ARGUMENT_MESSGE_GRAVITY)) {
                txtMessage.setGravity(arguments.getInt(ARGUMENT_MESSGE_GRAVITY,
                        Gravity.LEFT));
            }
        }
        
        //按钮
        if (arguments.containsKey(ARGUMENT_POSITIVE_BUTTON)
                || arguments.containsKey(ARGUMENT_NEUTRAL_BUTTON)
                || arguments.containsKey(ARGUMENT_NEGATIVE_BUTTON)) {
            findViewById(R.id.button_panel).setVisibility(View.VISIBLE);

            boolean hasPositive = false, hasNeutral = false, hasNegative = false;
            if (arguments.containsKey(ARGUMENT_POSITIVE_BUTTON)) {
                Button btnPositive = (Button) findViewById(R.id.button_positive);
                btnPositive.setVisibility(View.VISIBLE);
                btnPositive.setOnClickListener(this);
                Object value = arguments.get(ARGUMENT_POSITIVE_BUTTON);
                if (value instanceof Integer) {
                    btnPositive.setText((Integer) value);
                } else if (value instanceof CharSequence) {
                    btnPositive.setText((CharSequence) value);
                }
                if (arguments.containsKey(ARGUMENT_POSITIVE_BUTTON_COLOR)) {
                    int color = arguments.getInt(ARGUMENT_POSITIVE_BUTTON_COLOR);
                    btnPositive.setTextColor(color);
                }
                hasPositive = true;
            }
            if (arguments.containsKey(ARGUMENT_NEUTRAL_BUTTON)) {
                Button btnNeutral = (Button) findViewById(R.id.button_neutral);
                btnNeutral.setVisibility(View.VISIBLE);
                btnNeutral.setOnClickListener(this);
                Object value = arguments.get(ARGUMENT_NEUTRAL_BUTTON);
                if (value instanceof Integer) {
                    btnNeutral.setText((Integer) value);
                } else if (value instanceof CharSequence) {
                    btnNeutral.setText((CharSequence) value);
                }
                if (arguments.containsKey(ARGUMENT_NEUTRAL_BUTTON_COLOR)) {
                    int color = arguments.getInt(ARGUMENT_NEUTRAL_BUTTON_COLOR);
                    btnNeutral.setTextColor(color);
                }
                hasNeutral = true;
            }
            if (arguments.containsKey(ARGUMENT_NEGATIVE_BUTTON)) {
                Button btnNegative = (Button) findViewById(R.id.button_negative);
                btnNegative.setVisibility(View.VISIBLE);
                btnNegative.setOnClickListener(this);
                Object value = arguments.get(ARGUMENT_NEGATIVE_BUTTON);
                if (value instanceof Integer) {
                    btnNegative.setText((Integer) value);
                } else if (value instanceof CharSequence) {
                    btnNegative.setText((CharSequence) value);
                }
                if (arguments.containsKey(ARGUMENT_NEGATIVE_BUTTON_COLOR)) {
                    int color = arguments.getInt(ARGUMENT_NEGATIVE_BUTTON_COLOR);
                    btnNegative.setTextColor(color);
                }
                hasNegative = true;
            }

            if (arguments.containsKey(ARGUMENT_SET_ONCLICK_DISMISS)) {
                onClickDismiss = (Boolean) arguments
                        .get(ARGUMENT_SET_ONCLICK_DISMISS);
            }

            if (hasNeutral && hasPositive) {
                findViewById(R.id.button_divider_1).setVisibility(View.VISIBLE);
            }
            if (hasNegative && (hasNeutral || hasPositive)) {
                findViewById(R.id.button_divider_2).setVisibility(View.VISIBLE);
            }

        }

        //
        if (arguments.containsKey(ARGUMENT_SET_BACKGROUND_RESOURCE)) {
            findViewById(R.id.layout).setBackgroundResource(
                    arguments.getInt(ARGUMENT_SET_BACKGROUND_RESOURCE));
        }

        if (arguments.containsKey(ARGUMENT_SET_BACKGROUND_COLOR)) {
            findViewById(R.id.layout).setBackgroundColor(
                    arguments.getInt(ARGUMENT_SET_BACKGROUND_COLOR));
        }
        if(mOnDialogCreateListener != null){
            mOnDialogCreateListener.onDialogCreate(tag, this);
        }
    }


    @Override
    public void setTitle(CharSequence text) {
        if (txtTitle == null) {
            txtTitle = (TextView) findViewById(R.id.alert_title);
        }
        txtTitle.setText(text);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_positive) {
            onDialogClick(DialogInterface.BUTTON_POSITIVE, this);
        } else if (id == R.id.button_neutral) {
            onDialogClick(DialogInterface.BUTTON_NEUTRAL, this);
        } else if (id == R.id.button_negative) {
            onDialogClick(DialogInterface.BUTTON_NEGATIVE, this);
        }
        if (onClickDismiss) {
            dismiss();
        }
    }

    private void onDialogClick(int which, Dialog dlg) {
        if (mOnDialogClickListener != null) {
            mOnDialogClickListener.onDialogClick(tag, which, dlg);
        }
    }

    /* package */void setDialogArguments(Bundle arguments) {
        mArguments = arguments;
    }

    Bundle getArguments() {
        return mArguments;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setView(View view) {
        this.content_view = view;
    }

    public View getContentGroup() {
        return contentGroup;
    }

    public static class Builder {

        /* package */ Bundle mArguments = new Bundle();

        private Boolean mCancelable;

        private Drawable icon;

        private View view;

        private Context context;

        /* package */void apply(ApDialog dialog) {
            dialog.setDialogArguments(mArguments);
            if (mCancelable != null) {
                dialog.setCancelable(mCancelable);
            }
            if (icon != null) {
                dialog.setIcon(icon);
            }

            if (view != null) {
                dialog.setView(view);
            }
        }

        /**
         * 创建对话框
         *
         * @return
         */
        public ApDialog create() {
            ApDialog dialog = new ApDialog(context);
            apply(dialog);
            return dialog;
        }

        /**
         * 创建对话框
         *
         * @param context 上下文对象
         * @return
         */
        public ApDialog create(Context context) {
            ApDialog dialog = new ApDialog(context);
            apply(dialog);
            return dialog;
        }

        /**
         * 设置上下文对象
         *
         * @param context
         * @return
         */
        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        /**
         * 设置标题
         *
         * @param title
         * @return
         */
        public Builder setTitle(CharSequence title) {
            mArguments.putCharSequence(ARGUMENT_TITLE, title);
            return this;
        }

        /**
         * 设置标题
         *
         * @param titleResId
         * @return
         */
        public Builder setTitle(int titleResId) {
            mArguments.putInt(ARGUMENT_TITLE, titleResId);
            return this;
        }

        /**
         * 设置标题显示位置
         *
         * @param gravity
         * @return
         */
        public Builder setTitleGravity(int gravity) {
            mArguments.putInt(ARGUMENT_TITLE_GRAVITY, gravity);
            return this;
        }

        /**
         * 设置内容显示位置
         *
         * @param gravity
         * @return
         */
        public Builder setMessageGravity(int gravity) {
            mArguments.putInt(ARGUMENT_MESSGE_GRAVITY, gravity);
            return this;
        }

        /**
         * 设置标题的图标片
         *
         * @param iconId
         * @return
         */
        public Builder setTitleIcon(int iconId) {
            mArguments.putInt(ARGUMENT_TITLE_ICON, iconId);
            return this;
        }

        /**
         * 设置标题的图标
         *
         * @param icon
         * @return
         */
        public Builder setTitleIcon(Drawable icon) {
            mArguments.putInt(ARGUMENT_TITLE_ICON, -1);
            this.icon = icon;
            return this;
        }

        /**
         * 设置对话框内容
         *
         * @param view
         * @return
         */
        public Builder setView(View view) {
            mArguments.putInt(ARGUMENT_VIEW, -1);
            this.view = view;
            return this;
        }

        /**
         * 设置对话框内容
         *
         * @param viewId
         * @return
         */
        public Builder setView(int viewId) {
            mArguments.putInt(ARGUMENT_VIEW, viewId);
            return this;
        }

        /**
         * 设置对话框内容显示文本，使用默认布局中的view
         *
         * @param msg
         * @return
         */
        public Builder setMessage(CharSequence msg) {
            mArguments.putCharSequence(ARGUMENT_MESSAGE, msg);
            mArguments.putInt(ARGUMENT_MESSAGEVIEW_ID, -1);
            return this;
        }

        /**
         * 设置对话框显示文本，使用默认布局中的view
         *
         * @param msgResId
         * @return
         */
        public Builder setMessage(int msgResId) {
            mArguments.putInt(ARGUMENT_MESSAGE, msgResId);
            mArguments.putInt(ARGUMENT_MESSAGEVIEW_ID, -1);
            return this;
        }

        /**
         * 设置对话框显示文本，使用指定布局中的view
         *
         * @param msg
         * @param msgViewId
         * @return
         */
        public Builder setMessage(CharSequence msg, int msgViewId) {
            mArguments.putCharSequence(ARGUMENT_MESSAGE, msg);
            mArguments.putInt(ARGUMENT_MESSAGEVIEW_ID, msgViewId);
            return this;
        }

        /**
         * 设置对话框显示文本，使用指定布局中的view
         *
         * @param msgResId
         * @param msgViewId
         * @return
         */
        public Builder setMessage(int msgResId, int msgViewId) {
            mArguments.putInt(ARGUMENT_MESSAGE, msgResId);
            mArguments.putInt(ARGUMENT_MESSAGEVIEW_ID, msgViewId);
            return this;
        }

        /**
         * 设置对话框Positive按钮的文字颜色
         *
         * @param color
         * @return
         */
        public Builder setPositiveButtonColor(int color) {
            mArguments.putInt(ARGUMENT_POSITIVE_BUTTON_COLOR, color);
            return this;
        }

        /**
         * 设置对话框neutral按钮的文字颜色
         * @param color
         * @return
         */
        public Builder setNeutralButtonColor(int color) {
            mArguments.putInt(ARGUMENT_NEGATIVE_BUTTON_COLOR, color);
            return this;
        }

        /**
         * 设置对话框negative按钮的文字颜色
         * @param color
         * @return
         */
        public Builder setNegativeButtonColor(int color) {
            mArguments.putInt(ARGUMENT_NEGATIVE_BUTTON_COLOR, color);
            return this;
        }

        /**
         * 设置左边按钮的文本
         *
         * @param text
         * @return
         */
        public Builder setPositiveButton(CharSequence text) {
            mArguments.putCharSequence(ARGUMENT_POSITIVE_BUTTON, text);
            return this;
        }

        /**
         * 设置左边按钮的文本
         *
         * @param textResId
         * @return
         */
        public Builder setPositiveButton(int textResId) {
            mArguments.putInt(ARGUMENT_POSITIVE_BUTTON, textResId);
            return this;
        }

        /**
         * 设置中间按钮的文本
         *
         * @param text
         * @return
         */
        public Builder setNeutralButton(CharSequence text) {
            mArguments.putCharSequence(ARGUMENT_NEUTRAL_BUTTON, text);
            return this;
        }

        /**
         * 设置中间按钮的文本
         *
         * @param textResId
         * @return
         */
        public Builder setNeutralButton(int textResId) {
            mArguments.putInt(ARGUMENT_NEUTRAL_BUTTON, textResId);
            return this;
        }

        /**
         * 设置右边按钮的文本
         *
         * @param text
         * @return
         */
        public Builder setNegativeButton(CharSequence text) {
            mArguments.putCharSequence(ARGUMENT_NEGATIVE_BUTTON, text);
            return this;
        }

        /**
         * 设置右边按钮的文本
         *
         * @param textResId
         * @return
         */
        public Builder setNegativeButton(int textResId) {
            mArguments.putInt(ARGUMENT_NEGATIVE_BUTTON, textResId);
            return this;
        }

        /**
         * 设置是否点击对话框外部或点击back键，对话框消失
         *
         * @param cancelable
         * @return
         */
        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        /**
         * 设置是否点击按钮对话框消失
         *
         * @param isDismiss
         * @return
         */
        public Builder setClickDissmiss(Boolean isDismiss) {
            mArguments.putBoolean(ARGUMENT_SET_ONCLICK_DISMISS, isDismiss);
            return this;
        }

        /**
         * 设置对话框背景
         *
         * @param resId
         * @return
         */
        public Builder setBackgroundResource(int resId) {
            mArguments.putInt(ARGUMENT_SET_BACKGROUND_RESOURCE, resId);
            return this;
        }

        /**
         * 设置对话框背景色
         *
         * @param color
         * @return
         */
        public Builder setBackgroundColor(int color) {
            mArguments.putInt(ARGUMENT_SET_BACKGROUND_COLOR, color);
            return this;
        }

        /**
         * 设置对话框是否是全局对话框
         *
         * @param global
         * @return
         */
        public Builder setGlobal(boolean global) {
            mArguments.putBoolean(ARGUMENT_GLOBAL, global);
            return this;
        }
    }
}
