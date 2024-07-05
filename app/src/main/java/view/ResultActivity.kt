package view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import controller.ScoreListAdapter
import se.umu.cs.seod0005.thirty.R

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val scoreArray = intent.getIntArrayExtra("scoreArray")!!
        Log.d("scoreArray", scoreArray.toString())

        val scoreLabels = resources.getStringArray(R.array.combinations)
        Log.d("scoreLabels", "size: " + scoreLabels.size.toString())

        val scoreListView = findViewById<ListView>(R.id.scoreListView)
        val adapter = ScoreListAdapter(this, scoreLabels.toList(), scoreArray.toList())
        scoreListView.adapter = adapter

        val totalScore = scoreArray.sum()
        val totalScoreLabel = findViewById<TextView>(R.id.totalScore)
        totalScoreLabel.text = totalScore.toString()

        // Restarts the game.
        val tryAgainButton = findViewById<Button>(R.id.tryAgainButton)
        tryAgainButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }
}
