/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lemonade

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    enum class LemonadeState {
        SELECT, // SELECT represents the "pick lemon" state
        SQUEEZE, // SQUEEZE represents the "squeeze lemon" state
        DRINK, // DRINK represents the "drink lemonade" state
        RESTART // RESTART represents the state where the lemonade has been drunk and the glass is empty
    }

    /**
     * DO NOT ALTER ANY VARIABLE OR VALUE NAMES OR THEIR INITIAL VALUES.
     *
     * Anything labeled var instead of val is expected to be changed in the functions but DO NOT
     * alter their initial values declared here, this could cause the app to not function properly.
     */
    private val LEMONADE_STATE = "LEMONADE_STATE"
    private val LEMON_SIZE = "LEMON_SIZE"
    private val SQUEEZE_COUNT = "SQUEEZE_COUNT"

    // Default the state to select
    private var lemonadeState = LemonadeState.SELECT

    // Default lemonSize to -1
    private var lemonSize = -1

    // Default the squeezeCount to -1
    private var squeezeCount = -1

    private lateinit var lemonImage: ImageView
    private lateinit var lemonText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // === DO NOT ALTER THE CODE IN THE FOLLOWING IF STATEMENT ===
        if (savedInstanceState != null) {
            lemonadeState = savedInstanceState.getSerializable(LEMONADE_STATE) as LemonadeState
            lemonSize = savedInstanceState.getInt(LEMON_SIZE, -1)
            squeezeCount = savedInstanceState.getInt(SQUEEZE_COUNT, -1)
        }
        // === END IF STATEMENT ===

        lemonImage = findViewById(R.id.image_lemon_state)
        lemonText = findViewById(R.id.text_action)
        setViewElements()
        lemonImage.setOnClickListener {
            clickLemonImage()
        }
        lemonImage.setOnLongClickListener {
            showSnackbar()
        }
    }

    /**
     * === DO NOT ALTER THIS METHOD ===
     *
     * This method saves the state of the app if it is put in the background.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(LEMONADE_STATE, lemonadeState)
        outState.putInt(LEMON_SIZE, lemonSize)
        outState.putInt(SQUEEZE_COUNT, squeezeCount)
        super.onSaveInstanceState(outState)
    }

    /**
     * Clicking will elicit a different response depending on the state.
     * This method determines the state and proceeds with the correct action.
     */
    private fun clickLemonImage() {
        if (lemonadeState == LemonadeState.SELECT) {
            val pickedLemon = LemonTree().pick()

            lemonSize = pickedLemon
            squeezeCount = 0

            lemonadeState = LemonadeState.SQUEEZE
        } else if (lemonadeState == LemonadeState.SQUEEZE) {
            lemonSize--
            squeezeCount++

            if (lemonSize == 0) {
                lemonadeState = LemonadeState.DRINK
                lemonSize = -1
                squeezeCount = -1
            }
        } else if (lemonadeState == LemonadeState.DRINK) {
            lemonadeState = LemonadeState.RESTART
        } else if (lemonadeState == LemonadeState.RESTART) {
            lemonadeState = LemonadeState.SELECT
        } else {
            throw InvalidLemonadeStateException(lemonadeState)
        }

        setViewElements()
    }

    /**
     * Set up the view elements according to the state.
     */
    private fun setViewElements() {
        val (textId, imageId) = when (lemonadeState) {
            LemonadeState.SELECT -> Pair(R.string.lemon_select, R.drawable.lemon_tree)
            LemonadeState.SQUEEZE -> Pair(R.string.lemon_squeeze, R.drawable.lemon_squeeze)
            LemonadeState.DRINK -> Pair(R.string.lemon_drink, R.drawable.lemon_drink)
            LemonadeState.RESTART -> Pair(R.string.lemon_empty_glass, R.drawable.lemon_restart)
            else -> throw InvalidLemonadeStateException(lemonadeState)
        }

        lemonText.text = getString(textId)
        lemonImage.setImageResource(imageId)
    }

    /**
     * === DO NOT ALTER THIS METHOD ===
     *
     * Long clicking the lemon image will show how many times the lemon has been squeezed.
     */
    private fun showSnackbar(): Boolean {
        if (lemonadeState != LemonadeState.SQUEEZE) {
            return false
        }
        val squeezeText = getString(R.string.squeeze_count, squeezeCount)
        Snackbar.make(
            findViewById(R.id.constraint_Layout),
            squeezeText,
            Snackbar.LENGTH_SHORT
        ).show()
        return true
    }
}

/**
 * A Lemon tree class with a method to "pick" a lemon. The "size" of the lemon is randomized
 * and determines how many times a lemon needs to be squeezed before you get lemonade.
 */
class LemonTree {
    fun pick(): Int {
        return (2..4).random()
    }
}

class InvalidLemonadeStateException(invalidState: MainActivity.LemonadeState) :
    Exception("Invalid lemonade state (${invalidState})")