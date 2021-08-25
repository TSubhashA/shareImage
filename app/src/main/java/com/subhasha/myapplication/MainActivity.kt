package com.subhasha.myapplication


import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.os.StrictMode.VmPolicy
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import com.subhasha.myapplication.api.ApiInterface
import com.subhasha.myapplication.data.ReturnData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var mPager: ViewPager? = null

    private var tabLayout: TabLayout? = null

    private val ImagesArray = ArrayList<String>()

    private val STORAGE_PERMISSION_CODE = 101

    private val apiInterface = ApiInterface.create().getData()
    lateinit var progressBar: ProgressBar
    lateinit var scroll: ScrollView
    lateinit var bAppBar: BottomAppBar

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            STORAGE_PERMISSION_CODE
        )
        checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            STORAGE_PERMISSION_CODE
        )

        mPager = findViewById<View>(R.id.pager) as ViewPager
        val adapter = SlidingImage_Adapter(this@MainActivity, ImagesArray)

        mPager!!.setAdapter(adapter)

        tabLayout = findViewById(R.id.tab_layout) as TabLayout

        tabLayout!!.setupWithViewPager(mPager, true)

        tabLayout!!.setupWithViewPager(mPager)

        progressBar = findViewById(R.id.progressbar) as ProgressBar
        scroll = findViewById(R.id.scroll) as ScrollView
        bAppBar = findViewById(R.id.bottomAppBar) as BottomAppBar

        val productName = findViewById(R.id.product_name) as TextView
        val regularPrice = findViewById(R.id.mrp_price) as TextView
        val actualPrice = findViewById(R.id.thePriceOfProduct) as TextView
        val description = findViewById(R.id.product_description) as TextView
        val varientSize = findViewById(R.id.varient_size) as TextView
        val varientWeight = findViewById(R.id.varient_weight) as TextView
        val brand = findViewById(R.id.brand) as TextView
        val itemCode = findViewById(R.id.item_code) as TextView
        val btmNavigation: BottomNavigationView = findViewById(R.id.btm)
        val btn: Button = findViewById(R.id.btn)

        btn.setOnClickListener {
            Toast.makeText(this, "Producted Added to cart", LENGTH_LONG).show()
        }

        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.item_share -> {
                        // put your code here
                        share(ImagesArray)
                        return@OnNavigationItemSelectedListener true
                    }

                }
                false
            }



        btmNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        apiInterface.enqueue(object : Callback<ReturnData> {
            override fun onResponse(call: Call<ReturnData>, response: Response<ReturnData>) {
//                Toast.makeText(this@MainActivity,"SUCCESS", LENGTH_SHORT).show()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val rd = response.body()
                        val baseImageUrl = rd?.image_base_path
                        ImagesArray.add(baseImageUrl + rd?.data!!.product_image)

                        productName.text = rd.data.product_title
                        regularPrice.text =
                            "MRP" + "\u20B9" + rd.data.selected_variant.variant_mrp_price
                        actualPrice.text = "\u20B9" + rd.data.selected_variant.variant_regular_price
                        description.text = Html.fromHtml(rd.data.product_description)
                        varientSize.text = Html.fromHtml(rd.data.selected_variant.variant_size)
                        varientWeight.text = Html.fromHtml(rd.data.selected_variant.variant_weight)
                        brand.text = Html.fromHtml(rd.data.brand_name)
                        itemCode.text = Html.fromHtml(rd.data.product_code)

                        regularPrice.setPaintFlags(regularPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)


//                        Toast.makeText(this@MainActivity, "SUCCESS  123" + response, LENGTH_LONG)
//                            .show()
                        val iterator = rd.data.product_gallery.iterator()

                        iterator.forEach { it ->
                            ImagesArray.add(baseImageUrl + it.product_image)
                            Log.e("URL ", it.toString())
                        }
//                        Log.e("URL", ImagesArray.get(0))
                        adapter.notifyDataSetChanged()

                        progressBar.visibility = View.GONE
                        scroll.visibility = View.VISIBLE
                        bAppBar.visibility = View.VISIBLE

                    }
                }
            }

            override fun onFailure(call: Call<ReturnData>, t: Throwable) {

                Toast.makeText(this@MainActivity, "Failed", LENGTH_LONG).show()

            }
        })

    }

    fun share(urls: ArrayList<String>?) {
        if (urls != null) {
            Log.e(TAG, "size : " + urls.size)
        }
        val listOfImageUri = ArrayList<Uri>()
        progressBar.visibility = View.VISIBLE
        scroll.visibility = View.GONE
        bAppBar.visibility = View.GONE

        val mainHandler = Handler(getMainLooper())
        val runnable: Runnable = object : Runnable {
            override fun run() {
                //Do your work here !!

                if (urls != null) {
                    for (i in urls.indices) {
                        Picasso.with(this@MainActivity).setLoggingEnabled(true)
                        Picasso.with(this@MainActivity).load(urls[i])
                            .resize(300, 300)
                            .onlyScaleDown()
                            .into(object : Target {
                                override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom?) {
                                    try {
                                        val mydir =
                                            File(
                                                Environment.getExternalStorageDirectory()
                                                    .toString() + "/cart1"
                                            )


                                        if (!mydir.exists()) {
                                            mydir.mkdirs()
                                        }
                                        val fileUri =
                                            mydir.getAbsolutePath() + File.separator + System.currentTimeMillis()
                                                .toString() + i + ".png"

                                        val outputStream = FileOutputStream(fileUri)
                                        bitmap.compress(
                                            Bitmap.CompressFormat.PNG,
                                            100,
                                            outputStream
                                        )
                                        //                        listOfImageUri.add(Uri.parse(MediaStore.Images.Media.insertImage(
                                        //                            application.contentResolver, bitmap, "", null)))

                                        outputStream.flush()
                                        outputStream.close()
                                        //                        listOfImageUri.add(Uri.parse(MediaStore.Images.Media.insertImage(applicationContext.getContentResolver(), BitmapFactory.decodeFile(fileUri),null,null)))
                                        listOfImageUri.add(Uri.fromFile(File(fileUri)))

                                        Log.e(TAG, "Imag URI " + listOfImageUri[i])
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                    //                    Toast.makeText(applicationContext, "Image Downloaded", LENGTH_LONG).show()
                                }

                                override fun onBitmapFailed(errorDrawable: Drawable?) {}
                                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                            })
                    }

                }
                shareStart(listOfImageUri)
            }
        }

        mainHandler.postDelayed(runnable, 200)


    }

    private fun shareStart(listOfImageUri: ArrayList<Uri>) {

        progressBar.visibility = View.GONE
        scroll.visibility = View.VISIBLE
        bAppBar.visibility = View.VISIBLE

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, listOfImageUri)
            type = "image/*"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        try {
            startActivity(Intent.createChooser(shareIntent, "Share Via:"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show()
        }


    }


    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        }
        //        else {
////            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT)
////                .show()
//        }
    }


}

