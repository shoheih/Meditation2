package net.minpro.meditation2.view.dialog

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import net.minpro.meditation2.MyApplication
import net.minpro.meditation2.R
import net.minpro.meditation2.viewmodel.MainViewModel

class ThemeSelectDialog: DialogFragment() {

    val appContext = MyApplication.appContext
    private  val themeList = MyApplication.themeList
    private lateinit var viewModel: MainViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        val recyclerView = RecyclerView(appContext)
        with(recyclerView) {
            layoutManager = GridLayoutManager(appContext, 2)
            adapter = ThemeSelectAdapter(themeList,viewModel)
        }

        val dialog = AlertDialog.Builder(activity!!).apply {
            setTitle(R.string.select_theme)
            setView(recyclerView)
        }.create()

        viewModel.txtTheme.observe(activity!!, Observer {
            dialog.dismiss()
        })

        return dialog
    }
}