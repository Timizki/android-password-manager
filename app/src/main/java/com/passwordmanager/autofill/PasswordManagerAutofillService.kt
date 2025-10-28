package com.passwordmanager.autofill

import android.app.assist.AssistStructure
import android.content.Intent
import android.os.CancellationSignal
import android.service.autofill.*
import android.util.Log
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.passwordmanager.R
import com.passwordmanager.domain.repository.PasswordProfileRepository
import com.passwordmanager.utils.PassToolGenerator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PasswordManagerAutofillService : AutofillService() {
    
    @Inject
    lateinit var repository: PasswordProfileRepository
    
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    
    companion object {
        private const val TAG = "AutofillService"
        const val EXTRA_PROFILE_ID = "profile_id"
        const val EXTRA_PASSPHRASE = "passphrase"
    }
    
    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        Log.d(TAG, "onFillRequest called")
        
        val structure = request.fillContexts.lastOrNull()?.structure
        if (structure == null) {
            Log.w(TAG, "No AssistStructure found")
            callback.onFailure("No structure found")
            return
        }
        
        val autofillFields = findAutofillFields(structure)
        if (autofillFields.isEmpty()) {
            Log.w(TAG, "No autofill fields found")
            callback.onFailure("No autofill fields found")
            return
        }
        
        Log.d(TAG, "Found ${autofillFields.size} autofill fields")
        
        serviceScope.launch {
            try {
                val profiles = repository.getAllProfiles()
                val packageName = structure.activityComponent.packageName
                
                // Etsi sopiva profiili package namen tai domain nimen perusteella
                val matchingProfiles = profiles.filter { profile ->
                    profile.website.contains(packageName, ignoreCase = true) ||
                    packageName.contains(profile.website, ignoreCase = true) ||
                    profile.title.contains(packageName, ignoreCase = true)
                }
                
                val profilesToShow = if (matchingProfiles.isNotEmpty()) {
                    matchingProfiles
                } else {
                    profiles.take(5) // Näytä max 5 profiilia
                }
                
                if (profilesToShow.isEmpty()) {
                    callback.onFailure("No profiles found")
                    return@launch
                }
                
                val responseBuilder = FillResponse.Builder()
                
                // Luo dataset jokaiselle profiilille
                profilesToShow.forEach { profile ->
                    val dataset = createDatasetForProfile(profile, autofillFields)
                    responseBuilder.addDataset(dataset)
                }
                
                callback.onSuccess(responseBuilder.build())
                
            } catch (e: Exception) {
                Log.e(TAG, "Error in onFillRequest", e)
                callback.onFailure("Error: ${e.message}")
            }
        }
    }
    
    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        Log.d(TAG, "onSaveRequest called - not implemented")
        callback.onFailure("Save not supported")
    }
    
    private fun findAutofillFields(structure: AssistStructure): List<AutofillField> {
        val fields = mutableListOf<AutofillField>()
        
        for (i in 0 until structure.windowNodeCount) {
            val windowNode = structure.getWindowNodeAt(i)
            findAutofillFieldsInNode(windowNode.rootViewNode, fields)
        }
        
        return fields
    }
    
    private fun findAutofillFieldsInNode(
        node: AssistStructure.ViewNode,
        fields: MutableList<AutofillField>
    ) {
        val autofillHints = node.autofillHints
        val inputType = node.inputType
        val hint = node.hint?.lowercase()
        val text = node.text?.toString()?.lowercase()
        
        // Tunnista käyttäjänimi-kentät
        if (autofillHints?.contains("username") == true ||
            autofillHints?.contains("emailAddress") == true ||
            hint?.contains("email") == true ||
            hint?.contains("username") == true ||
            hint?.contains("käyttäjä") == true ||
            text?.contains("email") == true ||
            text?.contains("username") == true) {
            
            fields.add(AutofillField(node.autofillId!!, AutofillFieldType.USERNAME))
            Log.d(TAG, "Found username field: ${node.autofillId}")
        }
        
        // Tunnista salasana-kentät
        if (autofillHints?.contains("password") == true ||
            (inputType and 0x00000080) != 0 || // TYPE_TEXT_VARIATION_PASSWORD
            (inputType and 0x00000010) != 0 || // TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            hint?.contains("password") == true ||
            hint?.contains("salasana") == true ||
            text?.contains("password") == true) {
            
            fields.add(AutofillField(node.autofillId!!, AutofillFieldType.PASSWORD))
            Log.d(TAG, "Found password field: ${node.autofillId}")
        }
        
        // Rekursiivisesti käy läpi lapsisolmut
        for (i in 0 until node.childCount) {
            findAutofillFieldsInNode(node.getChildAt(i), fields)
        }
    }
    
    private fun createDatasetForProfile(
        profile: com.passwordmanager.domain.model.PasswordProfile,
        autofillFields: List<AutofillField>
    ): Dataset {
        val datasetBuilder = Dataset.Builder()
        
        // Luo RemoteViews dataset-esitykselle
        val presentation = RemoteViews(packageName, R.layout.autofill_dataset_item)
        presentation.setTextViewText(R.id.title, profile.title)
        presentation.setTextViewText(R.id.subtitle, profile.website.ifEmpty { "Ei sivustoa" })
        
        // Luo Intent profiilin valitsemiseksi
        val intent = Intent(this, AutofillActivity::class.java).apply {
            putExtra(EXTRA_PROFILE_ID, profile.id)
            // Lisää autofill-kentien tiedot intentiin
            putExtra("autofill_fields", autofillFields.map { "${it.autofillId}:${it.type}" }.toTypedArray())
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val intentSender = android.app.PendingIntent.getActivity(
            this, 
            profile.id.toInt(),
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        ).intentSender
        
        // Aseta authentication koko datasetille
        autofillFields.forEach { field ->
            datasetBuilder.setValue(
                field.autofillId,
                AutofillValue.forText("••••••••"),
                presentation
            )
        }
        
        datasetBuilder.setAuthentication(intentSender)
        
        return datasetBuilder.build()
    }
}

data class AutofillField(
    val autofillId: AutofillId,
    val type: AutofillFieldType
)

enum class AutofillFieldType {
    USERNAME,
    PASSWORD
}