package com.example.myapplication.Domain

class Time {
    var Id = 0
    var Value: String? = null
    override fun toString(): String {
        return Value!!
    }
}