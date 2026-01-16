package com.nazam.dailytodo.core.time

/**
 * Donne "maintenant" en millisecondes.
 * KMP: commonMain = expect, chaque plateforme fait son actual.
 */
expect fun nowMillis(): Long
