package com.artijjaek.admin.dto.request

data class PatchPasswordRequest(val oldPassword: String, val newPassword: String)
