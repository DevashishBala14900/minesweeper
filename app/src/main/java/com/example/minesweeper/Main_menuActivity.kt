package com.example.minesweeper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main_menu.*

var row: Int = 0                                                                                     //declaration of rows, columns and mines
var col: Int = 0
var mines: Int = 0
open class Main_menuActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        difficulty()
    }
    fun difficulty(){                                                                                 //function difficulty which sets values of rows, column and mines according to user selection
        val start = findViewById<Button>(R.id.start)
        val easy = findViewById<RadioButton>(R.id.easy)
        val normal = findViewById<RadioButton>(R.id.normal)
        val hard = findViewById<RadioButton>(R.id.hard)
        val custom = findViewById<RadioButton>(R.id.custom)
        val row_ET = findViewById<EditText>(R.id.edt_txv_rows)
        val col_ET = findViewById<EditText>(R.id.edt_txv_column)
        val mines_ET = findViewById<EditText>(R.id.edt_txv_mines)
        val intent = Intent(this,game_activity::class.java)
        val difficulties = findViewById<RadioGroup>(R.id.difficulties)
        difficulties.setOnCheckedChangeListener { difficulties , checkedId ->
            if(checkedId == R.id.custom){
                row_ET.setVisibility(View.VISIBLE)
                col_ET.setVisibility(View.VISIBLE)
                mines_ET.setVisibility(View.VISIBLE)
            }
            else{
                row_ET.setVisibility(View.INVISIBLE)
                col_ET.setVisibility(View.INVISIBLE)
                mines_ET.setVisibility(View.INVISIBLE)
            }
        }

        start.setOnClickListener {                                                                    // setOnClickListener to start activity with different conditions of difficulties
            if(easy.isChecked){
                row = 9
                col = 9
                mines = 10
                startActivity(intent)
            }
            else if(normal.isChecked){
                row = 16
                col = 16
                mines = 40
                startActivity(intent)
            }
            else if(hard.isChecked){
                row = 24
                col = 24
                mines = 99
                startActivity(intent)
            }
            else if(custom.isChecked){
                row = if(row_ET.getText().toString() != "") row_ET.getText().toString().toInt() else 0
                col = if(col_ET.getText().toString() != "") col_ET.getText().toString().toInt() else 0
                mines = if(mines_ET.getText().toString() != "") mines_ET.getText().toString().toInt() else -1

                if(row>0 && col>0 && row<=30 && col<=30 && mines>=0 && mines<row*col)              //Min value of rows and columns is 1, while mines is 0
                    startActivity(intent)
                else if(mines>=row*col && row!=0 && col != 0)
                    Snackbar.make(menuScreen, "number of mines can't be greater than or equal to rows X column", Snackbar.LENGTH_LONG).show()
                else
                    Snackbar.make(menuScreen, "try again", Snackbar.LENGTH_LONG).show()
            }

            else                                                                                                    //No option is chosen and start is pressed
                Snackbar.make(menuScreen, "Please select one of above options", Snackbar.LENGTH_LONG).show()


        }
    }
}