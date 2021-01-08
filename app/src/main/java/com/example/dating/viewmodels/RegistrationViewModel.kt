package com.example.dating.viewmodels

import androidx.lifecycle.ViewModel
import com.example.dating.models.UserModel

class RegistrationViewModel : ViewModel() {

    private val userModel = UserModel()

    fun getCurrentUser(): UserModel {
        return userModel
    }
}