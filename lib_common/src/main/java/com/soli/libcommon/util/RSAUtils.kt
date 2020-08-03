package com.soli.libcommon.util

import android.util.Base64
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import kotlin.math.ceil

/**
 *
 * RSA 公钥、私钥加解密工具类
 * @author Soli
 * @Time 2020/8/3 14:31
 */
object RSAUtils {

    private val ciperLocal = ThreadLocal<Cipher>()
    private val keyFactoryLocal = ThreadLocal<KeyFactory>()

    private val forCipher: Cipher
        get() = ciperLocal.get() ?: synchronized(this) {
            ciperLocal.get() ?: Cipher.getInstance("RSA/ECB/PKCS1PADDING").also {
                ciperLocal.set(it)
            }
        }

    private val forKeyFactory: KeyFactory
        get() = keyFactoryLocal.get() ?: synchronized(this) {
            keyFactoryLocal.get() ?: KeyFactory.getInstance("RSA").also {
                keyFactoryLocal.set(it)
            }
        }

    /**
     *
     */
    private fun getRSAPublicKey(publicKey: String?): RSAPublicKey? {
        publicKey ?: return null

        val keySpec = X509EncodedKeySpec(Base64.decode(publicKey, Base64.DEFAULT))
        return forKeyFactory.generatePublic(keySpec) as RSAPublicKey
    }

    /**
     *
     */
    private fun getRSAPrivateKey(privateKey: String?): RSAPrivateKey? {

        privateKey ?: return null

        val keySpec = PKCS8EncodedKeySpec(Base64.decode(privateKey, Base64.DEFAULT))
        return forKeyFactory.generatePrivate(keySpec) as RSAPrivateKey
    }


    /**
     * 公钥分段加密
     * 1024位，加密输入的时候是117  输出是128
     */
    fun encryptDataByPublickey(text: String, publicKey: String): String? {
        try {
            val cipher = forCipher
            cipher.init(Cipher.ENCRYPT_MODE, getRSAPublicKey(publicKey))

            return Base64.encodeToString(doFinalData(cipher, text.toByteArray()), Base64.DEFAULT)
                .replace("\n", "")

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 私钥分段解密
     */
    fun decryptDataByPrivatekey(text: String, privateKey: String): String {
        val cipher = forCipher
        cipher.init(Cipher.DECRYPT_MODE, getRSAPrivateKey(privateKey))
        return String(doFinalData(cipher, Base64.decode(text, Base64.DEFAULT)))
    }

    /**
     * 最终进行数据分段加解密的地方
     */
    private fun doFinalData(cipher: Cipher, bytes: ByteArray): ByteArray {

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

        return output
    }

    /**
     * 公钥分段解密
     */
    fun decryptDataByPublickey(text: String, publicKey: String): String {
        val cipher = forCipher
        cipher.init(Cipher.DECRYPT_MODE, getRSAPublicKey(publicKey))
        return String(doFinalData(cipher, Base64.decode(text, Base64.DEFAULT)))
    }

    /**
     * 私钥分段加密
     */
    fun encryptDataByPrivatekey(text: String, privateKey: String): String? {
        try {
            val cipher = forCipher
            cipher.init(Cipher.ENCRYPT_MODE, getRSAPrivateKey(privateKey))
            return Base64.encodeToString(doFinalData(cipher, text.toByteArray()), Base64.DEFAULT)
                .replace("\n", "")

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}