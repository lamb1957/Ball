package com.example.ball

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import kotlin.properties.Delegates
import android.view.SurfaceView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SensorEventListener, SurfaceHolder.Callback{

    private var mBallX: Float = 0f
    private var mBallY: Float = 0f
    private var mVX: Float = 0f
    private var mVY: Float = 0f
    private var mFrom: Long = 0
    private var mTo: Long = 0
    private val RADIUS: Float = 50.0f
    private val COEF: Float = 1000.0f

    private var flg: Boolean = false

    private var left: Int = 10
    private var top: Int = 100
    private var right: Int = 300
    private var bottom: Int = 200





    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, i1: Int, i2: Int) {
        mSurfaceWidth = i1
        mSurfaceHeight = i2
        mBallX = (i1 / 2).toFloat()
        mBallY = (i2 / 2).toFloat()
        mVX = 0f
        mVY = 0f

    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mFrom = System.currentTimeMillis()
        mSensorManager.registerListener(this, mAccSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mSensorManager.unregisterListener(this)
    }

    private var mHolder:SurfaceHolder by Delegates.notNull<SurfaceHolder>()
    private var mSurfaceWidth:Int = 0
    private var mSurfaceHeight:Int = 0

    private var mSensorManager: SensorManager by Delegates.notNull<SensorManager>()
    private var mAccSensor:Sensor by Delegates.notNull<Sensor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_main)


        mHolder = surfaceView.holder
        mHolder.addCallback(this)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override  fun onAccuracyChanged(sensor: Sensor?, accuracy: Int){
    }

    override fun onSensorChanged(event: SensorEvent?){
        var x: Float = -event?.values!![0]
        var y: Float = event?.values!![1]
        var z: Float = event?.values!![2]

        mTo = System.currentTimeMillis()
        var t: Float = (mTo - mFrom).toFloat()
        t = t / 1000.0f

        var dx: Float = mVX * t + x * t * t / 2.0f
        var dy: Float = mVY * t + y * t * t / 2.0f
        mBallX = mBallX + dx * COEF
        mBallY = mBallY + dy * COEF
        mVX = mVX + x * t
        mVY = mVY + y * t

        if(mBallX - RADIUS < 0 && mVX < 0){
            mVX = -mVX / 1.5f
            mBallX = RADIUS
        }else if (mBallX + RADIUS > mSurfaceWidth && mVX > 0){
            mVX = -mVX / 1.5f
            mBallX = mSurfaceWidth - RADIUS
        }

        if(mBallY - RADIUS < 0 && mVY < 0){
            mVY = -mVY / 1.5f
            mBallY = RADIUS
        }else if (mBallY + RADIUS > mSurfaceHeight && mVY >0){
            mVY = -mVY / 1.5f
            mBallY = mSurfaceHeight -RADIUS
        }

        mFrom = System.currentTimeMillis()
        drawCanvas()

        Log.d("Sensormanager", "---------")
        Log.d("x", event?.values!![0].toString())
        Log.d("y", event?.values!![1].toString())
        Log.d("z", event?.values!![2].toString())

        if(left <= mBallX + RADIUS && right >= mBallX- RADIUS && top <= mBallY + RADIUS && bottom >= mBallY - RADIUS){
            flg = true
        }else{
            flg = false
        }
    }

    private fun drawCanvas(){
        var c: Canvas = mHolder.lockCanvas()
        c.drawColor(Color.YELLOW)
        var paint :Paint = Paint()
        paint.setColor(Color.MAGENTA)
        c.drawCircle(mBallX, mBallY, RADIUS, paint)
        paint.setColor(Color.BLUE)
        var rect: Rect = Rect(left, top, right, bottom)
        c.drawRect(rect, paint)
        if(flg == true){
            c.drawText("HelloText", 100f, 100f, paint)
        }
        mHolder.unlockCanvasAndPost(c)
    }

    /*override fun onResume() {
        super.onResume()

    mSensorManager.registerListener(this, mAccSensor, SensorManager.SENSOR_DELAY_GAME)

    }

    override fun onPause(){
       super.onPause()
        mSensorManager.unregisterListener(this)
    }   */
}