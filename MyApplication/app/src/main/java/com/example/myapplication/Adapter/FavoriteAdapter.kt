package com.example.myapplication.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myapplication.Activities.DetailActivity
import com.example.myapplication.Activities.FavoriteActivity
import com.example.myapplication.Domain.Foods
import com.example.myapplication.Helpers.ManagmentFavorite
import com.example.myapplication.Helpers.TinyDB
import com.example.myapplication.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FavoriteAdapter(var list: ArrayList<Foods?>, context: FavoriteActivity) :
    RecyclerView.Adapter<FavoriteAdapter.viewholder>() {
    private val getItemCount = 0
    private val managmentFavorite: ManagmentFavorite
    var context: Context
    private val tinyDB: TinyDB

    init {
        this.context = context
        tinyDB = TinyDB(context)
        managmentFavorite = ManagmentFavorite(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        context = parent.context
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.viewholder_favorite, parent, false)
        return viewholder(view)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.title.text = list[position]!!.Title
        holder.timeTxt.text = list[position]!!.TimeValue.toString() + " min"
        holder.rateTxt.text = " " + list[position]!!.Star
        holder.priceTxt.text = "$" + list[position]!!.Price
        Glide.with(context)
            .load(list[position]!!.ImagePath)
            .transform(CenterCrop(), RoundedCorners(30))
            .into(holder.pic)
        var pos = holder.adapterPosition
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("object", list[holder.adapterPosition])
            context.startActivities(arrayOf(intent))
        }
        holder.removeBtn.setOnClickListener {
            var pos = holder.getAdapterPosition()
            val removedItem = list[pos]!!
            list.removeAt(pos)
            notifyItemRemoved(pos)
            val sharedPreferences =
                context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("email", "")
            if (!userEmail!!.isEmpty()) {
                val favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(
                    userEmail!!.replace(".", "_")
                )
                favoritesRef.orderByChild("Title").equalTo(removedItem.Title)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (snapshot in dataSnapshot.getChildren()) {
                                    val key = snapshot.key!!
                                    favoritesRef.child(key!!).removeValue()
                                    Toast.makeText(
                                        context,
                                        "Removed from Favorites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            //Handle Errors
                        }
                    })
            } else {
                Toast.makeText(context, "User is not logged in", Toast.LENGTH_SHORT).show()
            }
            if (pos >= itemCount) {
                pos = itemCount - 1
            }
            Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show()
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var timeTxt: TextView
        var rateTxt: TextView
        var priceTxt: TextView
        var removeBtn: Button
        var pic: ImageView

        init {
            title = itemView.findViewById<TextView>(R.id.titleTxt)
            timeTxt = itemView.findViewById<TextView>(R.id.timeTxt)
            rateTxt = itemView.findViewById<TextView>(R.id.rateTxt)
            priceTxt = itemView.findViewById<TextView>(R.id.priceTxt)
            removeBtn = itemView.findViewById<Button>(R.id.removeBtn)
            pic = itemView.findViewById<ImageView>(R.id.pic)
        }
    }
}
