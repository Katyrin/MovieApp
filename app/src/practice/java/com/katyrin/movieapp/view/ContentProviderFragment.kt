package com.katyrin.movieapp.view

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.katyrin.movieapp.R
import com.katyrin.movieapp.databinding.ContentProviderFragmentBinding
import com.katyrin.movieapp.view.MainActivity.Companion.REQUEST_CODE_READ_CONTACTS

class ContentProviderFragment: Fragment() {
    private var _binding: ContentProviderFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ContentProviderFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkContactsPermission()
    }

    private fun checkContactsPermission() {
        context?.let {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(it, Manifest.permission.READ_CONTACTS) -> getContacts()
                else -> requestContactsPermission()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            REQUEST_CODE_READ_CONTACTS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    getContacts()
                else {
                    context?.let {
                        AlertDialog.Builder(it)
                            .setTitle(getString(R.string.access_to_contacts))
                            .setMessage(getString(R.string.explanation))
                            .setPositiveButton(getString(R.string.grant_access)) { _, _ ->
                                requestContactsPermission()
                            }
                            .setNegativeButton(getString(R.string.do_not)) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    }
                }
                return
            }
        }
    }

    private fun getContacts() {
        context?.let {
            val contentResolver: ContentResolver = it.contentResolver
            val cursorWithContacts: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            cursorWithContacts?.let { cursor ->
                for (i in 0..cursor.count) {
                    if (cursor.moveToPosition(i)) {
                        val name =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        addView(it, name)
                    }
                }
            }
            cursorWithContacts?.close()
        }
    }

    private fun addView(context: Context, textToShow: String) {
        binding.containerForContacts.addView(AppCompatTextView(context).apply {
            text = textToShow
            textSize = resources.getDimension(R.dimen.main_container_text_size)
            setOnLongClickListener {
                checkCallPermission(it.context, text.toString())
                return@setOnLongClickListener true
            }
        })
    }

    private fun checkCallPermission(context: Context, phoneNumber: String) {
        context.let {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(it, Manifest.permission.CALL_PHONE) ->
                    openCallRequestAlertDialog(context, phoneNumber)
                else -> requestCallPermission()
            }
        }
    }

    private fun openCallRequestAlertDialog( context: Context, phoneNumber: String) {
        AlertDialog.Builder(context)
            .setTitle(phoneNumber)
            .setMessage(getString(R.string.want_call_number))
            .setPositiveButton(getString(R.string.call_number)) { _, _ ->
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber")))
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun requestContactsPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)
    }

    private fun requestCallPermission() {
        requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CODE_CALL_PHONE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ContentProviderFragment()
        const val REQUEST_CODE_CALL_PHONE = 54
    }
}