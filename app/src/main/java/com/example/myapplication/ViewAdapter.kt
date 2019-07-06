package org.esiea.cam_gidon.kotlineval

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.foursquare.R
import layout.place

class MyAdapter(private val myDataset: List<place>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(val item: View) : RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        // create a new view
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(itemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val ven: place = myDataset[position]

        // Read object
        val name = holder.itemView.findViewById(R.id.name) as TextView
        val address = holder.itemView.findViewById(R.id.address) as TextView
        val distance = holder.itemView.findViewById(R.id.distance) as TextView
        val rate = holder.itemView.findViewById(R.id.rate) as TextView

        // Fill textView
        name.text = myDataset[position].name
        address.text = myDataset[position].location.address + " " + myDataset[position].location.city + " " + myDataset[position].location.postalCode + ", " + myDataset[position].location.country
        distance.text = myDataset[position].distance +"km"
        rate.text = myDataset[position].rate.toString() +"/10"

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}