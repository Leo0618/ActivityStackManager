package vip.okfood.asm_sample.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import vip.okfood.asm_sample.R;

/**
 * function:WXCommentTextActivity
 *
 * <p></p>
 * Created by Leo on 2019/4/4.
 */
public class WXCommentTextActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxcommenttext);
        final WXCommentText wxCommentText1 = findViewById(R.id.commentTxt1);
        final WXCommentText wxCommentText2 = findViewById(R.id.commentTxt2);
        final WXCommentText wxCommentText3 = findViewById(R.id.commentTxt3);

        wxCommentText1.setComment(new Comment("Leo：", "这是一条评论消息", "一分钟前"));
        wxCommentText2.setComment(new Comment("Leo：", "这是一条评论一条评论一条一条评论评论一条评论一条评论消息", "05-01 12:30"));
        wxCommentText3.setComment(new Comment("Leo：", "这是一条一条评论评论一条评论消息", "昨天 09:25"));
    }

    @SuppressWarnings("WeakerAccess")
    static class Comment {
        public String author;
        public String content;
        public String time;

        public Comment(String author, String content, String time) {
            this.author = author;
            this.content = content;
            this.time = time;
        }

        @Override
        public String toString() {
            return "Comment{"+
                    "author='"+author+'\''+
                    ", content='"+content+'\''+
                    ", time='"+time+'\''+
                    '}';
        }
    }
}
