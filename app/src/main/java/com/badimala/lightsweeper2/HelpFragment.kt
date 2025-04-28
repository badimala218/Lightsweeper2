package com.badimala.lightsweeper2

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class HelpFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val parentView = inflater.inflate(R.layout.fragment_help, container, false)

        // Add animation to lightbulb
        val lightBulbImage = parentView.findViewById<ImageView>(R.id.lightBulbImageView)
        val noLightBulb = ObjectAnimator.ofFloat(lightBulbImage, "alpha", 1f, 0f)
        noLightBulb.duration = 2000

        val rotRight = ObjectAnimator.ofFloat(lightBulbImage, "rotation", 0f, 180f)
        rotRight.duration = 2000

        val rotContinue = ObjectAnimator.ofFloat(lightBulbImage, "rotation", 180f, 360f)
        rotContinue.duration = 2000

        val onLightBulb = ObjectAnimator.ofFloat(lightBulbImage, "alpha", 0f, 1f)
        onLightBulb.duration = 2000

        val animSet = AnimatorSet()
        animSet.play(rotContinue).after(rotRight)
        animSet.play(onLightBulb).after(noLightBulb)
        animSet.start()

        return parentView
    }
}