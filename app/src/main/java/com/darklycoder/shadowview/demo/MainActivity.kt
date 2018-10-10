package com.darklycoder.shadowview.demo

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import com.darklycoder.lib.ShadowView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initEvents()
        updateUI(false)
    }

    private fun initEvents() {
        sw_state.setOnCheckedChangeListener { _, _ ->
            updateUI()
        }

        sp_side.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                updateUI()
            }

        }

        sp_mode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                updateUI()
            }

        }

        sb_shadow_radius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, forUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                updateUI()
            }
        })

        sb_dx.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, forUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                updateUI()
            }
        })

        sb_dy.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, forUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                updateUI()
            }
        })

        sb_radius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, forUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                updateUI()
            }
        })
    }

    private fun updateUI(needRefresh: Boolean = true) {
        val checked = sw_state.isChecked
        val sidePos = sp_side.selectedItemPosition
        val modePos = sp_mode.selectedItemPosition

        sp_side.isEnabled = checked
        sp_mode.isEnabled = checked
        sb_shadow_radius.isEnabled = checked
        sb_dx.isEnabled = checked
        sb_dy.isEnabled = checked
        sb_radius.isEnabled = checked && (2 == modePos)

        if (!needRefresh) {
            return
        }

        if (!checked) {
            view_shadow.openShadow(false)

        } else {
            val side = when (sidePos) {
                0    -> ShadowView.ALL
                1    -> ShadowView.LEFT
                2    -> ShadowView.TOP
                3    -> ShadowView.RIGHT
                4    -> ShadowView.BOTTOM
                else -> ShadowView.ALL
            }

            val shape = when (modePos) {
                0    -> ShadowView.SHAPE_RECTANGLE
                1    -> ShadowView.SHAPE_OVAL
                2    -> ShadowView.SHAPE_ROUND
                else -> ShadowView.SHAPE_RECTANGLE
            }

            view_shadow
                    .setShadowSide(side)
                    .setShadowShape(shape)
                    .setShadowColor(Color.parseColor("#1FFF0000"))
                    .setShadowRadius(dp2px(this, sb_shadow_radius.progress))
                    .setShadowDx(dp2px(this, sb_dx.progress))
                    .setShadowDy(dp2px(this, sb_dy.progress))
                    .setRadius(dp2px(this, sb_radius.progress))
                    .openShadow(true)
        }
    }

    private fun dp2px(context: Context?, dpValue: Int): Float {
        if (null == context || dpValue == 0) {
            return dpValue.toFloat()
        }

        val scale = context.resources.displayMetrics.density
        return dpValue * scale + 0.5f
    }

}
