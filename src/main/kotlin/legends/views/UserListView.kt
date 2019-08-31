package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.UserModel

data class UserListView(
        @JsonProperty("users") val users: List<UserView>
)

fun List<UserModel>.toView(): UserListView {
    val views = map { UserView(it) }
    return UserListView(views)
}