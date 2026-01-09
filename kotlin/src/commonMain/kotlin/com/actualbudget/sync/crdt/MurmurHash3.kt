package com.actualbudget.sync.crdt

/**
 * MurmurHash3 implementation (32-bit).
 * Used for hashing timestamps in the merkle trie.
 */
object MurmurHash3 {
    private const val C1 = 0xcc9e2d51.toInt()
    private const val C2 = 0x1b873593

    fun hash32(data: String, seed: Int = 0): Int {
        val bytes = data.encodeToByteArray()
        return hash32(bytes, seed)
    }

    fun hash32(data: ByteArray, seed: Int = 0): Int {
        val len = data.size
        var h1 = seed
        val nblocks = len / 4

        // Body
        for (i in 0 until nblocks) {
            val i4 = i * 4
            var k1 = (data[i4].toInt() and 0xFF) or
                    ((data[i4 + 1].toInt() and 0xFF) shl 8) or
                    ((data[i4 + 2].toInt() and 0xFF) shl 16) or
                    ((data[i4 + 3].toInt() and 0xFF) shl 24)

            k1 *= C1
            k1 = rotl32(k1, 15)
            k1 *= C2

            h1 = h1 xor k1
            h1 = rotl32(h1, 13)
            h1 = h1 * 5 + 0xe6546b64.toInt()
        }

        // Tail
        val tail = nblocks * 4
        var k1 = 0

        when (len and 3) {
            3 -> {
                k1 = k1 xor ((data[tail + 2].toInt() and 0xFF) shl 16)
                k1 = k1 xor ((data[tail + 1].toInt() and 0xFF) shl 8)
                k1 = k1 xor (data[tail].toInt() and 0xFF)
                k1 *= C1
                k1 = rotl32(k1, 15)
                k1 *= C2
                h1 = h1 xor k1
            }
            2 -> {
                k1 = k1 xor ((data[tail + 1].toInt() and 0xFF) shl 8)
                k1 = k1 xor (data[tail].toInt() and 0xFF)
                k1 *= C1
                k1 = rotl32(k1, 15)
                k1 *= C2
                h1 = h1 xor k1
            }
            1 -> {
                k1 = k1 xor (data[tail].toInt() and 0xFF)
                k1 *= C1
                k1 = rotl32(k1, 15)
                k1 *= C2
                h1 = h1 xor k1
            }
        }

        // Finalization
        h1 = h1 xor len
        h1 = fmix32(h1)

        return h1
    }

    private fun rotl32(x: Int, r: Int): Int {
        return (x shl r) or (x ushr (32 - r))
    }

    private fun fmix32(h: Int): Int {
        var result = h
        result = result xor (result ushr 16)
        result *= 0x85ebca6b.toInt()
        result = result xor (result ushr 13)
        result *= 0xc2b2ae35.toInt()
        result = result xor (result ushr 16)
        return result
    }
}
