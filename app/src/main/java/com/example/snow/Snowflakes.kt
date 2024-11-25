package com.example.snow

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.AsyncTask
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.sin
import kotlin.random.Random

data class Snowflake(
    var x: Float,
    var y: Float,
    var velocity: Float,
    val radius: Float,
    val color: Int,
    var horizontalOffset: Float = 0f,
    val baseX: Float = x,
    var horizontalSpeed: Float = 2 + Random.nextFloat() * 3
)

lateinit var snow: Array<Snowflake>
val paint = Paint()
var h = 1000
var w = 1000

open class Snowflakes(ctx: Context) : View(ctx) {
    lateinit var moveTask: MoveTask

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLUE)

        for (s in snow) {
            paint.color = s.color
            canvas.drawCircle(s.x + s.horizontalOffset, s.y, s.radius, paint)
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        h = bottom - top
        w = right - left
        val r = Random(System.currentTimeMillis())

        snow = Array(50) {
            Snowflake(
                x = r.nextFloat() * w,
                y = r.nextFloat() * h,
                velocity = 5 + 10 * r.nextFloat(),
                radius = 10 + 20 * r.nextFloat(),
                color = Color.rgb(
                    200 + r.nextInt(56),
                    200 + r.nextInt(56),
                    200 + r.nextInt(56)
                )
            )
        }
        Log.d("mytag", "snow: " + snow.contentToString())
    }

    fun moveSnowflakes() {
        val time = System.currentTimeMillis() / 1000f
        for (s in snow) {
            val slowdownFactor = 1 - (s.y / h)
            val currentVelocity = s.velocity * (0.2f + 0.8f * slowdownFactor)

            s.y += currentVelocity
            s.x += s.horizontalSpeed * sin(time + s.y / 100)

            if (s.x > w) s.x = 0f
            if (s.x < 0) s.x = w.toFloat()
            if (s.y > h) {
                s.y = 0f
                s.x = Random.nextFloat() * w
            }
        }
        invalidate()
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        moveTask = MoveTask(this)
        moveTask.execute(50)
        return false
    }

    class MoveTask(val s: Snowflakes) : AsyncTask<Int, Int, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            val delay = params[0] ?: 200
            while (true) {
                Thread.sleep(delay.toLong())
                s.moveSnowflakes()
            }
        }
    }
}
