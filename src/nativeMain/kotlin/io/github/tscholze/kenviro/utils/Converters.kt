package io.github.tscholze.kenviro.utils


fun UByteArray.toInt(): Int {
    var result = 0
    var shift = 0
    for (byte in this) {
        result = result or (byte.toInt() shl shift)
        shift += 8
    }

    return result
}