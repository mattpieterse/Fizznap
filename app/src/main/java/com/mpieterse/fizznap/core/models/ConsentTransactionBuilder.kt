package com.mpieterse.fizznap.core.models

import androidx.fragment.app.FragmentActivity
import com.mpieterse.fizznap.core.commands.ConsentTransactionCommand
import com.mpieterse.fizznap.core.utils.ConsentBundleTranslator

/**
 * Builds and configures a [ConsentTransactionCommand].
 *
 * Configuration options are provided using the chained builder functions
 * directly on the builder object or via [apply].
 *
 * **Examples:**
 *
 * ```
 * // Trigger the system permission requests
 * val request = ConsentTransactionBuilder(_).apply {
 *      delegateUiHost(_)
 *      requestBundles(_)
 * }.build()
 *
 * request.execute()
 * ```
 *
 * @throws IllegalArgumentException
 * @throws IllegalStateException
 */
class ConsentTransactionBuilder(
    private val caller: FragmentActivity
) {

    private var configuredHandler: ConsentUiHost? = null
    private var configuredBundles: List<ConsentBundle>? = null


// --- FluentAPI


    /**
     * Configures the UI delegate for the permission request.
     *
     * **Examples:**
     *
     * ```
     * ConsentTransactionBuilder().delegateUiHost(_)
     * ```
     *
     * ```
     * ConsentTransactionBuilder().apply {
     *      delegateUiHost(_)
     * }
     * ```
     *
     * @return [ConsentTransactionBuilder]
     */
    fun delegateUiHost(uiHost: ConsentUiHost): ConsentTransactionBuilder = apply {
        configuredHandler = uiHost
    }


    /**
     * Configures the bundles to be translated and requested.
     *
     * **Examples:**
     *
     * ```
     * ConsentTransactionBuilder().requestBundles(_)
     * ```
     *
     * ```
     * ConsentTransactionBuilder().apply {
     *      requestBundles(_)
     * }
     * ```
     *
     * @return [ConsentTransactionBuilder]
     */
    fun requestBundles(vararg values: ConsentBundle): ConsentTransactionBuilder = apply {
        configuredBundles = values.toList()
    }


    /**
     * Constructs and returns the [ConsentTransactionCommand].
     *
     * **Note:** This method is guarded from misconfigurations. Ensure that all
     * of the required configurations have been provided and that the necessary
     * exception handling has been implemented.
     *
     * @return [ConsentTransactionCommand]
     *
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     */
    fun build(): ConsentTransactionCommand {
        requireBundles()
        requireHandler()

        checkCallerIsAlive()

        return ConsentTransactionCommand(
            caller, configuredHandler!!, permissions = configuredBundles!!.flatMap {
                ConsentBundleTranslator.toAndroid(it).toList()
            }.toTypedArray()
        )
    }


// --- Internals


    /**
     * Throws if the caller is not in a valid state for the permission request.
     *
     * **Fail conditions:**
     *
     * - [FragmentActivity] caller lifecycle is `Finishing`.
     * - [FragmentActivity] caller lifecycle is `Destroyed`.
     *
     * @see FragmentActivity.isFinishing
     * @see FragmentActivity.isDestroyed
     * @see check
     *
     * @throws IllegalStateException
     */
    private fun checkCallerIsAlive() {
        check(!(caller.isFinishing || caller.isDestroyed)) {
            "Caller is not in a valid state for the permission request."
        }
    }


    /**
     * Throws if the required bundles are not provided or null.
     *
     * If this exception has been thrown, ensure that you have provided a
     * value to the builder using the chained [requestBundles] function during
     * its construction.
     *
     * **Fail conditions:**
     *
     * - [List] of bundles is null and/or has not been provided.
     * - [List] of bundles is empty.
     *
     * @see require
     *
     * @throws IllegalArgumentException
     */
    private fun requireBundles() = require(configuredBundles?.isNotEmpty() == true) {
        "The configuration for bundles was not provided or poorly constructed."
    }


    /**
     * Throws if the required handler is null.
     *
     * If this exception has been thrown, ensure that you have provided a
     * value to the builder using the chained [delegateUiHost] function during
     * its construction.
     *
     * **Fail conditions:**
     *
     * - [ConsentUiHost] delegate is null and/or has not been provided.
     *
     * @see require
     *
     * @throws IllegalArgumentException
     */
    private fun requireHandler() = require(configuredHandler != null) {
        "The configuration for handler was not provided or poorly constructed."
    }
}