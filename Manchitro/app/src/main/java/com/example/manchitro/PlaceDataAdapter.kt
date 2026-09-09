package com.example.manchitro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.manchitro.json.PlaceDataItem

class PlaceDataAdapter(private val onClick: (PlaceDataItem) -> Unit) :
    RecyclerView.Adapter<PlaceDataAdapter.ViewHolder>() {

    private var dataList = listOf<PlaceDataItem>()

    fun setData(newData: List<PlaceDataItem>) {
        dataList = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place_data, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val placeDataItem = dataList[position]
        holder.bind(placeDataItem, onClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.tv_id)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvLat: TextView = itemView.findViewById(R.id.tv_lat)
        private val tvLon: TextView = itemView.findViewById(R.id.tv_lon)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val btnDetail: Button = itemView.findViewById(R.id.btn_detail)

        fun bind(placeDataItem: PlaceDataItem, onClick: (PlaceDataItem) -> Unit) {
            tvId.text = placeDataItem.id.toString()
            tvTitle.text = placeDataItem.title
            tvLat.text = placeDataItem.lat.toString()
            tvLon.text = placeDataItem.lon.toString()
            Glide.with(itemView.context).load("https://labs.anontech.info/cse489/t3/" + placeDataItem.image).into(imageView)

            btnDetail.setOnClickListener {
                onClick(placeDataItem)
            }
        }
    }
}
