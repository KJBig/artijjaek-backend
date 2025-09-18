package com.artiting.core.enums


enum class CategoryType(val stringCategory: String) {
    BACKEND("백엔드"),
    FRONTEND("프론트엔드"),
    AOS("안드로이드"),
    IOS("iOS"),
    DESIGN("디자인"),
    PLAN("기획"),
    MARKETING("마케팅"),
    QA("QA"),
    AI("AI"),
    DATA("데이터"),
    SECURITY("보안"),
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