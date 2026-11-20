package com.example.contactsapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var listContacts: ListView
    private lateinit var btnAddContact: ImageView

    private val contacts = mutableListOf(
        Contact("Dupont", "Claire", "06 12 34 56 78", "claire.dupont@example.com"),
        Contact("Martin", "Thomas", "07 23 45 67 89", "thomas.martin@example.com"),
        Contact("Bernard", "Sophie", "06 98 76 54 32", "sophie.bernard@example.com"),
        Contact("Petit", "Lucas", "07 11 22 33 44", "lucas.petit@example.com"),
        Contact("Moreau", "Julien", "06 55 44 33 22", "julien.moreau@example.com")
    )

    private lateinit var adapter: ContactAdapter

    // Recevoir le résultat de DetailActivity
    private val detailLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK && result.data != null) {

                val data = result.data!!
                val action = data.getStringExtra("action")
                val position = data.getIntExtra("index", -1)

                if (action == "DELETE") {
                    if (position >= 0 && position < contacts.size) {
                        contacts.removeAt(position)
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "Contact supprimé", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val newContact = Contact(
                        data.getStringExtra("nom") ?: "",
                        data.getStringExtra("prenom") ?: "",
                        data.getStringExtra("telephone") ?: "",
                        data.getStringExtra("email") ?: "",
                        data.getStringExtra("photo")
                    )

                    if (position >= 0 && position < contacts.size) {
                        contacts[position] = newContact
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "Contact modifié avec succès", Toast.LENGTH_SHORT).show()
                    } else {
                        contacts.add(newContact)
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "Contact ajouté avec succès", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listContacts = findViewById(R.id.listContacts)
        btnAddContact = findViewById(R.id.btnAddContact)

        adapter = ContactAdapter(this, contacts)
        listContacts.adapter = adapter

        // Bouton ajouter (+ dans l'en-tête)
        btnAddContact.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("index", -1)
            detailLauncher.launch(intent)
        }

        // Sélectionner un contact
        listContacts.setOnItemClickListener { _, _, position, _ ->
            val contact = contacts[position]
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("nom", contact.nom)
            intent.putExtra("prenom", contact.prenom)
            intent.putExtra("telephone", contact.telephone)
            intent.putExtra("email", contact.email)
            intent.putExtra("photo", contact.photoUri)
            detailLauncher.launch(intent)
        }
    }
}
