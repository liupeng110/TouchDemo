package com.xujun.drag;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;

    //view的子类 onlayout回调会再view的layout方法中执行 在layout之前会执行setFrame
    //setFrame判断view是否发生变化 如变,将ltrb传给view 调invalidate刷新ui返回true  否则返回false
public class DragImageView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "xujun";
    private Activity mActivity;
    private int screen_W, screen_H;// 可见屏幕的宽高度
    private int bitmap_W, bitmap_H;// 当前图片宽高
    private int MAX_W, MAX_H, MIN_W, MIN_H;// 极限值
    private int current_Top, current_Right, current_Bottom, current_Left;// 当前图片上下左右坐标
    private int start_Top = -1, start_Right = -1, start_Bottom = -1, start_Left = -1;// 初始化默认位置.
    private int start_x, start_y, current_x, current_y;// 触摸位置
    private float beforeLenght, afterLenght;// 两触点距离
    private float scale_temp;// 缩放比例
    private int mLastX;//最后一次x坐标
    private int mLastY;//最后一次y坐标
    private enum MODE {NONE, DRAG, ZOOM};//none 无,drag 拖拽 ,zoom 缩放
    private MODE mode = MODE.NONE;// 默认模式
    private boolean isControl_V = false;// 垂直监控
    private boolean isControl_H = false;// 水平监控
    private ScaleAnimation scaleAnimation;// 缩放动画
    private boolean isScaleAnim = false;// 缩放动画
    private MyAsyncTask myAsyncTask;// 异步动画
    public DragImageView(Context context) {
        super(context);
    }
    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }
    public void setScreen_W(int screen_W) {
        this.screen_W = screen_W;
    }//可见屏幕宽度
    public void setScreen_H(int screen_H) {
        this.screen_H = screen_H;
    }   //可见屏幕高度
    public DragImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        bitmap_W = bm.getWidth();
        bitmap_H = bm.getHeight();

        MAX_W = bitmap_W * 3;
        MAX_H = bitmap_H * 3;

        MIN_W = bitmap_W / 2;
        MIN_H = bitmap_H / 2;
    } //设置位图
    @Override protected void onLayout(boolean changed, int left, int top, int right,  int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (start_Top == -1) {
            start_Top = top;
            start_Left = left;
            start_Bottom = bottom;
            start_Right = right;
        }
    }//重新布局

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        int x= (int) event.getY();
        int y= (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);  break;
            case MotionEvent.ACTION_POINTER_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                onPointerDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if(mode!=MODE.NONE){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }else{
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        mLastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(event);
    }

    @Override public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Logger.i("onTouchEvent=ACTION_DOWN");
                onTouchDown(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN: //多点触摸
                Logger.i("onTouchEvent=ACTION_POINTER_DOWN");
                onPointerDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                Logger.i("onTouchEvent=ACTION_MOVE");
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                Logger.i("onTouchEvent=ACTION_UP");
                mode = MODE.NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:  // 多点松开
                Logger.i("onTouchEvent=ACTION_POINTER_UP");
                mode = MODE.NONE;
                if (isScaleAnim) {  doScaleAnim();  }//执行缩放还原
                break;
        }
        return true;
    }
    void onTouchDown(MotionEvent event) {
        mode = MODE.DRAG;

        current_x = (int) event.getRawX();
        current_y = (int) event.getRawY();

        start_x = (int) event.getX();
        start_y = current_y - this.getTop();

    }//按下
    void onPointerDown(MotionEvent event) {
      if   (event.getPointerCount() == 2) {
            mode = MODE.ZOOM;
            beforeLenght = getDistance(event);// 获取两点的距离
        }
    }//两个手指 只能放大缩小

    void onTouchMove(MotionEvent event) {
        int left = 0, top = 0, right = 0, bottom = 0;
 //处理拖动
 if (mode == MODE.DRAG) {

             //获取左上右下
            left = current_x - start_x;
            right = current_x + this.getWidth() - start_x;
            top = current_y - start_y;
            bottom = current_y - start_y + this.getHeight();
            //水平判断    防止越界
            if (isControl_H) {
                if (left >= 0) {                     left = 0;                                                 right = this.getWidth();       }
                if (right <= screen_W) {    left = screen_W - this.getWidth();      right = screen_W;      }
            } else {
                                                            left = this.getLeft();                              right = this.getRight();
            }

            //垂直判断
            if (isControl_V) {
                if (top >= 0) {                       top = 0;                                                 bottom = this.getHeight();  }
                if (bottom <= screen_H) {   top = screen_H - this.getHeight();     bottom = screen_H;             }
            } else {                                        top = this.getTop();                             bottom = this.getBottom();  }

            if (isControl_H || isControl_V)
                this.setPosition(left, top, right, bottom);

            current_x = (int) event.getRawX();
            current_y = (int) event.getRawY(); //最后设置当前触摸坐标点
        }

 //处理缩放
else if (mode == MODE.ZOOM) {
            afterLenght = getDistance(event);// 获取两点的距离
            float gapLenght = afterLenght - beforeLenght;// 变化的长度
            if (Math.abs(gapLenght) > 5f) {
                scale_temp = afterLenght / beforeLenght;// 求的缩放的比例
                this.setScale(scale_temp);
                beforeLenght = afterLenght;
            }
        }

    }  //event--move  移动的处理
    float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }//获取两点的距离
    private void setPosition(int left, int top, int right, int bottom) {
        this.layout(left, top, right, bottom);
    }//实现处理拖动
    void setScale(float scale) {
        int disX = (int) (this.getWidth() * Math.abs(1 - scale)) / 4;// 获取缩放水平距离
        int disY = (int) (this.getHeight() * Math.abs(1 - scale)) / 4;// 获取缩放垂直距离

 // 放大
  if (scale > 1 && this.getWidth() <= MAX_W) {
            current_Left = this.getLeft() - disX;
            current_Top = this.getTop() - disY;
            current_Right = this.getRight() + disX;
            current_Bottom = this.getBottom() + disY;
            this.setFrame(current_Left, current_Top, current_Right, current_Bottom);

            //此时考虑对称  只做一遍判断
            if (current_Top <= 0 && current_Bottom >= screen_H) {
                //		Log.e("jj", "屏幕高度=" + this.getHeight());
                isControl_V = true;// 开启垂直监控
            } else {
                isControl_V = false;//关闭垂直监控
            }

            if (current_Left <= 0 && current_Right >= screen_W) {
                isControl_H = true;// 开启水平监控
            } else {
                isControl_H = false;//关闭水平监控
            }

        }
 // 缩小
 else if (scale < 1 && this.getWidth() >= MIN_W) {
            current_Left = this.getLeft() + disX;
            current_Top = this.getTop() + disY;
            current_Right = this.getRight() - disX;
            current_Bottom = this.getBottom() - disY;
            // 上边越界
            if (isControl_V && current_Top > 0) {
                current_Top = 0;
                current_Bottom = this.getBottom() - 2 * disY;
                if (current_Bottom < screen_H) { current_Bottom = screen_H; isControl_V = false; }// 关闭垂直监听
            }

            // 下边越界
            if (isControl_V && current_Bottom < screen_H) {
                current_Bottom = screen_H;
                current_Top = this.getTop() + 2 * disY;
                if (current_Top > 0) { current_Top = 0;  isControl_V = false;  }// 关闭垂直监听
            }

            // 左边越界
            if (isControl_H && current_Left >= 0) {
                current_Left = 0;
                current_Right = this.getRight() - 2 * disX;
                if (current_Right <= screen_W) {  current_Right = screen_W; isControl_H = false;  }// 关闭水平监控
            }

            // 右边越界
            if (isControl_H && current_Right <= screen_W) {
                current_Right = screen_W;
                current_Left = this.getLeft() + 2 * disX;
                if (current_Left >= 0) { current_Left = 0; isControl_H = false; }// 关闭水平监控
            }

            if (isControl_H || isControl_V) {
                this.setFrame(current_Left, current_Top, current_Right, current_Bottom);
            } else {
                this.setFrame(current_Left, current_Top, current_Right, current_Bottom);
                isScaleAnim = true;
            }// 开启缩放动画

        }

    }//处理缩放
    public void doScaleAnim() {
        myAsyncTask = new MyAsyncTask(screen_W, this.getWidth(), this.getHeight());
        myAsyncTask.setLTRB(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
        myAsyncTask.execute();
        isScaleAnim = false;// 关闭动画
    }//缩放动画处理

    //回缩动画执行
    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        private int screen_W, current_Width, current_Height;
        private int left, top, right, bottom;
        private float scale_WH;// 宽高的比例
        public void setLTRB(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }//当前位置属性
        private float STEP = 8f;// 步伐
        private float step_H, step_V;// 水平步伐，垂直步伐
        public MyAsyncTask(int screen_W, int current_Width, int current_Height) {
            super();
            this.screen_W = screen_W;
            this.current_Width = current_Width;
            this.current_Height = current_Height;
            scale_WH = (float) current_Height / current_Width;
            step_H = STEP;
            step_V = scale_WH * STEP;
        }
        @Override  protected Void doInBackground(Void... params) {

            while (current_Width <= screen_W) {
                left -= step_H;
                top -= step_V;
                right += step_H;
                bottom += step_V;
                current_Width += 2 * step_H;

                left = Math.max(left, start_Left);
                top = Math.max(top, start_Top);
                right = Math.min(right, start_Right);
                bottom = Math.min(bottom, start_Bottom);
                Log.e("jj", "top="+top+",bottom="+bottom+",left="+left+",right="+right);
                publishProgress(left, top, right, bottom);
                try { Thread.sleep(10);  } catch (InterruptedException e) { e.printStackTrace(); }//？为何休眠？
            }

            return null;
        }//后台执行费时计算
        @Override protected void onProgressUpdate(final Integer... values) {
            super.onProgressUpdate(values);
            mActivity.runOnUiThread(new Runnable() {
                @Override public void run() { setFrame(values[0], values[1], values[2], values[3]);      }
            });
        }//更新ui
    }

}
