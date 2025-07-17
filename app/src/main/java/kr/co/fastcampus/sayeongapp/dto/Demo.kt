package kr.co.fastcampus.sayeongapp.dto

data class Demo(
    val id: String,
    val name: String
)

data class UpdateDemoRequest(
    val name: String
)
