package com.example.kwallet

import ChildItem
import ParentItem
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.rv_term)
        val items = listOf(
            ParentItem(
                0, "Item 1", false, mutableListOf(
                    ChildItem(10, "Subitem 1.1"),
                    ChildItem(11, "Subitem 1.2"),
                    ChildItem(12, "Subitem 1.3")
                )
            ),
            ParentItem(
                1, "Item 2", false, mutableListOf(
                    ChildItem(20, "Subitem 2.1"),
                    ChildItem(21, "Subitem 2.2"),
                    ChildItem(22, "Subitem 2.3")
                )
            )
        )
        val adapter = ExpandableAdapter(items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}