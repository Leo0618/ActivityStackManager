package vip.okfood.asm_sample.test;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * function:WXCommentText
 *
 * <p></p>
 * Created by Leo on 2019/4/4.
 */
public class WXCommentText extends android.support.v7.widget.AppCompatTextView {
    public WXCommentText(Context context) {
        super(context);
    }

    public WXCommentText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WXCommentText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //空字符-占位
    private static final String SPACE     = " ";
    //时间相对于内容大小的比例
    private static final float  ratio     = 0.8f;
    //时间文本的颜色
    public static final  int    colorGray = Color.parseColor("#999999");

    public void setComment(WXCommentTextActivity.Comment comment) {
        post(() -> {
            String authorContent = comment.author+comment.content;
            new SubStringClickSpan(WXCommentText.this, authorContent, 0, comment.author.length(),
                    v -> Toast.makeText(getContext(), (String) v.getTag(), Toast.LENGTH_SHORT).show());

            SpannableString styledText = SpannableString.valueOf(SPACE+SPACE+comment.time);
            styledText.setSpan(new RelativeSizeSpan(ratio), 0, styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new ForegroundColorSpan(colorGray), 0, styledText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            int timeTxtWidth = (int) getPaint().measureText(styledText, 0, styledText.length());

            int width        = getWidth();
            int contentWidth = (int) getPaint().measureText(comment.content);
            int lastWidth    = width-contentWidth%width;
            //如果作者及内容显示完后的剩余宽度不够显示时间，那么就换行
            //另一行内容：多个空字符串+时间内容(时间靠右),如果时间靠左对其则不需加空字符
            if(lastWidth <= timeTxtWidth) {
                append("\n");
                float spaceWidth = getPaint().measureText(SPACE);
                int   count      = (int) ((width-timeTxtWidth)*1.0f/spaceWidth+0.5f);
                for(int x = 0; x < count; x++) append(SPACE);
            }
            append(styledText);
        });
    }


    class SubStringClickSpan extends ClickableSpan {
        private int                  colorDefault = Color.BLUE;
        private View.OnClickListener mOnClickListener;
        private CharSequence         source;

        SubStringClickSpan(TextView target, CharSequence source, int start, int end, View.OnClickListener onClickListener) {
            SpannableString str = new SpannableString(source);
            this.mOnClickListener = onClickListener;
            this.source = source;
            str.setSpan(this, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            target.setText(str);
            target.setMovementMethod(LinkMovementMethod.getInstance());
            target.setHighlightColor(Color.TRANSPARENT);
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(colorDefault);
            ds.setUnderlineText(false);
            ds.bgColor = 0;
        }

        @Override
        public void onClick(@NonNull View widget) {
            if(mOnClickListener != null) {
                widget.setTag(source);
                mOnClickListener.onClick(widget);
            }
        }
    }
}
