package com.example.userssp

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.userssp.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity(),OnClickListener {

    private lateinit var userAdapter: UserAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //SharePreference
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val isFirstTime = preferences.getBoolean(getString(R.string.sp_first_time),true)
        if(isFirstTime){
            val dialogView = layoutInflater.inflate(R.layout.dialog_register,null)
            /*MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(
                    R.string.dialog_confirm
                ) { _, _ ->
                    val username = dialogView.findViewById<TextInputEditText>(R.id.etUsername)
                        .text.toString()
                    with(preferences.edit()){
                        putBoolean(getString(R.string.sp_first_time), false)
                        putString(getString(R.string.sp_username),username)
                            .apply()
                    }
                    Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                }
                .setNeutralButton(
                    R.string.dialog_refuse
                ){_, _ ->

                }
                .show()*/
            val dialog = MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_confirm) { _, _ -> }
                .setNeutralButton(R.string.dialog_refuse){_, _ -> }
                .create()

            dialog.show()

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val username = dialogView.findViewById<TextInputEditText>(R.id.etUsername).text.toString()
                if (username.isBlank()){
                    Toast.makeText(this, getString(R.string.register_invalid), Toast.LENGTH_SHORT).show()
                } else {
                    val username = dialogView.findViewById<TextInputEditText>(R.id.etUsername)
                        .text.toString()
                    with(preferences.edit()){
                        putBoolean(getString(R.string.sp_first_time), false)
                        putString(getString(R.string.sp_username),username)
                            .apply()
                    }
                    Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }else{
            val username = preferences.getString(
                getString(R.string.sp_username),
                getString(R.string.hint_username))
            Toast.makeText(this, "Bienvenido $username", Toast.LENGTH_SHORT).show()
        }


        userAdapter = UserAdapter(getUsers(),this)
        linearLayoutManager = LinearLayoutManager(this)

        binding.recyclerView.apply {
            setHasFixedSize(true)   //indica que las vistas tienen tamano definido
            layoutManager = linearLayoutManager
            adapter = userAdapter
        }
        val swipeHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                userAdapter.remove(viewHolder.adapterPosition)
            }
        })
        swipeHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun getUsers():MutableList<User>{
        val users = mutableListOf<User>()

        val alex = User(1,"Alex","Apaza","https://media-exp1.licdn.com/dms/image/C5603AQG08rNwB23MXg/profile-displayphoto-shrink_200_200/0/1638800097874?e=1645056000&v=beta&t=zY0s7L86toiwPc1j--o_G1YUFij5CWKc84V6Oyl6XfQ")
        val miguel = User(2,"Miguel","Human","data:image/jpeg;base64,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")
        val dantya = User(3,"Dantya","Asuncion","https://media-exp1.licdn.com/dms/image/C5603AQHEC398pPyXUw/profile-displayphoto-shrink_200_200/0/1608141510128?e=1645660800&v=beta&t=0zSkY1B6AOV0aV6Iwa1G7EXjss7oJDVSFp57SLLHn4A")
        val enrique = User(4,"Enrique","Caceres","https://upload.wikimedia.org/wikipedia/commons/6/69/Enrique_C%C3%A1ceres.jpg")

        //
        val alain = User(5, "Alain", "Nicolás", "https://frogames.es/wp-content/uploads/2020/09/alain-1.jpg")
        val samanta = User(6, "Samanta", "Meza", "https://pbs.twimg.com/profile_images/1400912361209438217/C8g3cVCV_400x400.jpg")
        val javier = User(7, "Javier", "Gómez", "https://upload.wikimedia.org/wikipedia/commons/6/63/Photographer_Javier_Gomez_in_New_York_City.jpg")
        val emma = User(8, "Emma", "Stone", "https://i0.wp.com/ellatinoonline.com/wp-content/uploads/2021/09/Screen-Shot-2021-09-16-at-3.45.29-PM.png?fit=1200%2C743&ssl=1")
        val tom = User(9, "Tom", "Holand", "https://static.wikia.nocookie.net/marvelcinematicuniverse/images/b/bd/Tom_Holland.png/revision/latest/top-crop/width/360/height/450?cb=20200724215614&path-prefix=es")
        //
        users.add(alex)
        users.add(miguel)
        users.add(dantya)
        users.add(enrique)
        users.add(alain)
        users.add(samanta)
        users.add(javier)
        users.add(emma)
        users.add(tom)
        return users
    }

    override fun onClick(user: User, position:Int) {
        Toast.makeText(this, "$position : ${user.getFullName()}", Toast.LENGTH_SHORT).show()
    }

}