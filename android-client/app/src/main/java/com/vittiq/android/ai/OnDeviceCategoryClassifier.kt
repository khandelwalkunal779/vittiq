package com.vittiq.android.ai

import android.content.Context
import android.util.Log

interface OnDeviceCategoryClassifier {
    suspend fun isAvailable(): Boolean
    suspend fun classifyCategory(title: String, availableCategories: List<String>): String?
}

class GoogleAiCoreCategoryClassifier(
    private val context: Context? = null
) : OnDeviceCategoryClassifier {

    companion object {
        private const val TAG = "OnDeviceClassifier"
        private const val AICORE_PACKAGE = "com.google.android.aicore"
    }

    override suspend fun isAvailable(): Boolean {
        val ctx = context ?: return false
        return try {
            val pm = ctx.packageManager
            val info = pm.getPackageInfo(AICORE_PACKAGE, 0)
            info.applicationInfo?.enabled == true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun classifyCategory(title: String, availableCategories: List<String>): String? {
        val trimmed = title.trim()
        if (trimmed.isEmpty() || availableCategories.isEmpty()) return null

        if (!isAvailable()) {
            Log.d(TAG, "Google AICore is not available on this device; skipping AI category classification.")
            return null
        }

        val prompt = buildPrompt(trimmed, availableCategories)

        return try {
            val rawOutput = queryAiCoreModel(prompt) ?: return null
            matchCategory(rawOutput, availableCategories)
        } catch (e: Exception) {
            Log.w(TAG, "AICore classification failed: ${e.message}")
            null
        }
    }

    fun buildPrompt(title: String, availableCategories: List<String>): String {
        return "You are an on-device personal finance assistant. Classify the transaction titled \"$title\" into exactly one of these categories: ${availableCategories.joinToString(", ")}.\nIMPORTANT: Remember that transfers and balances must strictly be categorised under 'Transfer' (including self-transfers, moving money between accounts, and ATM withdrawals).\nRespond with only the single category name and nothing else."
    }

    private suspend fun queryAiCoreModel(prompt: String): String? {
        return try {
            val clientClass = Class.forName("com.google.ai.edge.aicore.GenerativeModel")
            val constructor = clientClass.getConstructor(String::class.java)
            val modelInstance = constructor.newInstance("gemini-nano")
            val generateMethod = clientClass.getMethod("generateContent", String::class.java)
            val response = generateMethod.invoke(modelInstance, prompt)
            val textMethod = response?.javaClass?.getMethod("getText")
            textMethod?.invoke(response) as? String
        } catch (e: ClassNotFoundException) {
            Log.d(TAG, "AICore Edge SDK library not linked; returning null as required.")
            null
        } catch (e: Exception) {
            Log.d(TAG, "AICore execution unavailable: ${e.message}")
            null
        }
    }

    fun matchCategory(rawOutput: String, availableCategories: List<String>): String? {
        val clean = rawOutput.trim().removeSurrounding("\"", "\"").removeSurrounding("'", "'")
        return availableCategories.find { it.equals(clean, ignoreCase = true) }
            ?: availableCategories.find { clean.contains(it, ignoreCase = true) }
    }
}
