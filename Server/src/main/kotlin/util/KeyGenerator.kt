package dev.efelleto.diogenes.server.util

import java.security.SecureRandom

object KeyGenerator {
    // Definimos os caracteres permitidos (Letras maiúsculas e Números)
    // Removemos caracteres confusos como '0' (zero) e 'O' (letra O), se quiser,
    // mas aqui deixei o padrão completo:
    private val CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    private val random = SecureRandom() // Mais seguro que o Random comum

    /**
     * Gera uma chave no formato: XXXXX-XXXXX-XXXXX-XXXXX
     */
    fun generate(): String {
        val keyLength = 20 // 4 blocos de 5 = 20 caracteres
        val sb = StringBuilder()

        for (i in 0 until keyLength) {
            val randomIndex = random.nextInt(CHARACTERS.length)
            sb.append(CHARACTERS[randomIndex])
        }

        // Divide em blocos de 5 e junta com "-"
        return sb.toString().chunked(5).joinToString("-")
    }
}