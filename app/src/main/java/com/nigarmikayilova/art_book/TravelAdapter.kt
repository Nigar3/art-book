package com.nigarmikayilova.art_book

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nigarmikayilova.art_book.databinding.RecyclerrawBinding

class TravelAdapter(val travellist:ArrayList<travel>): RecyclerView.Adapter<TravelAdapter.TravelHolder>() {
    class TravelHolder(val binding: RecyclerrawBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelHolder {
    val binding=RecyclerrawBinding.inflate(LayoutInflater.from(parent.context),parent,false)

    return TravelHolder(binding)
    }

    override fun getItemCount(): Int {
        return travellist.size
    }

    override fun onBindViewHolder(holder: TravelHolder, position: Int) {
        holder.binding.textView2.text=travellist.get(position).name
        holder.itemView.setOnClickListener{
            val intent=Intent(holder.itemView.context,artActivity::class.java)
            intent.putExtra("info","old")
            intent.putExtra("id",travellist.get(position).id)
            holder.itemView.context.startActivity(intent)
        }

    }
}