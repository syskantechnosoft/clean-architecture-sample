package features

import domain.UserRepository

class ListUsers(private val userRepo: UserRepository) {
    fun execute() = userRepo.findAll()
}