package com.nigarmikayilova.art_book

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.nigarmikayilova.art_book.databinding.ActivityArtBinding
import java.io.ByteArrayOutputStream


class artActivity : AppCompatActivity() {



    private lateinit var binding:ActivityArtBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap: Bitmap?=null
    private lateinit var dataBase:SQLiteDatabase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityArtBinding.inflate(layoutInflater)
        setContentView(binding.root)



        dataBase=this.openOrCreateDatabase("Travel", MODE_PRIVATE,null)

        registerLauncher()



        val intent=intent
        val info=intent.getStringExtra("info")
        if(info.equals("new")){
            binding.countryName.setText("")
            binding.cityName.setText("")
            binding.year.setText("")
            binding.button.visibility=View.VISIBLE
        }else{
            binding.button.visibility=View.INVISIBLE
            val selectedId=intent.getIntExtra("id",1)

            val cursor=dataBase.rawQuery("SELECT*FROM travel WHERE id=?", arrayOf(selectedId.toString()))

            val countrynameIx=cursor.getColumnIndex("countryname")
            val ciynameIx=cursor.getColumnIndex("cityname")
            val year=cursor.getColumnIndex("year")
            val imageIx=cursor.getColumnIndex("image")

            while (cursor.moveToNext()){
                binding.countryName.setText(cursor.getString(countrynameIx))
                binding.cityName.setText(cursor.getString(ciynameIx))
                binding.year.setText(cursor.getString(year))


                val byteArray=cursor.getBlob(imageIx)
                val bitmap=BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.imageView2.setImageBitmap(bitmap)
            }
            cursor.close()
        }

    }


    fun selectImage(view: View){
        //API 33+ olarsa bu control elave edilir ki eger 33+ ise if deyilse else islesin-> manifest faylinda da uses_permissiona READ_MEDIA_IMAGES elave edilir

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            //Android 33+ ->READ_MEDIA_IMAGES
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)!=PackageManager.PERMISSION_GRANTED){
                //rational
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", View.OnClickListener {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()
                }else{
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)



                }}else{
                val intentToGallery= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }
        }else{
            //Android32- -> READ_EXTERNAL_STORAGE
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                //rational
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", View.OnClickListener {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                }else{
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)



                }}else{
                val intentToGallery= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }
            }



    }

    fun saveimage(view:View){

        val countryName=binding.countryName.text.toString()
        val cityName=binding.cityName.text.toString()
        val year=binding.year.text.toString()

        if (selectedBitmap!=null){

            val smallBitmap=makeSmallerBitmap(selectedBitmap!!,300)

            //sekli dataya cevirmek ucun
            val outPutStream=ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outPutStream)
            val byteArray=outPutStream.toByteArray()

            try {

                //database yaradaraq oraya daxil edilen datalari save edek
               // val dataBase=this.openOrCreateDatabase("Travel", MODE_PRIVATE,null)
                dataBase.execSQL("CREATE TABLE IF NOT EXISTS travel(id INTEGER PRIMARY KEY, countryname VARCHAR, cityname VARCHAR, year VARCHAR, image BLOB)")


                //her bir daxil edilen datani ? ile elaqelendirek
                val sqlString="INSERT INTO travel(countryname,cityname,year,image) VALUES(?,?,?,?)"
                val statement=dataBase.compileStatement(sqlString)
                statement.bindString(1,countryName)
                statement.bindString(2,cityName)
                statement.bindString(3,year)
                statement.bindBlob(4,byteArray)
                statement.execute()

            }catch (e:Exception){
                e.printStackTrace()

            }


            val intent=Intent(this@artActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)


        }




    }

    //image kiciltmek ucun fun
    private fun makeSmallerBitmap(image:Bitmap, maximumSize:Int):Bitmap{
        var width=image.width
        var height=image.height
        val bitmapRatio:Double= width.toDouble() / height.toDouble()
        if(bitmapRatio>1){
            //landsape
            width=maximumSize
            val scaleHeight=width/bitmapRatio
            height=scaleHeight.toInt()
        }else{
            //portrait
            height=maximumSize
            val scaleWidth=height*bitmapRatio
            width=scaleWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height , true)

    }



//qaleriden sekil cekmek ucun funksiya
    private fun registerLauncher() {
        activityResultLauncher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    println("ok")
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageData=intentFromResult.data
                        //binding.imageView2.setImageURI(imageData)

                       if (imageData!=null){
                           println("OK")
                        try{
                            if (Build.VERSION.SDK_INT>=28) { //SDK yoxlamaq
                                val source = ImageDecoder.createSource(this@artActivity.contentResolver,imageData)
                                selectedBitmap=ImageDecoder.decodeBitmap(source)
                                binding.imageView2.setImageBitmap(selectedBitmap) //secilen seklin imageviewda gosterilmesi

                            }else{
                                selectedBitmap=MediaStore.Images.Media.getBitmap(contentResolver,imageData)
                                binding.imageView2.setImageBitmap(selectedBitmap)
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                       }

                    }
                }
            }


    permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
        if(result){
            //permission granted
            val intentToGallery= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }else{
            //permission denied
            Toast.makeText(this@artActivity,"Permission needed!",Toast.LENGTH_LONG).show()


        }
    }







    }
}