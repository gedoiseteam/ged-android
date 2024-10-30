package com.upsaclay.gedoise.presentation.profile.support

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel

class SupportViewModel (
    @SuppressLint("StaticFieldLeak") var contexte: Context,
    var message: String,
    var object_message: String
) : ViewModel() {
    @SuppressLint("QueryPermissionsNeeded")
    fun contactSupport() {
        // Encodage des paramètres pour éviter les erreurs dans l'URI
        val encodedSubject = Uri.encode(object_message)
        val encodedBody = Uri.encode(message)


        // Correction de l'URI
        val uri = Uri.parse("mailto:application.ged@gmail.com?subject=$encodedSubject&body=$encodedBody")

        // Création de l'Intent
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = uri // Directement utiliser l'URI formée
        }

        // Vérifier si une application peut gérer l'intention
        if (emailIntent.resolveActivity(contexte.packageManager) != null) {
            contexte.startActivity(emailIntent) // Utilisation du contexte passé
        } else {
            // Gérer le cas où aucune application n'est disponible pour envoyer des e-mails
            Toast.makeText(contexte, "Aucune application d'e-mail disponible", Toast.LENGTH_SHORT).show()
        }
    }
}