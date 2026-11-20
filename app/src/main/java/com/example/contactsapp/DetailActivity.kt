package com.example.contactsapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.FileOutputStream

class DetailActivity : AppCompatActivity() {

    private lateinit var editNom: EditText
    private lateinit var editPrenom: EditText
    private lateinit var editTel: EditText
    private lateinit var editEmail: EditText
    private lateinit var imgPhoto: ImageView
    private lateinit var txtDetailTitle: TextView
    private lateinit var btnDeleteContact: ImageView

    private var photoUri: String? = null
    private var contactIndex = -1

    // Sélectionner une photo dans la galerie
    private val galerieLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                photoUri = uri.toString()
                imgPhoto.setImageURI(uri)
            }
        }

    // Prendre une photo avec la caméra
    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap: Bitmap? ->
            if (bitmap != null) {
                try {
                    val file = File(filesDir, "photo_${System.currentTimeMillis()}.jpg")
                    FileOutputStream(file).use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
                    }
                    photoUri = Uri.fromFile(file).toString()
                    imgPhoto.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Toast.makeText(this, "Erreur d'enregistrement", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Aucune photo prise", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Header controls
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        txtDetailTitle = findViewById(R.id.txtDetailTitle)
        btnDeleteContact = findViewById(R.id.btnDeleteContact)

        // Récupérer les composants du formulaire
        editNom = findViewById(R.id.editNom)
        editPrenom = findViewById(R.id.editPrenom)
        editTel = findViewById(R.id.editTelephone)
        editEmail = findViewById(R.id.editEmail)
        imgPhoto = findViewById(R.id.imgDetail)

        // Récupérer les données envoyées
        contactIndex = intent.getIntExtra("index", -1)

        if (contactIndex == -1) {
            txtDetailTitle.text = "Nouveau contact"
            btnDeleteContact.visibility = View.GONE
        } else {
            txtDetailTitle.text = "Détail du contact"
            btnDeleteContact.visibility = View.VISIBLE
        }

        editNom.setText(intent.getStringExtra("nom"))
        editPrenom.setText(intent.getStringExtra("prenom"))
        editTel.setText(intent.getStringExtra("telephone"))
        editEmail.setText(intent.getStringExtra("email"))

        photoUri = intent.getStringExtra("photo")
        afficherPhoto()

        // Badge caméra sur la photo
        findViewById<FrameLayout>(R.id.btnBadgeCamera)?.setOnClickListener {
            galerieLauncher.launch("image/*")
        }

        // Click sur l'image elle-même
        imgPhoto.setOnClickListener {
            galerieLauncher.launch("image/*")
        }

        // Action supprimer
        btnDeleteContact.setOnClickListener {
            if (contactIndex != -1) {
                val resultIntent = Intent()
                resultIntent.putExtra("action", "DELETE")
                resultIntent.putExtra("index", contactIndex)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        // Bouton galerie
        findViewById<MaterialButton>(R.id.btnGalerie).setOnClickListener {
            galerieLauncher.launch("image/*")
        }

        // Bouton caméra
        findViewById<MaterialButton>(R.id.btnCamera).setOnClickListener {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(this, "Caméra indisponible", Toast.LENGTH_SHORT).show()
            }
        }

        // Bouton appeler
        findViewById<MaterialButton>(R.id.btnAppeler).setOnClickListener {
            appeler()
        }

        // Bouton e-mail
        findViewById<MaterialButton>(R.id.btnEmail).setOnClickListener {
            envoyerEmail()
        }

        // Bouton enregistrer
        findViewById<MaterialButton>(R.id.btnEnregistrer).setOnClickListener {
            enregistrer()
        }
    }

    private fun afficherPhoto() {
        imgPhoto.setImageResource(R.drawable.ic_person)
        if (!photoUri.isNullOrEmpty()) {
            try {
                imgPhoto.setImageURI(Uri.parse(photoUri))
            } catch (e: Exception) {
                imgPhoto.setImageResource(R.drawable.ic_person)
            }
        }
    }

    private fun appeler() {
        val numero = editTel.text.toString().trim()
        if (numero.isEmpty()) {
            editTel.error = "Téléphone obligatoire"
            return
        }
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero"))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Application téléphonique indisponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun envoyerEmail() {
        val email = editEmail.text.toString().trim()
        if (email.isEmpty()) {
            editEmail.error = "E-mail obligatoire"
            return
        }
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Application e-mail indisponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enregistrer() {
        val nom = editNom.text.toString().trim()
        val prenom = editPrenom.text.toString().trim()
        val tel = editTel.text.toString().trim()
        val email = editEmail.text.toString().trim()

        if (nom.isEmpty()) {
            editNom.error = "Nom obligatoire"
            return
        }
        if (prenom.isEmpty()) {
            editPrenom.error = "Prénom obligatoire"
            return
        }
        if (tel.isEmpty()) {
            editTel.error = "Téléphone obligatoire"
            return
        }

        val resultIntent = Intent()
        resultIntent.putExtra("action", "SAVE")
        resultIntent.putExtra("index", contactIndex)
        resultIntent.putExtra("nom", nom)
        resultIntent.putExtra("prenom", prenom)
        resultIntent.putExtra("telephone", tel)
        resultIntent.putExtra("email", email)
        resultIntent.putExtra("photo", photoUri)

        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
