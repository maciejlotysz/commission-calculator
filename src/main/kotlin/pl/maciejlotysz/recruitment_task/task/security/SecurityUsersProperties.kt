package pl.maciejlotysz.recruitment_task.task.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

@ConfigurationProperties("security")
class SecurityUsersProperties {
    lateinit var userCredentials: List<UserCredential>

    class UserCredential {
        lateinit var username: String
        lateinit var password: String

        fun toUserDetails(): UserDetails =
            User(
                username, password, listOf()
            )
    }
}
