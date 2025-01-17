package edu.vandy.simulator.utils

import edu.vandy.simulator.utils.Student.Type.Graduate
import edu.vandy.simulator.utils.Student.Type.Undergraduate

object Student {
    enum class Type {
        @JvmStatic
        Graduate,
        @JvmStatic
        Undergraduate
    }

    /**
     * TODO: (Graduate students) - remove "Undergraduate" from the set below.
     * TODO: (Undergraduate students) - remove "Graduate" from the set below.
     */
    @JvmStatic
    private var type = setOf(Graduate)

    @JvmStatic
    fun `is`(type: Type) = this.type.contains(type)

    @JvmStatic
    fun isGraduate() = `is`(Graduate)

    @JvmStatic
    fun isUndergraduate() = `is`(Undergraduate)
}