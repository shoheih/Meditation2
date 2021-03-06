package net.minpro.meditation2.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import net.minpro.meditation2.R
import net.minpro.meditation2.viewmodel.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class LevelSelectDialog: DialogFragment() {

    var selectedItemId = 0

    //ateinit var viewModel: MainViewModel
    private val viewModel: MainViewModel by sharedViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = AlertDialog.Builder(activity!!).apply {
            setTitle(R.string.select_level)
            setSingleChoiceItems(R.array.level_list, selectedItemId) { dialog, which ->
                selectedItemId = which
                viewModel.setLevel(selectedItemId)
                dialog.dismiss()
            }
        }.create()
        return dialog
    }
}