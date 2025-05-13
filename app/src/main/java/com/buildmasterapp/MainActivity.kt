package com.buildmasterapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buildmasterapp.catalogue.data.api.RetrofitClient
import com.buildmasterapp.catalogue.domain.model.Component
import com.buildmasterapp.catalogue.presentation.ComponentAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ComponentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.componentRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        RetrofitClient.instance.getAllComponents().enqueue(object : Callback<List<Component>> {
            override fun onResponse(call: Call<List<Component>>, response: Response<List<Component>>) {
                if (response.isSuccessful) {
                    val components = response.body() ?: emptyList()
                    adapter = ComponentAdapter(components)
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<Component>>, t: Throwable) {
                Log.e("API", "Error: ${t.message}")
            }
        })
    }
}
