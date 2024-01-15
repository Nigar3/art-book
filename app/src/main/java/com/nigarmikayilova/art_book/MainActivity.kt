package com.nigarmikayilova.art_book

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.nigarmikayilova.art_book.databinding.ActivityMainBinding

/* Təqdim olunan kod Kotlin-də travel memories tətbiqidir.
O, istifadəçilərə ölkə adı, şəhər adı, il və sənət əsərinin şəkli daxil olmaqla müxtəlif sənət
əsərləri haqqında məlumat əlavə etməyə və onlara baxmaq imkanı verir.
Koda qalereyadan şəkil seçmək, sənət əsəri haqqında məlumatı SQLite verilənlər
bazasında saxlamaq və saxlanmış sənət əsərini əsas fəaliyyətdə göstərmək funksiyası daxildir.
  */




class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var travellist:ArrayList<travel>
    private lateinit var travellAdapter: TravelAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        //data burada cekilir
        travellist= ArrayList<travel>()


        val travellAdapter= TravelAdapter(travellist)
       binding.recyclerView.layoutManager=LinearLayoutManager(this)
        binding.recyclerView.adapter= travellAdapter




        //data burada elave olunur

        try {
            val database=this.openOrCreateDatabase("Travel", MODE_PRIVATE,null)
            val cursor=database.rawQuery("SELECT* FROM travel",null)
            val artNameIx=cursor.getColumnIndex("countryname")
            val id= cursor.getColumnIndex("id")

            while (cursor.moveToNext()){
                val name =cursor.getString(artNameIx)
                val iD=cursor.getInt(id)
                val travel=travel(name,iD)
                travellist.add(travel)
            }

            //deyisilen datalari elave etmek
            travellAdapter.notifyDataSetChanged()


            cursor.close()

        }catch (e:Exception){
            e.printStackTrace()
        }


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //inflate- menyu ile main activity baglamaq
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_art, menu)
        return true


    }

//menyudan artactivitye intent etmek
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //intent
        if (item.itemId == R.id.menu) {
            val intent = Intent(this@MainActivity, artActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    fun addtravel(view: View){
        val intent=Intent(this@MainActivity,artActivity::class.java)
        intent.putExtra("info","new")
        startActivity(intent)

    }


}