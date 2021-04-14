package com.example.minesweeper

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.code_game.*
import kotlin.random.Random

class game_activity: Main_menuActivity()                                                             //inheriting Main_menuActivity
{


    var mineLocation = BooleanArray(row*col){false}                                             //Stores locations of mines
    var FirstMove = true                                                                             //Stores the first move made is safe
    var count = 0                                                                                    //Count is for setting buttons and their IDs
    var leftEdge = IntArray(row){0}                                                                  //Stores IDs/Indices of buttons at the left edge (used in fun countAdjacentMines())
    var left: Int = 0                                                                                //Used for storing the IDs in var leftEdge
    var rightEdge = IntArray(row){0}                                                                 //Stores IDs/Indices of buttons at the right edge (used in fun countAdjacentMines())
    var right: Int = col-1                                                                           //Used for storing the IDs in var rightEdge
    var topEdge = IntArray(col){0}                                                                   //Stores IDs/Indices of buttons at the top edge (used in fun countAdjacentMines())
    var bottomEdge = IntArray(col){0}                                                                //Stores IDs/Indices of buttons at the bottom edge (used in fun countAdjacentMines())
    var bottom: Int = 0                                                                              //Used for storing the IDs in var bottomEdge
    var isScanned = BooleanArray(row*col){false}                                                //Stores if a particular button ID has been scanned or not (used in fun countAdjacentMines())
    var movesLeft: Int = (row*col) - mines                                                           //Stores number of moves left
    var isfirst_flagmove = true                                                                       //Variable for holding if first move was placing a flag
    var isFlagged = BooleanArray(row*col){false}                                                //Stores if a button of particular ID has been flagged
    var flagCount: Int = mines                                                                       //Stores the number of flags remaining
    var time = -1                                                                                    //Stores time in seconds
    var count_up_timer = object : CountDownTimer(1000000, 1000)         //Variable for counting the time elapsed since game started, using class CountDownTimer
    {
        override fun onTick(millisUntilFinished: Long) {
            time++                                                                                     //Incrementing value for every time interval
            txv_timer.setText(time.toString())
        }
        override fun onFinish() {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.code_game)

        val flags = findViewById<TextView>(R.id.txv_flag_count)
        flags.setText(flagCount.toString())
        start_game()
        val timer = findViewById<TextView>(R.id.txv_timer)
        timer.setText("0")
    }

    fun setupBoard(){                                                                                  // generates the mines field
        val params1 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0
        )
        val params2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        params2.setMargins(0,0,0,0)

        for(i in 1..row){
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = params1
            params1.weight  = 1.0F

            for(j in 1..col){
                val button = Button(this)
                button.id = count
                button.textSize = 18.0F
                button.layoutParams = params2
                params2.weight = 1.0F
                button.setBackgroundResource(R.drawable.block_closed)                                  // initial plotting of button

                button.setOnClickListener{
                    if (FirstMove) {
                        count_up_timer.start()                                                           //Timer starts on first tap
                        FirstMove = false
                        if(mineLocation[button.id] == true)                                           //If the location has a mines, change its location
                            replace_mine(button.id)
                    }

                    if(is_mine(button.id)) {                                                            //If tapped button is a mines, end game,
                        button.setBackgroundResource(R.drawable.mines)
                        end(true)                                                             //with game lost = true
                    }
                    else {
                        count_adjacent_mines(button.id)                                                // to count mines around the tapped location
                    }
                }

                button.setOnLongClickListener{                                                         // Set onLongClickListener for plotting flag,
                    if (isfirst_flagmove) {                                                            // Timer starts when flag is first move
                        count_up_timer.start()
                        isfirst_flagmove = false
                    }
                    setFlag(button.id)
                    true
                }

                linearLayout.addView(button)                                                           // Adds button to the row
                count++
            }
            grid.addView(linearLayout)                                                            // Adds row to the mines field
        }
    }

    fun plot_mines(counter: Int){                                                                      // Function for randomly placing mines
        var i = 0

        for (i in 0..counter-1)
            mineLocation[i] = false

        while(i < mines){
            val random = Random.nextInt(0, counter)
            if (mineLocation[random]!=true) {                                                         //Stores mines locations in isMineLocation, if there is no mines previously
                mineLocation[random] = true
                i++
            }
        }
    }



    fun count_adjacent_mines(id: Int): Unit{                                                          //Function for counting the mines touching a particular tile
        val button = findViewById<Button>(id)
        var count = 0
        var onLeftEdge: Boolean = false
        var onRightEdge: Boolean = false
        var onTopEdge: Boolean = false
        var onBottomEdge: Boolean = false

        for (i in 0..row - 1){                                                                        // to check if button is at left or right edge
            if (id == leftEdge[i])
                onLeftEdge = true
            else if (id == rightEdge[i])
                onRightEdge = true
        }

        for (i in 0..col-1){                                                                          // to check if button is at top or bottom edge
            if (id == topEdge[i])
                onTopEdge = true
            else if (id == bottomEdge[i])
                onBottomEdge = true
        }

                                                                                                      //Scan for mines and increase the value of count
        //North
        if(is_valid(id-col) && mineLocation[id-col] && !onTopEdge)
            count++
        //North-East
        if(is_valid(id-col+1) && mineLocation[id-col+1] && !onRightEdge)
            count++
        //East
        if(is_valid(id+1) && mineLocation[id+1] && !onRightEdge)
            count++
        //South-East
        if(is_valid(id+col+1) && mineLocation[id+col+1] && !onRightEdge)
            count++
        //South
        if(is_valid(id+col) && mineLocation[id+col] && !onBottomEdge)
            count++
        //South-West
        if(is_valid(id+col-1) && mineLocation[id+col-1] && !onLeftEdge)
            count++
        //West
        if(is_valid(id-1) && mineLocation[id-1] && !onLeftEdge)
            count++
        //North-West
        if(is_valid(id-col-1) && mineLocation[id-col-1] && !onLeftEdge)
            count++

        if (isScanned[id] == true) {
            return
        }

        if (isFlagged[button.id]) {
            flagCount++
            txv_flag_count.setText(flagCount.toString())
        }


        isScanned[id] = true

        if (count == 0){                                                                              // If statement for uncover tiles if mines count is 0, using recursion
            //North
            if(is_valid(id-col) && !mineLocation[id-col] && !onTopEdge)
                count_adjacent_mines(id-col)
            //North-East
            if(is_valid(id-col+1) && !mineLocation[id-col+1] && !onRightEdge)
                count_adjacent_mines(id-col+1)
            //East
            if(is_valid(id+1) && !mineLocation[id+1] && !onRightEdge)
                count_adjacent_mines(id+1)
            //South-East
            if(is_valid(id+col+1) && !mineLocation[id+col+1] && !onRightEdge)
                count_adjacent_mines(id+col+1)
            //South
            if(is_valid(id+col) && !mineLocation[id+col] && !onBottomEdge)
                count_adjacent_mines(id+col)
            //South-West
            if(is_valid(id+col-1) && !mineLocation[id+col-1] && !onLeftEdge)
                count_adjacent_mines(id+col-1)
            //West
            if(is_valid(id-1) && !mineLocation[id-1] && !onLeftEdge)
                count_adjacent_mines(id-1)
            //North-West
            if(is_valid(id-col-1) && !mineLocation[id-col-1] && !onLeftEdge)
                count_adjacent_mines(id-col-1)
        }
        button.isEnabled = false
        place_mine_count(id, count)
        movesLeft--
        if (movesLeft == 0)                                                                            //If all possible moves have been done, end game
            end(false)
        else
            return
    }
    fun replace_mine(id: Int) {                                                                         //Function for replacing mines if first move holds a mines
        mineLocation[id] = false
        var mineReplaced = false
        while (!mineReplaced) {
            val random = Random.nextInt(0, count)
            if(mineLocation[random] != true && random != id)
            {
                mineLocation[random] = true
                mineReplaced = true
            }
        }
    }
    fun is_valid(id: Int): Boolean{                                                                     //Function to check if the ID is valid
        return(id>=0 && id<count)
    }

    fun is_mine(id: Int): Boolean {                                                                     // if the ID holds mines or not
        if(mineLocation[id])
            return true
        return false
    }

    fun edges(){                                                                                     //Function for storing the IDs of buttons at edges
        for(i in 0..row-1) {
            leftEdge[i] = left
            left+= col
            rightEdge[i] = right
            right+= col
        }

        bottom = leftEdge[row-1]

        for(i in 0..col-1){
            topEdge[i] = i
            bottomEdge[i] = bottom++
        }
    }

    fun place_mine_count(id: Int, count: Int){                                                           //Function for placing number images according to mines count at an ID
        val button = findViewById<Button>(id)
        when(count){
            0 -> button.setBackgroundResource(R.drawable.m_0)
            1 -> button.setBackgroundResource(R.drawable.m_1)
            2 -> button.setBackgroundResource(R.drawable.m_2)
            3 -> button.setBackgroundResource(R.drawable.m_3)
            4 -> button.setBackgroundResource(R.drawable.m_4)
            5 -> button.setBackgroundResource(R.drawable.m_5)
            6 -> button.setBackgroundResource(R.drawable.m_6)
            7 -> button.setBackgroundResource(R.drawable.m_7)
            8 -> button.setBackgroundResource(R.drawable.m_8)
        }
    }

    fun setFlag(id: Int){                                                                              // Function for plotting flags at an ID, and updating number of flag
        val button = findViewById<Button>(id)

        if(!isFlagged[id]){
            if(flagCount > 0) {
                flagCount--
                txv_flag_count.setText(flagCount.toString())
                button.setBackgroundResource(R.drawable.block_flagged)
                isFlagged[id] = true
            }
            else                                                                                           //If all flags have been used
                Snackbar.make(gameScreen, "No more Flags available", Snackbar.LENGTH_LONG).show()
        }
        else{
            flagCount++
            txv_flag_count.setText(flagCount.toString())
            button.setBackgroundResource(R.drawable.block_closed)
            isFlagged[id] = false
        }
    }

    fun end(gameLost: Boolean){                                                                     //Function for game ending
        count_up_timer.cancel()                                                                     // timer stops here

        for (i in 0 until row*col) {                                                                //Result part
            val button = findViewById<Button>(i)
            if (mineLocation[i]) {
                button.setBackgroundResource(R.drawable.mines)
            }
            button.isEnabled = false
        }

        if(gameLost)                                                                                   // messages for win or lose
            Snackbar.make(gameScreen, "YOU LOSE, try again !", Snackbar.LENGTH_INDEFINITE).show()
        else
            Snackbar.make(gameScreen, "You Won. Great Job!", Snackbar.LENGTH_INDEFINITE).show()
    }

    fun start_game(){
        plot_mines(row*col)
        edges()
        setupBoard()

        val restart = findViewById<Button>(R.id.restart)
        restart.setOnClickListener{                                                                    // onClickListener for restarting the game
            this.recreate()
        }
    }


}