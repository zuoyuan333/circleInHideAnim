package com.zy.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * 主页扫描时,出现很多圈圈,向屏幕中间缩小,靠拢,透明 ,消失,就是这货了
 * @author zy on 2018/1/24. 14:28
 */

public class CircleView
        extends View{
    private boolean stopAnim=false;
    private int circleColor;
    private int radial = 0;
    private Context mContext;
    private int centerWidth;//中心x
    private int centerHeight;//中心y
    private int minR;//生成的圈圈到中心点的最小半径;
    private int maxR;//生成的圈圈到中心点的最大半径;
    int tempR=1;//随机数范围
    //要显示的白圈圈的集合  暂定8个
    private int[] setX=     {0,0,0,0,0,0};//x
    private int[] setY=     {0,0,0,0,0,0};//y
    private int[] setR=     {0,0,0,0,0,0};//圈圈大小
    private int[] reduceX=  {0,0,0,0,0,0};//每次减小的x
    private int[] reduceY=  {0,0,0,0,0,0};//每次减小的y
    private Paint[] setPaint={new Paint(),new Paint(),new Paint(),new Paint(),new Paint(),new Paint()};
    private int hideCircle=50;//隐藏圈圈的范围
    public CircleView(Context context) {
        super(context,null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        initView();
    }

    private void initView() {
        stopAnim=false;
        //圈圈默认白色
        circleColor=0x3F00a73c;
        for (Paint paint : setPaint) {
            //背景默认透明
            paint = new Paint();
            //抗锯齿
            paint.setAntiAlias(true);
            //Paint.Style.STROKE：描边 ; Paint.Style.FILL_AND_STROKE：描边并填充 ; Paint.Style.FILL：填充
            paint.setStyle(Paint.Style.FILL);
        }
    }

    /**
     * 执行动画之前,要使用此方法进行初始化画图
     */
    public void readyViewDraw(){
        if (radial<=0){
            radial=30;
        }
        //中心坐标
        centerWidth=(getRight()-getLeft())/2;
        centerHeight=(getBottom()-getTop())/2;
        //计算要生成圈圈的区间
        //最小,最短的边长度的一半减去一个值  目前是2倍圈圈大小
        if (centerWidth>centerHeight){
            minR=centerHeight-radial/2;
        }else{
            minR=centerWidth-radial/2;
        }
        //对角边的一半  勾股定理
        maxR=(int)Math.sqrt(centerHeight*centerHeight+centerWidth*centerWidth);
        //r的取值范围
        tempR=Math.abs(maxR-minR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < setPaint.length; i++) {
            /**
             * 绘制圆环drawCircle的前两个参数表示圆心的XY坐标
             * 第三个参数是圆的半径，第四个参数则为画笔
             */
            canvas.drawCircle(setX[i], setY[i], setR[i], setPaint[i]);
        }
    }
    private void startRun() {
        int a=0;//防止大小变得太块
        boolean b;
        /**
         * 使用while循环不断的刷新view的半径
         * 当半径小于100每次增加10 invalidate()重绘view会报错
         * android.view.ViewRootImpl$CalledFromWrongThreadException 是非主线程更新UI
         * Android给提供postInvalidate();快捷方法来重绘view
         */
        while (true) {
            if (stopAnim){
                return;
            }
            for (int i = 0; i < setX.length; i++) {
                b=shouldHide(setX[i],setY[i]);
                if (b){
                    getRandomCoordinate(i);
                }else{
                    //根据相对中心位置控制坐标加减
                    if (setY[i]>centerHeight){
                        setY[i]-=reduceY[i];
                    }else{
                        setY[i]+=reduceY[i];
                    }
                    if (setX[i]>centerWidth){
                        setX[i]-=reduceX[i];
                    }else{
                        setX[i]+=reduceX[i];
                    }
                    a++;
                    if (a>=3){
                        a=0;
                        setR[i]-=1;
                    }
                }
            }
            postInvalidate();
            try {
                Thread.sleep(20);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否应该去隐藏圈圈
     */
    private boolean shouldHide(int x,int y){
        //消失的圈圈是50
        if (hideCircle<=60){
            hideCircle=dp2px(mContext,80);
        }
        int currentlong=(int)Math.sqrt(Math.abs(centerWidth-x)*Math.abs(centerWidth-x)+Math.abs(centerHeight-y)*Math.abs(centerHeight-y));
        return currentlong-100<0;
    }
    /**
     * 生成一个新的圈圈的信息
     */
    private void getRandomCoordinate(int i){
        Random r=new Random();
        setR[i]=radial+r.nextInt(radial/3);//设置圈圈大小
        int angle=r.nextInt(360);//随机一个角度
        int lineLong=minR+new Random().nextInt(tempR);//随机一个斜边长度
        //根据勾股定理计算x,y
        //根据角度和斜边长和中心点计算x,y的坐标
        int tempAngle=angle % 90;
        if (tempAngle<=20){
            tempAngle=tempAngle+10;
        }else if (tempAngle>=70){
            tempAngle=tempAngle-10;
        }
        int x= (int)(lineLong * Math.sin(2 * Math.PI / 360 * tempAngle));
        int y=(int)(lineLong * Math.cos(2 * Math.PI / 360 * tempAngle));
        reduceX[i]=x/50;
        reduceY[i]=y/50;
        if (angle<90){
            setX[i]=centerWidth+x;
            setY[i]=centerHeight+y;
        }else if (angle<180){
            setX[i]=centerWidth-x;
            setY[i]=centerHeight+y;
        }else if (angle<270){
            setX[i]=centerWidth-x;
            setY[i]=centerHeight-y;
        }else{
            setX[i]=centerWidth+x;
            setY[i]=centerHeight-y;
        }
        setPaint[i].setColor(circleColor);
    }

    /**
     * 设置圈圈颜色
     */
    public void setCircleColor(int color){
        circleColor=color;
    }
    /**
     * 设置圈圈大小 dp
     */
    public void setCircleSize(int size){
        radial=dp2px(mContext,size);
    }
    /**
     * 设置圈圈消失的区域(dp)
     */
    public void setHideRegionSize(int dp){
        hideCircle=dp2px(mContext,dp);
    }

    /**
     * 开始直播
     */
    public void startAnim(){
        for (int i = 0; i < setX.length; i++) {
            getRandomCoordinate(i);
        }
        stopAnim=false;
        new Thread(){
            @Override
            public void run() {
                startRun();
            }
        }.start();
    }

    /**
     * 直播结束
     */
    public void stopAnim(){
        for (Paint paint : setPaint) {
            paint.setColor(0x00000000);
        }
        postInvalidate();
        stopAnim=true;
    }
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
