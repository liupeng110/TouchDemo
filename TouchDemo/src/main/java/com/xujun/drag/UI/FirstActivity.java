package com.xujun.drag.UI;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;

import com.xujun.drag.BitmapUtil;
import com.xujun.drag.DragImageView;
import com.xujun.drag.R;

//第一个avtivity
public class FirstActivity extends Activity {
    private int window_width, window_height;// 控件宽度
    private DragImageView dragImageView;// 自定义控件
    private int state_height;// 状态栏的高度
    private ViewTreeObserver viewTreeObserver;
    @Override  public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        WindowManager manager = getWindowManager();//获取可见区域 高度
        window_width = manager.getDefaultDisplay().getWidth();
        window_height = manager.getDefaultDisplay().getHeight();

        dragImageView = (DragImageView) findViewById(R.id.div_main);
        Bitmap bmp = BitmapUtil.ReadBitmapById(this, R.drawable.huoying, window_width, window_height);
        dragImageView.setImageBitmap(bmp); // 设置图片
        dragImageView.setmActivity(this);         // 注入Activity
        viewTreeObserver = dragImageView.getViewTreeObserver();//测量状态栏高度
        viewTreeObserver .addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        if (state_height == 0) {// 获取状况栏高度
                            Rect frame = new Rect();
                            getWindow().getDecorView() .getWindowVisibleDisplayFrame(frame);
                            state_height = frame.top;
                            dragImageView.setScreen_H(window_height-state_height);
                            dragImageView.setScreen_W(window_width);
                        }
                    }
                });
    }
    public static Bitmap ReadBitmapById(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);  // 获取资源图片
        return BitmapFactory.decodeStream(is, null, opt);
    } //读取本地资源图片 res

}