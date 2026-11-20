package com.example.contactsapp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ContactAdapter(
    context: Context,
    private val contacts: MutableList<Contact>
) : ArrayAdapter<Contact>(context, 0, contacts) {

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_contact, parent, false)

        val contact = contacts[position]

        val img = view.findViewById<ImageView>(R.id.imgContact)
        val nom = view.findViewById<TextView>(R.id.txtNom)
        val tel = view.findViewById<TextView>(R.id.txtTelephone)

        nom.text = "${contact.nom} ${contact.prenom}".trim()
        tel.text = contact.telephone

        img.setImageResource(R.drawable.ic_person)

        if (!contact.photoUri.isNullOrEmpty()) {
            try {
                img.setImageURI(Uri.parse(contact.photoUri))
            } catch (e: Exception) {
                img.setImageResource(R.drawable.ic_person)
            }
        }

        return view
    }
}
