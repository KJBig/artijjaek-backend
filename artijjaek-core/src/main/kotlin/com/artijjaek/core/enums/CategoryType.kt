package com.artijjaek.core.enums


enum class CategoryType(val stringCategory: String) {
    DEV("개발"),
    DESIGN("디자인"),
    AI("AI"),
    ETC("기타");

    companion object {
        fun fromString(stringCategory: String): CategoryType =
            entries.find { it.stringCategory == stringCategory }
                ?: throw IllegalArgumentException("Unknown category: $stringCategory")

        fun getAllCategoryTypeString(): String {
            val sb = StringBuilder()
            sb.append("[")
            for (type in entries) {
                sb.append("\"").append(type.stringCategory).append("\"").append(", ")
            }
            sb.append("]")
            return sb.toString()
        }
    }
}