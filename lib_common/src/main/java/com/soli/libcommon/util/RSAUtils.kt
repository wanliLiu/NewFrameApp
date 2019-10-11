package com.soli.libcommon.util

import android.util.Base64
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import kotlin.math.ceil

/**
 * @author Soli
 * @Time 2018/10/25 10:05
 */
object RSAUtils {

    /**
     * 公钥分段加密
     */
     fun encryptDataByPublickey(txt: String, publicKey: String): String {
        try {
            val publicBytes = Base64.decode(publicKey, Base64.DEFAULT)
            val keySpec = X509EncodedKeySpec(publicBytes)
            val keyFactory = KeyFactory.getInstance("RSA")
            val pubKey = keyFactory.generatePublic(keySpec)
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, pubKey)

            val bytes = txt.toByteArray()
            val blockSize = cipher.blockSize
            val outBlockSize = cipher.getOutputSize(bytes.size)
            val blocks: Int = ceil(bytes.size / blockSize.toDouble()).toInt()
            var output = ByteArray(blocks * outBlockSize)
            var outputSize = 0

            for (i in 0 until blocks) {
                val offset = i * blockSize
                val blockLength = blockSize.coerceAtMost(bytes.size - offset)
                val cryptoBlock = cipher.doFinal(bytes, offset, blockLength)
                System.arraycopy(cryptoBlock, 0, output, outputSize, cryptoBlock.size)
                outputSize += cryptoBlock.size
            }

            if (outputSize != output.size) {
                val tmp = output.copyOfRange(0, outputSize)
                output = tmp
            }

            return Base64.encodeToString(output, Base64.DEFAULT).replace("\n", "")

        } catch (e: Exception) {
            throw Exception(e.toString())
        }
    }

    /**
     *
     */
    private fun loadPrivateKey(privateKey: String): PrivateKey {
        val clear = Base64.decode(privateKey, Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(clear)
        val fact = KeyFactory.getInstance("RSA")
        val priv = fact.generatePrivate(keySpec)
        Arrays.fill(clear, 0.toByte())
        return priv
    }
    /**
     * 私钥分段解密
     */
    fun decryptDataByPrivatekey(text: String, privateKey: String): String {
        val encryptedBytes = Base64.decode(text,Base64.DEFAULT)
        val cipher1 = Cipher.getInstance("RSA/ECB/PKCS1PADDING")
        cipher1.init(Cipher.DECRYPT_MODE, loadPrivateKey(privateKey))

        val blockSize = cipher1.blockSize
        val outBlockSize = cipher1.getOutputSize(encryptedBytes.size)
        val blocks: Int = ceil(encryptedBytes.size / blockSize.toDouble()).toInt()
        var output = ByteArray(blocks * outBlockSize)
        var outputSize = 0

        for (i in 0 until blocks) {
            val offset = i * blockSize
            val blockLength = blockSize.coerceAtMost(encryptedBytes.size - offset)
            val cryptoBlock = cipher1.doFinal(encryptedBytes, offset, blockLength)
            System.arraycopy(cryptoBlock, 0, output, outputSize, cryptoBlock.size)
            outputSize += cryptoBlock.size
        }

        if (outputSize != output.size) {
            val tmp = output.copyOfRange(0, outputSize)
            output = tmp
        }

        return String(output)
    }

}